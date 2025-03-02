package net.craftersland.ctw.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.slikey.effectlib.EffectManager;
import io.puharesource.mc.titlemanager.api.v2.TitleManagerAPI;
import net.craftersland.ctw.server.achievements.*;
import net.craftersland.ctw.server.commands.*;
import net.craftersland.ctw.server.database.CTWPlayerRepository;
import net.craftersland.ctw.server.database.DataHandler;
import net.craftersland.ctw.server.database.MysqlSetup;
import net.craftersland.ctw.server.events.*;
import net.craftersland.ctw.server.events.protection.*;
import net.craftersland.ctw.server.game.*;
import net.craftersland.ctw.server.game.map.MapConfigHandler;
import net.craftersland.ctw.server.game.map.MapHandler;
import net.craftersland.ctw.server.kits.KitHandler;
import net.craftersland.ctw.server.kits.enchants.*;
import net.craftersland.ctw.server.kits.items.ItemKitHandler;
import net.craftersland.ctw.server.kits.items.KitConfigHandler;
import net.craftersland.ctw.server.score.*;
import net.craftersland.ctw.server.utils.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class CTW extends JavaPlugin implements PluginMessageListener {
    public static Logger log;
    public static SendCenteredMessage sendMessage;
    public static Economy economy;
    public static TitleManagerAPI tm;
    private static ConfigHandler cH;
    private static SoundHandler soH;
    private static KitConfigHandler kcH;
    private static WorldHandler wH;
    private static MapConfigHandler mcH;
    private static MapHandler mH;
    private static GameEngine ge;
    private static TeamHandler tH;
    private static ScoreboardHandler sH;
    private static NewScoreboard nsH;
    private static PlayerHandler pH;
    private static JoinMenu jm;
    private static StartupKit sk;
    private static WoolHandler woolH;
    private static TakenWools tw;
    private static MessageUtils mu;
    private static EffectUtils eu;
    private static EffectManager em;
    private static ProtectionHandler protH;
    private static Fireworks fw;
    private static SpectatorTask st;
    private static TeamVictoryHandler tvH;
    private static TabList tl;
    private static LanguageHandler lH;
    private static Tasks tsk;
    private static LobbyLink ll;
    private static MysqlSetup ms;
    private static DataHandler dH;
    private static CTWPlayerRepository ctwPlayerRepository;
    private static TeamScoreHandler tsH;
    private static TeamKillsHandler tkH;
    private static TeamWoolsCaptured twc;
    private static TeamDamageHandler tdH;
    private static EconomyHandler eH;
    private static WoolAchievementHandler waH;
    private static ShooterAchievementHandler saH;
    private static MeleeAchievementHandler maH;
    private static OverpoweredAchievementHandler oaH;
    private static DistanceAchievementHandler daH;
    private static KillStreakHandler ksH;
    private static LastDamageHandler ldH;
    private static KitHandler kH;
    private static SharpnessI sh;
    private static SharpnessII ssh;
    private static SharpnessIII sssh;
    private static SharpnessIV ssssh;
    private static PowerI pow;
    private static PowerII ppow;
    private static PowerIII pppow;
    private static PowerIV ppppow;
    private static KnockbackI kn;
    private static KnockbackII kkn;
    private static FireAspectI fa;
    private static FireAspectII ffa;
    private static PunchI pu;
    private static PunchII ppu;
    private static FlameI fl;
    private static InfinityI inf;
    private static ProtectionI prot;
    private static ProtectionII pprot;
    private static ProtectionIII ppprot;
    private static ProtectionIV pppprot;
    private static ThornsI tho;
    private static ThornsII ttho;
    private static FeatherFallingI fafa;
    private static FeatherFallingII ffafa;
    private static Cleanup cl;
    private static ItemKitHandler ikH;
    private static DisableRecipe dr;
    private static ProtectedMoveHandler pmH;
    private static LobbyServersHandler lsH;
    private static WorldEditPlugin we;
    private static RestartHandler rH;
    private static Set<UUID> playersAlreadyEquipped;

    static {
        CTW.economy = null;
    }

    public boolean isEnabled;
    public boolean isDisabling;
    public boolean isValutEnabled;
    public boolean isAacEnabled;
    public String thisServerName;
    public String map;
    public HashMap<UUID, PlayerProjectile> playerProjectile = new HashMap<>();

    public CTW() {
        this.isEnabled = false;
        this.isDisabling = false;
        this.isValutEnabled = false;
        this.isAacEnabled = false;
    }

    public static Set<UUID> getPlayersAlreadyEquipped() {
        return playersAlreadyEquipped;
    }

    public void onEnable() {
        CTW.log = this.getLogger();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        this.getMcVersion();
        this.setupEconomy();
        this.checkWorldEditDependency();
        this.loadClass();
        this.loadEvent();
        this.loadCommand();

        tm = (TitleManagerAPI) Bukkit.getServer().getPluginManager().getPlugin("TitleManager");

        if (tm == null) {
            CTW.log.severe("TitleManager not found! Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            CTW.log.info("PlaceholderAPI found and is enabled.");
            new PAPI(this).register();
        } else {
            getLogger().severe("Could not find PlaceholderAPI! This plugin is required."); //
            Bukkit.getPluginManager().disablePlugin(this);
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lCTW &aSuccessfully enabled!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lCTW &7version &a" + this.getDescription().getVersion() + " &7by &aCraftersLand"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6&lCTW &7modified &aby reussy & Ak & DcRubik for &6GamesMadeInPola"));
        this.isEnabled = true;
    }

    public void loadCommand() {
        final Join joinCmd = new Join(this);
        final GlobalChat gChat = new GlobalChat(this);
        final Leave leave = new Leave(this);
        final Stats stats = new Stats(this);
        final Kits kit = new Kits(this);
        final Setup setup = new Setup(this);
        this.getCommand("join").setExecutor(joinCmd);
        this.getCommand("g").setExecutor(gChat);
        this.getCommand("leave").setExecutor(leave);
        this.getCommand("stats").setExecutor(stats);
        this.getCommand("shop").setExecutor(kit);
        this.getCommand("setup").setExecutor(setup);
    }

    public void loadEvent() {
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new Connecting(this), this);
        pm.registerEvents(new ServerPing(this), this);
        pm.registerEvents(new AntiSkyBridge(this), this);
        pm.registerEvents(new PlayerDataEvent(this), this);
        pm.registerEvents(new Disconnecting(this), this);
        pm.registerEvents(new InventoryClick(this), this);
        pm.registerEvents(new Respawning(this), this);
        pm.registerEvents(new PickupItem(this), this);
        pm.registerEvents(new ItemDrop(this), this);
        pm.registerEvents(new Death(this), this);
        pm.registerEvents(new onProjectile(this), this);
        pm.registerEvents(new PlayerFall(this), this);
        pm.registerEvents(new PlayerChat(this), this);
        pm.registerEvents(new BlockBreak(this), this);
        pm.registerEvents(new BlockDecayGrow(this), this);
        pm.registerEvents(new BlockExplode(this), this);
        pm.registerEvents(new BlockFadeForm(this), this);
        pm.registerEvents(new BlockPlace(this), this);
        pm.registerEvents(new BukkitUsage(this), this);
        pm.registerEvents(new EntityDamage(this), this);
        pm.registerEvents(new EntityInteract(this), this);
        pm.registerEvents(new FireSpread(this), this);
        pm.registerEvents(new Moving(this), this);
        pm.registerEvents(new PistonExtend(this), this);
        //pm.registerEvents(new InventoryClose(this), this);
        pm.registerEvents(new LiquidFlow(this), this);
        pm.registerEvents(new InventoryDrag(this), this);
        pm.registerEvents(new ShootBow(this), this);
        this.checkAacDependency(pm);

    }

    public void loadClass() {
        CTW.playersAlreadyEquipped = new HashSet<>();
        CTW.cH = new ConfigHandler(this);
        CTW.lH = new LanguageHandler(this);
        CTW.soH = new SoundHandler(this);
        CTW.kcH = new KitConfigHandler(this);
        CTW.wH = new WorldHandler(this);
        CTW.mcH = new MapConfigHandler(this);
        CTW.rH = new RestartHandler(this);
        CTW.mH = new MapHandler(this);
        CTW.ge = new GameEngine(this);
        CTW.tH = new TeamHandler(this);
        //CTW.sH = new ScoreboardHandler(this);
        CTW.nsH = new NewScoreboard(this);
        CTW.pH = new PlayerHandler(this);
        CTW.jm = new JoinMenu(this);
        CTW.sk = new StartupKit(this);
        CTW.woolH = new WoolHandler(this);
        CTW.tw = new TakenWools(this);
        CTW.mu = new MessageUtils(this);
        CTW.em = new EffectManager(this);
        CTW.eu = new EffectUtils(this);
        CTW.protH = new ProtectionHandler(this);
        CTW.fw = new Fireworks(this);
        CTW.st = new SpectatorTask(this);
        CTW.tvH = new TeamVictoryHandler(this);
        CTW.tl = new TabList(this);
        CTW.lsH = new LobbyServersHandler(this);
        CTW.sendMessage = new SendCenteredMessage();
        CTW.tsk = new Tasks(this);
        CTW.ll = new LobbyLink(this);
        setupDatabase();
        CTW.ctwPlayerRepository = new CTWPlayerRepository();
        CTW.tsH = new TeamScoreHandler(this);
        CTW.tkH = new TeamKillsHandler(this);
        CTW.twc = new TeamWoolsCaptured(this);
        CTW.tdH = new TeamDamageHandler(this);
        CTW.eH = new EconomyHandler(this);
        CTW.waH = new WoolAchievementHandler(this);
        CTW.saH = new ShooterAchievementHandler(this);
        CTW.maH = new MeleeAchievementHandler(this);
        CTW.oaH = new OverpoweredAchievementHandler(this);
        CTW.daH = new DistanceAchievementHandler(this);
        CTW.ksH = new KillStreakHandler(this);
        CTW.ldH = new LastDamageHandler();
        CTW.kH = new KitHandler(this);
        CTW.sh = new SharpnessI(this);
        CTW.ssh = new SharpnessII(this);
        CTW.sssh = new SharpnessIII(this);
        CTW.ssssh = new SharpnessIV(this);
        CTW.pow = new PowerI(this);
        CTW.ppow = new PowerII(this);
        CTW.pppow = new PowerIII(this);
        CTW.ppppow = new PowerIV(this);
        CTW.kn = new KnockbackI(this);
        CTW.kkn = new KnockbackII(this);
        CTW.fa = new FireAspectI(this);
        CTW.ffa = new FireAspectII(this);
        CTW.pu = new PunchI(this);
        CTW.ppu = new PunchII(this);
        CTW.fl = new FlameI(this);
        CTW.inf = new InfinityI(this);
        CTW.prot = new ProtectionI(this);
        CTW.pprot = new ProtectionII(this);
        CTW.ppprot = new ProtectionIII(this);
        CTW.pppprot = new ProtectionIV(this);
        CTW.tho = new ThornsI(this);
        CTW.ttho = new ThornsII(this);
        CTW.fafa = new FeatherFallingI(this);
        CTW.ffafa = new FeatherFallingII(this);
        CTW.cl = new Cleanup(this);
        CTW.ikH = new ItemKitHandler(this);
        CTW.dr = new DisableRecipe(this);
        CTW.pmH = new ProtectedMoveHandler(this);
    }

    public void setupDatabase(){
        CTW.ms = new MysqlSetup(this);

        if (!ms.connect()){
            CTW.log.severe("No se pudo conectar a la base de datos. Deshabilitando plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        ms.createTable();
        ms.maintenanceTask();

        CTW.dH = new DataHandler(this);
    }

    public void onDisable() {
        this.isDisabling = true;
        this.isEnabled = true;
        CTW.em.dispose();
        CTW.wH.unloadWorldsOnShutDown();
        this.getCleanup().cleanup();
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
        CTW.log.info("CTWserver has been disabled");
    }

    public ConfigHandler getConfigHandler() {
        return CTW.cH;
    }

    public WorldHandler getWorldHandler() {
        return CTW.wH;
    }

    public MapHandler getMapHandler() {
        return CTW.mH;
    }

    public MapConfigHandler getMapConfigHandler() {
        return CTW.mcH;
    }

    public SendCenteredMessage getSendMessage() {
        return CTW.sendMessage;
    }

    public GameEngine getGameEngine() {
        return CTW.ge;
    }

    public TeamHandler getTeamHandler() {
        return CTW.tH;
    }

    public NewScoreboard getNewScoreboardHandler() {
        return CTW.nsH;
    }

    public PlayerHandler getPlayerHandler() {
        return CTW.pH;
    }

    public JoinMenu getJoinMenu() {
        return CTW.jm;
    }

    public StartupKit getStartupKit() {
        return CTW.sk;
    }

    public WoolHandler getWoolHandler() {
        return CTW.woolH;
    }

    public TakenWools getTakenWools() {
        return CTW.tw;
    }

    public MessageUtils getMessageUtils() {
        return CTW.mu;
    }

    public EffectUtils getEffectUtils() {
        return CTW.eu;
    }

    public EffectManager getEffectManager() {
        return CTW.em;
    }

    public ProtectionHandler getProtectionHandler() {
        return CTW.protH;
    }

    public Fireworks getFireworks() {
        return CTW.fw;
    }

    public SpectatorTask getSpectatorTask() {
        return CTW.st;
    }

    public TeamVictoryHandler getTeamVictoryHandler() {
        return CTW.tvH;
    }

    public TabList getTabList() {
        return CTW.tl;
    }

    public Tasks getTasks() {
        return CTW.tsk;
    }

    public LobbyLink getLobbyLink() {
        return CTW.ll;
    }

    public MysqlSetup getMysqlSetup() {
        return CTW.ms;
    }

    public DataHandler getDataHandler() {
        return CTW.dH;
    }

    public CTWPlayerRepository getCTWPlayerRepository() {
        return CTW.ctwPlayerRepository;
    }

    public TeamScoreHandler getTeamScoreHandler() {
        return CTW.tsH;
    }

    public TeamKillsHandler getTeamKillsHandler() {
        return CTW.tkH;
    }

    public TeamWoolsCaptured getTeamWoolsCaptured() {
        return CTW.twc;
    }

    public TeamDamageHandler getTeamDamageHandler() {
        return CTW.tdH;
    }

    public EconomyHandler getEconomyHandler() {
        return CTW.eH;
    }

    public WoolAchievementHandler getWoolAchievementHandler() {
        return CTW.waH;
    }

    public ShooterAchievementHandler getShooterAchievementHandler() {
        return CTW.saH;
    }

    public MeleeAchievementHandler getMeleeAchievementHandler() {
        return CTW.maH;
    }

    public OverpoweredAchievementHandler getOverpoweredAchievementHandler() {
        return CTW.oaH;
    }

    public DistanceAchievementHandler getDistanceAchievementHandler() {
        return CTW.daH;
    }

    public KillStreakHandler getKillStreakHandler() {
        return CTW.ksH;
    }

    public KitHandler getKitHandler() {
        return CTW.kH;
    }

    public SharpnessI getEnchantSharpnessI() {
        return CTW.sh;
    }

    public SharpnessII getEnchantSharpnessII() {
        return CTW.ssh;
    }

    public SharpnessIII getEnchantSharpnessIII() {
        return CTW.sssh;
    }

    public SharpnessIV getEnchantSharpnessIV() {
        return CTW.ssssh;
    }

    public LastDamageHandler getLastDamageHandler() {
        return CTW.ldH;
    }

    public PowerI getEnchantPowerI() {
        return CTW.pow;
    }

    public PowerII getEnchantPowerII() {
        return CTW.ppow;
    }

    public PowerIII getEnchantPowerIII() {
        return CTW.pppow;
    }

    public PowerIV getEnchantPowerIV() {
        return CTW.ppppow;
    }

    public KnockbackI getEnchantKnockbackI() {
        return CTW.kn;
    }

    public KnockbackII getEnchantKnockbackII() {
        return CTW.kkn;
    }

    public FireAspectI getEnchantFireAspectI() {
        return CTW.fa;
    }

    public FireAspectII getEnchantFireAspectII() {
        return CTW.ffa;
    }

    public PunchI getEnchantPunchI() {
        return CTW.pu;
    }

    public PunchII getEnchantPunchII() {
        return CTW.ppu;
    }

    public FlameI getEnchantFlameI() {
        return CTW.fl;
    }

    public InfinityI getEnchantInfinityI() {
        return CTW.inf;
    }

    public ProtectionI getEnchantProtectionI() {
        return CTW.prot;
    }

    public ProtectionII getEnchantProtectionII() {
        return CTW.pprot;
    }

    public ProtectionIII getEnchantProtectionIII() {
        return CTW.ppprot;
    }

    public ProtectionIV getEnchantProtectionIV() {
        return CTW.pppprot;
    }

    public ThornsI getEnchantThornsI() {
        return CTW.tho;
    }

    public ThornsII getEnchantThornsII() {
        return CTW.ttho;
    }

    public FeatherFallingI getEnchantFeatherFallingI() {
        return CTW.fafa;
    }

    public FeatherFallingII getEnchantFeatherFallingII() {
        return CTW.ffafa;
    }

    public LanguageHandler getLanguageHandler() {
        return CTW.lH;
    }

    public KitConfigHandler getKitConfigHandler() {
        return CTW.kcH;
    }

    private Cleanup getCleanup() {
        return CTW.cl;
    }

    public ItemKitHandler getItemKitHandler() {
        return CTW.ikH;
    }

    public DisableRecipe getDisableRecipe() {
        return CTW.dr;
    }

    public ProtectedMoveHandler getProtectedMoveHandler() {
        return CTW.pmH;
    }

    public LobbyServersHandler getLobbyServersHandler() {
        return CTW.lsH;
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return CTW.we;
    }

    public SoundHandler getSoundHandler() {
        return CTW.soH;
    }

    public RestartHandler getRestartHandler() {
        return CTW.rH;
    }

    public void onPluginMessageReceived(final @NotNull String channel, final Player player, final byte[] message) {
        if (channel.equals("BungeeCord")) {
            final ByteArrayDataInput in = ByteStreams.newDataInput(message);
            final String subchannel = in.readUTF();
            if (subchannel.equals("GetServer")) {
                this.thisServerName = in.readUTF();
            } else if (subchannel.equals("ServerIP")) {
                final String serverName = in.readUTF();
                final String ip = in.readUTF();
                final short port = in.readShort();
                this.getLobbyServersHandler().saveLobbyAddresses(serverName, ip + ":" + port);
            }
        }
    }

    private void setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            CTW.log.info("Vault dependency detected.");
            RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                CTW.economy = economyProvider.getProvider();
                CTW.log.info("Economy system detected: " + economyProvider.getProvider().getName());
                this.isValutEnabled = true;
            } else {
                CTW.log.severe("Could not find any economy system!");
            }
        } else {
            CTW.log.severe("Vault dependency could not found!");
        }
    }

    private void checkWorldEditDependency() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
            CTW.log.info("WorldEdit dependency detected.");
            CTW.we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        } else {
            CTW.log.severe("WorldEdit dependency could not be found!");
        }
    }

    private void checkAacDependency(final PluginManager pm) {
        if (this.getConfigHandler().getBoolean("Settings.AntiCheatIntegration.AAC")) {
            if (Bukkit.getPluginManager().getPlugin("AAC") != null) {
                CTW.log.info("AAC AntiCheat detected and integration active.");
                pm.registerEvents(new AacIntegration(this), this);
                this.isAacEnabled = true;
            } else {
                CTW.log.severe("AAC AntiCheat could not found! Integration disabled!");
            }
        }
    }

    private void getMcVersion() {
        final String[] serverVersion = Bukkit.getBukkitVersion().split("-");
        final String version = serverVersion[0];
        if (!version.matches("1.8.8")) {
            CTW.log.severe("Incompatible server version detected: " + version + " This plugin is for spigot 1.8.8, install the plugin for this server version!");
        }
    }
}
