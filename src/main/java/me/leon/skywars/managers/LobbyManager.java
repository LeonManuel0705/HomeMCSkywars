package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LobbyManager {

    private final SkyWars plugin;
    private final Set<UUID> playersInLobby;
    private BukkitRunnable updateTask;

    public LobbyManager(SkyWars plugin) {
        this.plugin = plugin;
        this.playersInLobby = new HashSet<>();
        startUpdateTask();
    }

    public void addToLobby(Player player) {
        playersInLobby.add(player.getUniqueId());

        // Spieler Setup
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setFlying(false);

        if (player.hasPermission("homemc.fly")) {
            player.setAllowFlight(true);
        } else {
            player.setAllowFlight(false);
        }

        // Zum Lobby Spawn teleportieren
        Location lobbySpawn = plugin.getMapManager().getLobbySpawn();
        if (lobbySpawn != null) {
            player.teleport(lobbySpawn);
        }

        // AutoNick aktivieren wenn vorhanden
        if (plugin.getCore().getNickManager().hasAutoNick(player.getUniqueId())) {
            if (!plugin.getCore().getNickManager().isNicked(player.getUniqueId())) {
                plugin.getCore().getNickManager().nickPlayer(player);
            }
        }

        // Lobby Items geben
        giveLobbyItems(player);

        // Scoreboard & Tablist updaten
        updateScoreboard(player);
        updateTablist(player);
    }

    public void removeFromLobby(UUID uuid) {
        playersInLobby.remove(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

            // AutoNick deaktivieren
            if (plugin.getCore().getNickManager().isNicked(uuid)) {
                plugin.getCore().getNickManager().unnickPlayer(player);
            }
        }
    }

    public void giveLobbyItems(Player player) {
        // Spielen Item (Slot 0)
        ItemStack play = new ItemStack(Material.COMPASS);
        ItemMeta playMeta = play.getItemMeta();
        playMeta.setDisplayName("§a§lSpielen §7(Rechtsklick)");
        playMeta.setLore(Arrays.asList("§7Tritt einem Spiel bei!"));
        play.setItemMeta(playMeta);

        // Stats Item (Slot 4)
        ItemStack stats = new ItemStack(Material.PAPER);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName("§6§lStatistiken §7(Rechtsklick)");
        statsMeta.setLore(Arrays.asList("§7Zeige deine Stats an!"));
        stats.setItemMeta(statsMeta);

        // Zurück zur Lobby Item (Slot 8)
        ItemStack leave = new ItemStack(Material.INK_SACK, 1, (short) 1);
        ItemMeta leaveMeta = leave.getItemMeta();
        leaveMeta.setDisplayName("§c§lZurück zur Lobby §7(Rechtsklick)");
        leaveMeta.setLore(Arrays.asList("§7Kehre zur Hauptlobby zurück!"));
        leave.setItemMeta(leaveMeta);

        player.getInventory().setItem(0, play);
        player.getInventory().setItem(4, stats);
        player.getInventory().setItem(8, leave);
    }

    private void startUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : new HashSet<>(playersInLobby)) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        updateScoreboard(player);
                        updateTablist(player);
                    } else {
                        playersInLobby.remove(uuid);
                    }
                }
            }
        };
        updateTask.runTaskTimer(plugin, 20L, 20L);
    }

    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }

    private void updateScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("lobby", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§2§lSKYWARS");

        int line = 11;

        setScore(obj, "§1", line--);
        setScore(obj, "§fOnline: §a" + playersInLobby.size(), line--);
        setScore(obj, "§2", line--);

        // Game Status anzeigen
        if (plugin.getGameManager().getCurrentGame() != null) {
            GameState state = plugin.getGameManager().getCurrentGame().getState();
            String status = getGameStatus(state);
            setScore(obj, "§fStatus: " + status, line--);
        } else {
            setScore(obj, "§fStatus: §cKein Spiel", line--);
        }

        setScore(obj, "§3", line--);

        // Player Stats
        int kills = plugin.getStatsManager().getKills(player.getUniqueId());
        int deaths = plugin.getStatsManager().getDeaths(player.getUniqueId());
        int wins = plugin.getStatsManager().getWins(player.getUniqueId());

        setScore(obj, "§fKills: §e" + kills, line--);
        setScore(obj, "§fDeaths: §e" + deaths, line--);
        setScore(obj, "§fWins: §e" + wins, line--);
        setScore(obj, "§4", line--);

        setScore(obj, "§eplay.homemc.org", line--);

        player.setScoreboard(board);
    }

    private String getGameStatus(GameState state) {
        switch (state) {
            case LOBBY:
                return "§eWartend";
            case STARTING:
                return "§aStartet...";
            case INGAME:
                return "§cIngame";
            case ENDING:
                return "§6Ending";
            case RESTARTING:
                return "§4Restart";
            default:
                return "§7Unbekannt";
        }
    }

    private void setScore(Objective obj, String text, int score) {
        obj.getScore(text).setScore(score);
    }

    private void updateTablist(Player player) {
        String header = "\n§2§lSKYWARS\n§7Willkommen auf §aHomeMC§7!\n";
        String footer = "\n§7Online: §a" + playersInLobby.size() + "\n§eplay.homemc.org\n";

        try {
            String version = getServerVersion();

            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object handle = craftPlayerClass.getMethod("getHandle").invoke(craftPlayer);
            Object connection = handle.getClass().getField("playerConnection").get(handle);

            Class<?> chatSerializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutPlayerListHeaderFooter");

            Object headerComponent = chatSerializer.getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + header.replace("\"", "\\\"") + "\"}");
            Object footerComponent = chatSerializer.getMethod("a", String.class)
                    .invoke(null, "{\"text\":\"" + footer.replace("\"", "\\\"") + "\"}");

            Object packet = packetClass.getConstructor().newInstance();

            java.lang.reflect.Field a = packetClass.getDeclaredField("a");
            java.lang.reflect.Field b = packetClass.getDeclaredField("b");
            a.setAccessible(true);
            b.setAccessible(true);
            a.set(packet, headerComponent);
            b.set(packet, footerComponent);

            connection.getClass().getMethod("sendPacket",
                            Class.forName("net.minecraft.server." + version + ".Packet"))
                    .invoke(connection, packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getServerVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public void sendToMainLobby(Player player) {
        try {
            com.google.common.io.ByteArrayDataOutput out = com.google.common.io.ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF("lobby");
            player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
        } catch (Exception e) {
            player.sendMessage(SkyWars.PREFIX + "§cFehler beim Verbinden zur Lobby!");
            e.printStackTrace();
        }
    }

    public boolean isInLobby(UUID uuid) {
        return playersInLobby.contains(uuid);
    }

    public Set<UUID> getPlayersInLobby() {
        return new HashSet<>(playersInLobby);
    }
}