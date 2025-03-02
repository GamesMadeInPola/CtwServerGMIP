package net.craftersland.ctw.server.database;

import net.craftersland.ctw.server.CTW;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class DataHandler {
    private final CTW ctw;

    public DataHandler(final CTW ctw) {
        this.ctw = ctw;
    }

    public boolean saveCTWPlayer(@NotNull CTWPlayer ctwPlayer) {
        try {
            if (userExists(ctwPlayer.getUuid())) {
                return updateCTWPlayer(ctwPlayer);
            } else {
                return createCTWPlayer(ctwPlayer);
            }
        } catch (Exception e) {
            CTW.log.warning("Error guardando al usuario " + ctwPlayer.getUuid() + " en la base de datos: " + e.getMessage());
            return false;
        }
    }

    private boolean userExists(@NotNull UUID uuid) {
        try {
            return doesPlayerExist(uuid);
        } catch (Exception e) {
            ctw.getLogger().severe("No se pudo comprobar al usuario " + uuid + " en la base de datos: " + e.getMessage());
            return false;
        }
    }

    protected boolean doesPlayerExist(@NotNull UUID uuid) {

        boolean userExists = false;
        String sql = "SELECT `player_uuid` FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";

        try (Connection connection = ctw.getMysqlSetup().getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ctw.getLogger().info("Conexión abierta: " + connection);

            preparedStatement.setString(1, uuid.toString());

            try (ResultSet result = preparedStatement.executeQuery()) {
                userExists = result.next();
            }

        } catch (SQLException e) {
            ctw.getLogger().severe("Error: " + e.getMessage());
        }

        return userExists;
    }

    protected boolean createCTWPlayer(@NotNull CTWPlayer ctwPlayer) {

        boolean success = false;
        String sql = "INSERT INTO `" + this.ctw.getConfigHandler().getString("Database.TableName") +
                "`(`player_uuid`, `player_name`, `effects`, `score`, `total_kills`, `melee_kills`, `defense_kills`, `bow_kills`, `bow_distance_kill`, `wool_pickups`, `wool_placed`, `last_seen`) " + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ctw.getMysqlSetup().getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ctw.getLogger().info("Conexión abierta: " + connection);

            preparedStatement.setString(1, ctwPlayer.getUuid().toString());
            preparedStatement.setString(2, ctwPlayer.getName());
            preparedStatement.setString(3, ctwPlayer.getEffects());
            preparedStatement.setInt(4, ctwPlayer.getScore());
            preparedStatement.setInt(5, ctwPlayer.getTotalKills());
            preparedStatement.setInt(6, ctwPlayer.getDefenseKills());
            preparedStatement.setInt(7, ctwPlayer.getBowKills());
            preparedStatement.setInt(8, ctwPlayer.getBowDistanceKill());
            preparedStatement.setInt(9, ctwPlayer.getWoolPickups());
            preparedStatement.setInt(10, ctwPlayer.getWoolPlacements());
            preparedStatement.setString(11, String.valueOf(ctwPlayer.getLastSeen()));

            int affectedRows = preparedStatement.executeUpdate();
            success = affectedRows > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            ctw.getLogger().severe("El usuario con UUID" + ctwPlayer.getUuid() + " ya existe en la base de datos: " + e.getMessage());
        } catch (SQLException e) {
            ctw.getLogger().severe("Error insertando a un jugador en la base de datos: " + e.getMessage());
        }

        return success;
    }

    public boolean updateCTWPlayer(@NotNull CTWPlayer ctwPlayer) {

        String sql = "UPDATE `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` " +
                "SET `player_name` = ?, `effects` = ?, `score` = ?, `melee_kills` = ?, `defense_kills` = ?, `bow_kills` = ?, `bow_distance_kill` = ?, `wool_placed` = ?, `last_seen` = ? " +
                "WHERE `player_uuid` = ?";

        try (Connection connection = ctw.getMysqlSetup().getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, ctwPlayer.getName());
            preparedStatement.setString(2, ctwPlayer.getEffects());
            preparedStatement.setInt(3, ctwPlayer.getScore());
            preparedStatement.setInt(4, ctwPlayer.getTotalKills());
            preparedStatement.setInt(5, ctwPlayer.getDefenseKills());
            preparedStatement.setInt(6, ctwPlayer.getBowKills());
            preparedStatement.setInt(7, ctwPlayer.getBowDistanceKill());
            preparedStatement.setInt(8, ctwPlayer.getWoolPlacements());
            preparedStatement.setString(9, String.valueOf(ctwPlayer.getLastSeen()));

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            ctw.getLogger().severe("Error actualizando a un jugador en la base de datos: " + e.getMessage());
            return false;
        }
    }

    public Optional<CTWPlayer> getCTWPlayer(@NotNull UUID uuid){
        CTWPlayer ctwPlayer = new CTWPlayer(uuid, Bukkit.getPlayer(uuid).getName(), 0, 0, 0, 0, 0, 0, 0, 0);
        String sql = "SELECT * FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";

        try (Connection connection = ctw.getMysqlSetup().getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, uuid.toString());

            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    ctwPlayer.setName(result.getString("player_name"));
                    ctwPlayer.setEffects(result.getString("effects"));
                    ctwPlayer.setScore(result.getInt("score"));
                    ctwPlayer.setTotalKills(result.getInt("melee_kills"));
                    ctwPlayer.setDefenseKills(result.getInt("defense_kills"));
                    ctwPlayer.setMeleeKills(result.getInt("melee_kills"));
                    ctwPlayer.setBowKills(result.getInt("bow_kills"));
                    ctwPlayer.setBowDistanceKill(result.getInt("bow_distance_kill"));
                    ctwPlayer.setWoolPickups(result.getInt("wool_pickups"));
                    ctwPlayer.setWoolPlacements(result.getInt("wool_placed"));
                    ctwPlayer.setLastSeen(Long.parseLong(result.getString("last_seen")));
                }
            }

        } catch (SQLException e) {
            ctw.getLogger().severe("Error obteniendo a un jugador de la base de datos: " + e.getMessage());
        }

        return Optional.of(ctwPlayer);
    }

    /**
    public boolean hasAccount(final @NotNull Player player) {
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String sql = "SELECT `player_uuid` FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return false;
        } finally {
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            result.close();
            preparedUpdateStatement.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return false;
    }

    public boolean createAccount(final @NotNull Player player) {
        PreparedStatement preparedStatement = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String sql = "INSERT INTO `" + this.ctw.getConfigHandler().getString("Database.TableName") + "`(`player_uuid`, `player_name`, `score`, `melee_kills`, `bow_kills`, `bow_distance_kill`, `wool_placed`, `last_seen`) " + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = this.conn.prepareStatement(sql);
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, 0);
            preparedStatement.setString(8, String.valueOf(System.currentTimeMillis()));
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                preparedStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                preparedStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public boolean setScore(final Player player, final Integer score) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String updateSql = "UPDATE `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` " + "SET `score` = ?" + ", `player_name` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(updateSql);
            preparedUpdateStatement.setInt(1, score);
            preparedUpdateStatement.setString(2, player.getName());
            preparedUpdateStatement.setString(3, String.valueOf(System.currentTimeMillis()));
            preparedUpdateStatement.setString(4, player.getUniqueId().toString());
            preparedUpdateStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public Integer getScore(final Player player) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String sql = "SELECT `score` FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            if (result.next()) {
                return result.getInt("score");
            }
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        } finally {
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            result.close();
            preparedUpdateStatement.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public String getEffect(final Player player) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String sql = "SELECT `effects` FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            if (result.next()) {
                return result.getString("effects");
            }
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        } finally {
            try {
                result.close();
                preparedUpdateStatement.close();

            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            result.close();
            preparedUpdateStatement.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public boolean setKills(final Player player, final int meleeKills, final int bowKills) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String updateSql = "UPDATE `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` " + "SET `melee_kills` = ?" + ", `bow_kills` = ?" + ", `player_name` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(updateSql);
            preparedUpdateStatement.setInt(1, meleeKills);
            preparedUpdateStatement.setInt(2, bowKills);
            preparedUpdateStatement.setString(3, player.getName());
            preparedUpdateStatement.setString(4, String.valueOf(System.currentTimeMillis()));
            preparedUpdateStatement.setString(5, player.getUniqueId().toString());
            preparedUpdateStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public Integer[] getKills(final Player player) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String sql = "SELECT * FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            if (result.next()) {
                final int meleeKills = result.getInt("melee_kills");
                final int bowKills = result.getInt("bow_kills");
                return new Integer[]{meleeKills, bowKills};
            }
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        } finally {
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            result.close();
            preparedUpdateStatement.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public boolean setWoolsPlaced(final Player player, final Integer count) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String updateSql = "UPDATE `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` " + "SET `wool_placed` = ?" + ", `player_name` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(updateSql);
            preparedUpdateStatement.setInt(1, count);
            preparedUpdateStatement.setString(2, player.getName());
            preparedUpdateStatement.setString(3, String.valueOf(System.currentTimeMillis()));
            preparedUpdateStatement.setString(4, player.getUniqueId().toString());
            preparedUpdateStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public Integer getWoolsPlaced(final Player player) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String sql = "SELECT `wool_placed` FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            if (result.next()) {
                return result.getInt("wool_placed");
            }
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        } finally {
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            result.close();
            preparedUpdateStatement.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }

    public boolean setBowDistanceKill(final Player player, final Integer count) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String updateSql = "UPDATE `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` " + "SET `bow_distance_kill` = ?" + ", `player_name` = ?" + ", `last_seen` = ?" + " WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(updateSql);
            preparedUpdateStatement.setInt(1, count);
            preparedUpdateStatement.setString(2, player.getName());
            preparedUpdateStatement.setString(3, String.valueOf(System.currentTimeMillis()));
            preparedUpdateStatement.setString(4, player.getUniqueId().toString());
            preparedUpdateStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return false;
    }

    public Integer getBowDistanceKill(final Player player) {
        if (!this.hasAccount(player)) {
            this.createAccount(player);
        }
        PreparedStatement preparedUpdateStatement = null;
        ResultSet result = null;
        try {
            this.conn = this.ctw.getMysqlSetup().getConnection();
            final String sql = "SELECT `bow_distance_kill` FROM `" + this.ctw.getConfigHandler().getString("Database.TableName") + "` WHERE `player_uuid` = ? LIMIT 1";
            preparedUpdateStatement = this.conn.prepareStatement(sql);
            preparedUpdateStatement.setString(1, player.getUniqueId().toString());
            result = preparedUpdateStatement.executeQuery();
            if (result.next()) {
                return result.getInt("bow_distance_kill");
            }
        } catch (SQLException e) {
            CTW.log.warning("Error: " + e.getMessage());
            e.printStackTrace();
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        } finally {
            try {
                result.close();
                preparedUpdateStatement.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        try {
            result.close();
            preparedUpdateStatement.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return null;
    }
     */
}
