package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager {

    private final SkyWars plugin;
    private final Map<UUID, PlayerStats> statsCache;

    public StatsManager(SkyWars plugin) {
        this.plugin = plugin;
        this.statsCache = new ConcurrentHashMap<>();
        createTables();
    }

    private void createTables() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement ps = plugin.getCore().getMySQL().getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS skywars_stats (" +
                                "uuid VARCHAR(36) PRIMARY KEY, " +
                                "wins INT DEFAULT 0, " +
                                "losses INT DEFAULT 0, " +
                                "kills INT DEFAULT 0, " +
                                "deaths INT DEFAULT 0, " +
                                "games_played INT DEFAULT 0, " +
                                "INDEX idx_wins (wins), " +
                                "INDEX idx_kills (kills)" +
                                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
                );
                ps.executeUpdate();
                ps.close();
                plugin.getLogger().info("SkyWars Stats Tabelle erstellt/geprüft");
            } catch (Exception e) {
                plugin.getLogger().severe("Fehler beim Erstellen der Stats-Tabelle");
                e.printStackTrace();
            }
        });
    }

    public void loadStats(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement ps = plugin.getCore().getMySQL().getConnection().prepareStatement(
                        "SELECT * FROM skywars_stats WHERE uuid = ?"
                );
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    PlayerStats stats = new PlayerStats(
                            rs.getInt("wins"),
                            rs.getInt("losses"),
                            rs.getInt("kills"),
                            rs.getInt("deaths"),
                            rs.getInt("games_played")
                    );
                    statsCache.put(uuid, stats);
                } else {
                    // Erstelle neuen Eintrag
                    createStats(uuid);
                    statsCache.put(uuid, new PlayerStats(0, 0, 0, 0, 0));
                }

                rs.close();
                ps.close();
            } catch (Exception e) {
                plugin.getLogger().severe("Fehler beim Laden der Stats für " + uuid);
                e.printStackTrace();
            }
        });
    }

    private void createStats(UUID uuid) {
        try {
            PreparedStatement ps = plugin.getCore().getMySQL().getConnection().prepareStatement(
                    "INSERT INTO skywars_stats (uuid) VALUES (?)"
            );
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addWin(UUID uuid) {
        addStat(uuid, "wins", 1);
        addStat(uuid, "games_played", 1);
    }

    public void addLoss(UUID uuid) {
        addStat(uuid, "losses", 1);
        addStat(uuid, "games_played", 1);
    }

    public void addKills(UUID uuid, int amount) {
        addStat(uuid, "kills", amount);
    }

    public void addDeaths(UUID uuid, int amount) {
        addStat(uuid, "deaths", amount);
    }

    private void addStat(UUID uuid, String stat, int amount) {
        PlayerStats stats = statsCache.get(uuid);
        if (stats != null) {
            switch (stat) {
                case "wins": stats.wins += amount; break;
                case "losses": stats.losses += amount; break;
                case "kills": stats.kills += amount; break;
                case "deaths": stats.deaths += amount; break;
                case "games_played": stats.gamesPlayed += amount; break;
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement ps = plugin.getCore().getMySQL().getConnection().prepareStatement(
                        "UPDATE skywars_stats SET " + stat + " = " + stat + " + ? WHERE uuid = ?"
                );
                ps.setInt(1, amount);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void showStats(Player player, Player target) {
        PlayerStats stats = statsCache.get(target.getUniqueId());
        if (stats == null) {
            player.sendMessage(SkyWars.PREFIX + "§cStats nicht geladen!");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27, "§6§l" + target.getName() + " Stats");

        // Kopf
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(target.getName());
        skullMeta.setDisplayName("§6" + plugin.getCore().getRankManager().getRankColor(target) +
                target.getName());
        skull.setItemMeta(skullMeta);
        inv.setItem(4, skull);

        // Stats
        inv.setItem(10, createStatItem(Material.DIAMOND, "§aGewinne", stats.wins));
        inv.setItem(12, createStatItem(Material.REDSTONE, "§cNiederlagen", stats.losses));
        inv.setItem(14, createStatItem(Material.IRON_SWORD, "§eKills", stats.kills));
        inv.setItem(16, createStatItem(Material.SKULL_ITEM, "§7Tode", stats.deaths));

        double kd = stats.deaths > 0 ? (double) stats.kills / stats.deaths : stats.kills;
        inv.setItem(22, createStatItem(Material.PAPER, "§bK/D",
                String.format("%.2f", kd)));

        player.openInventory(inv);
    }

    private ItemStack createStatItem(Material mat, String name, int value) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("", "§7Wert: §e" + value));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createStatItem(Material mat, String name, String value) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("", "§7Wert: §e" + value));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getStatsItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lStatistiken §7(Rechtsklick)");
        List<String> lore = Arrays.asList(
                "",
                "§7Zeige deine SkyWars-Statistiken!",
                "",
                "§e» Rechtsklick zum Öffnen"
        );
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public PlayerStats getStats(UUID uuid) {
        return statsCache.get(uuid);
    }

    public static class PlayerStats {
        public int wins;
        public int losses;
        public int kills;
        public int deaths;
        public int gamesPlayed;

        public PlayerStats(int wins, int losses, int kills, int deaths, int gamesPlayed) {
            this.wins = wins;
            this.losses = losses;
            this.kills = kills;
            this.deaths = deaths;
            this.gamesPlayed = gamesPlayed;
        }
    }

    public int getWins(UUID uuid) {
        PlayerStats stats = statsCache.get(uuid);
        return stats != null ? stats.wins : 0;
    }

    public int getLosses(UUID uuid) {
        PlayerStats stats = statsCache.get(uuid);
        return stats != null ? stats.losses : 0;
    }

    public int getKills(UUID uuid) {
        PlayerStats stats = statsCache.get(uuid);
        return stats != null ? stats.kills : 0;
    }

    public int getDeaths(UUID uuid) {
        PlayerStats stats = statsCache.get(uuid);
        return stats != null ? stats.deaths : 0;
    }

    public int getGamesPlayed(UUID uuid) {
        PlayerStats stats = statsCache.get(uuid);
        return stats != null ? stats.gamesPlayed : 0;
    }

    public double getKD(UUID uuid) {
        PlayerStats stats = statsCache.get(uuid);
        if (stats == null) return 0.0;
        return stats.deaths > 0 ? (double) stats.kills / stats.deaths : stats.kills;
    }

    // Überladene showStats Methode für UUID statt Player
    public void showStats(Player viewer, UUID targetUUID) {
        PlayerStats stats = statsCache.get(targetUUID);
        if (stats == null) {
            viewer.sendMessage(SkyWars.PREFIX + "§cStats nicht geladen!");
            return;
        }

        // Name des Targets holen
        Player target = Bukkit.getPlayer(targetUUID);
        String targetName = target != null ? target.getName() : "Unbekannt";

        Inventory inv = Bukkit.createInventory(null, 27, "§6§l" + targetName + " Stats");

        // Kopf
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwner(targetName);
        if (target != null) {
            skullMeta.setDisplayName("§6" + plugin.getCore().getRankManager().getRankColor(target) + targetName);
        } else {
            skullMeta.setDisplayName("§6" + targetName);
        }
        skull.setItemMeta(skullMeta);
        inv.setItem(4, skull);

        // Stats
        inv.setItem(10, createStatItem(Material.DIAMOND, "§aGewinne", stats.wins));
        inv.setItem(12, createStatItem(Material.REDSTONE, "§cNiederlagen", stats.losses));
        inv.setItem(14, createStatItem(Material.IRON_SWORD, "§eKills", stats.kills));
        inv.setItem(16, createStatItem(Material.SKULL_ITEM, "§7Tode", stats.deaths));

        double kd = stats.deaths > 0 ? (double) stats.kills / stats.deaths : stats.kills;
        inv.setItem(22, createStatItem(Material.PAPER, "§bK/D", String.format("%.2f", kd)));

        viewer.openInventory(inv);
    }

    // Methode für saveStats (falls später benötigt, aktuell nicht genutzt)
    public void saveStats(UUID uuid) {
        // Stats werden bereits async durch addStat() gespeichert
        // Diese Methode kann leer bleiben oder für manuelles Speichern verwendet werden
        PlayerStats stats = statsCache.get(uuid);
        if (stats == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement ps = plugin.getCore().getMySQL().getConnection().prepareStatement(
                        "UPDATE skywars_stats SET wins = ?, losses = ?, kills = ?, deaths = ?, games_played = ? WHERE uuid = ?"
                );
                ps.setInt(1, stats.wins);
                ps.setInt(2, stats.losses);
                ps.setInt(3, stats.kills);
                ps.setInt(4, stats.deaths);
                ps.setInt(5, stats.gamesPlayed);
                ps.setString(6, uuid.toString());
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                plugin.getLogger().severe("Fehler beim Speichern der Stats für " + uuid);
                e.printStackTrace();
            }
        });
    }
}