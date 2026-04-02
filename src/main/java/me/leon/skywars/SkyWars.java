package me.leon.skywars;

import me.leon.core.Core;
import me.leon.core.managers.RankManager;
import me.leon.skywars.commands.*;
import me.leon.skywars.listeners.*;
import me.leon.skywars.managers.*;
import me.leon.skywars.utils.LocationSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SkyWars extends JavaPlugin implements PluginMessageListener {

    private static SkyWars instance;
    private Core core;

    private GameManager gameManager;
    private MapManager mapManager;
    private KitManager kitManager;
    private StatsManager statsManager;
    private VoteManager voteManager;
    private SpectatorManager spectatorManager;
    private ChestManager chestManager;
    private LobbyManager lobbyManager;
    private ArenaManager arenaManager;

    public static final String PREFIX = "§2§lSkyWars §8» §7";

    @Override
    public void onEnable() {
        instance = this;

        this.core = Core.getInstance();
        if (core == null) {
            getLogger().severe("Core Plugin nicht gefunden! Deaktiviere SkyWars...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        LocationSerializer.init(this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        this.arenaManager = new ArenaManager(this);
        this.mapManager = new MapManager(this);
        this.kitManager = new KitManager(this);
        this.statsManager = new StatsManager(this);
        this.chestManager = new ChestManager(this);
        this.voteManager = new VoteManager(this);
        this.spectatorManager = new SpectatorManager(this);
        this.lobbyManager = new LobbyManager(this);
        this.gameManager = new GameManager(this);

        registerCommand("start", new StartCommand(this));
        registerCommand("forcestart", new ForceStartCommand(this));
        registerCommand("stop", new StopCommand(this));
        registerCommand("setlobby", new SetLobbyCommand(this));
        registerCommand("addspawn", new AddSpawnCommand(this));
        registerCommand("removespawn", new RemoveSpawnCommand(this));
        registerCommand("listspawns", new ListSpawnsCommand(this));
        registerCommand("swstats", new StatsCommand(this));
        registerCommand("swkit", new KitCommand(this));
        registerCommand("build", new BuildCommand(this));
        registerCommand("arena", new ArenaCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPickupItemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WeatherChangeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChestOpenListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyProtectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new LobbyInteractListener(this), this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "=================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "SkyWars Plugin aktiviert!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Version: " + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Integration: Core v" + core.getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "=================================");
    }

    @Override
    public void onDisable() {
        if (lobbyManager != null) {
            lobbyManager.stopUpdateTask();
        }

        if (gameManager != null) {
            gameManager.shutdown();
        }

        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "SkyWars Plugin deaktiviert!");
    }

    @Override
    public void onPluginMessageReceived(String channel, org.bukkit.entity.Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        org.bukkit.command.PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        } else {
            getLogger().warning("Command '" + name + "' konnte nicht registriert werden!");
        }
    }

    public static SkyWars getInstance() {
        return instance;
    }

    public Core getCore() {
        return core;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public KitManager getKitManager() {
        return kitManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public VoteManager getVoteManager() {
        return voteManager;
    }

    public SpectatorManager getSpectatorManager() {
        return spectatorManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public ChestManager getChestManager() {
        return chestManager;
    }

    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }

    public RankManager getRankManager() {
        return core.getRankManager();
    }
}