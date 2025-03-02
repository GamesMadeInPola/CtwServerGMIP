package net.craftersland.ctw.server.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.craftersland.ctw.server.CTW;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MysqlSetup {
    private final CTW ctw;
    private HikariDataSource dataSource;
    private final String host;
    private final String port;
    private final String databaseName;
    private final String tableName;
    private final String user;
    private final String password;
    private final boolean ssl;
    private final int maxPoolSize;
    private final long maxLifetime;

    public MysqlSetup(final CTW ctw) {
        this.ctw = ctw;

        this.host = this.ctw.getConfigHandler().getString("Database.mysql.host");
        this.port = this.ctw.getConfigHandler().getString("Database.mysql.port");
        this.databaseName = this.ctw.getConfigHandler().getString("Database.mysql.databaseName");
        this.tableName = this.ctw.getConfigHandler().getString("Database.TableName");
        this.user = this.ctw.getConfigHandler().getString("Database.mysql.user");
        this.password = this.ctw.getConfigHandler().getString("Database.mysql.password");
        this.ssl = this.ctw.getConfigHandler().getBoolean("Database.mysql.ssl");
        this.maxPoolSize = this.ctw.getConfigHandler().getInteger("Database.mysql.pool-settings.maximum-pool-size");
        this.maxLifetime = this.ctw.getConfigHandler().getInteger("Database.mysql.pool-settings.maximum-lifetime") * 1000L;
    }


    public boolean connect() {

        if (this.dataSource != null && !this.dataSource.isClosed()) {
            return true;
        }

        disconnect();

        try {
            HikariConfig config = getConfig();

            this.dataSource = new HikariDataSource(config);
            ctw.getLogger().info("Conexión a la base de datos establecida correctamente.");
            ctw.getLogger().info("Conexiones activas: " + dataSource.getHikariPoolMXBean().getActiveConnections());
        } catch (Exception e) {
            this.ctw.getLogger().severe("Error al conectar a la base de datos: " + e.getMessage());
            return false;
        }

        return true;
    }

    private @NotNull HikariConfig getConfig() {
        HikariConfig config = getHikariConfig();

        config.addDataSourceProperty("useSSL", ssl);

        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("encoding", "UTF-8");
        config.addDataSourceProperty("useUnicode", "true");

        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("jdbcCompliantTruncation", "false");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "275");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        // Recover if connection gets interrupted
        config.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        return config;
    }

    private @NotNull HikariConfig getHikariConfig() {
        HikariConfig config = new HikariConfig();

        //config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setPoolName("ctw-server-pool");

        config.setMaximumPoolSize(Math.min(maxPoolSize, 50));
        config.setMaxLifetime(maxLifetime);

        config.setLeakDetectionThreshold(5000);

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + databaseName);

        config.setUsername(user);
        config.setPassword(password);
        return config;
    }

    public void disconnect() {
        if (this.dataSource != null) {
            ctw.getLogger().info("Cerrando el pool de conexiones...");
            this.dataSource.close();
            this.dataSource = null;
        }
    }

    public void createTable() {
        try (Connection connection = this.dataSource.getConnection()) {
            if (!doesTableExist(connection, tableName)) {
                createPlayerTable(connection);
            }
        } catch (SQLException e) {
            this.ctw.getLogger().severe("Error al crear las tablas: " + e.getMessage());
        }
    }

    private boolean doesTableExist(@NotNull Connection connection, String tableName) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }

    private void createPlayerTable(@NotNull Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `" +
                tableName +
                "` (id INT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                "player_uuid CHAR(36) UNIQUE NOT NULL, " +
                "player_name VARCHAR(16) NOT NULL, " +
                "effects CHAR(50), " +
                "score INT(10) NOT NULL, " +
                "total_kills INT(10) NOT NULL, " +
                "melee_kills INT(10) NOT NULL, " +
                "defense_kills INT(10) NOT NULL, " +
                "bow_kills INT(10) NOT NULL, " +
                "bow_distance_kill INT(10) NOT NULL, " +
                "wool_pickups INT(10) NOT NULL, " +
                "wool_placed INT(10) NOT NULL, " +
                "last_seen CHAR(13) NOT NULL, " +
                "PRIMARY KEY(id));";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ctw.getLogger().info("Conexión abierta: " + connection);
            stmt.executeUpdate();
            this.ctw.getLogger().info("Tabla de usuarios creada o verificada.");
        }
    }

    public void maintenanceTask() {
        if (this.ctw.getConfigHandler().getBoolean("Database.removeOldUsers.enabled")) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, () -> {
                long inactivityDays = Long.parseLong(ctw.getConfigHandler().getString("Database.removeOldUsers.inactive"));
                long inactivityMillis = inactivityDays * 24L * 60L * 60L * 1000L;
                long inactiveTime = System.currentTimeMillis() - inactivityMillis;

                ctw.getLogger().info("Iniciando tarea de mantenimiento...");

                String sql = "DELETE FROM `" + tableName + "` WHERE `last_seen` < ?";
                try (Connection connection = dataSource.getConnection()) {
                    if (connection.isValid(5)) {
                        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                            preparedStatement.setLong(1, inactiveTime);
                            int rowsAffected = preparedStatement.executeUpdate();
                            ctw.getLogger().info("Mantenimiento completado. Filas eliminadas: " + rowsAffected);
                        }
                    } else {
                        ctw.getLogger().warning("Conexión no válida durante el mantenimiento.");
                    }
                } catch (SQLException e) {
                    ctw.getLogger().severe("Error durante el mantenimiento: " + e.getMessage());
                }
            }, 380L);
        }
    }


    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
