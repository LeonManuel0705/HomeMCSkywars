package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VoteManager {

    private final SkyWars plugin;
    private final Map<String, Map<UUID, String>> votes; // category -> (player -> choice)

    public VoteManager(SkyWars plugin) {
        this.plugin = plugin;
        this.votes = new HashMap<>();
        votes.put("time", new HashMap<>());
        votes.put("weather", new HashMap<>());
        votes.put("chest", new HashMap<>());
    }

    public void openVoteMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6§lAbstimmung");

        // Zeit
        inv.setItem(10, createVoteItem(Material.WATCH, "§eZeit",
                player.hasPermission("skywars.vote.time")));

        // Wetter
        inv.setItem(12, createVoteItem(Material.WATER_BUCKET, "§bWetter",
                player.hasPermission("skywars.vote.weather")));

        // Chest-Füllung
        inv.setItem(14, createVoteItem(Material.CHEST, "§6Truhen",
                player.hasPermission("skywars.vote.chest")));

        player.openInventory(inv);
    }

    public void openTimeVote(Player player) {
        if (!player.hasPermission("skywars.vote.time")) {
            player.sendMessage(SkyWars.PREFIX + "§cDu benötigst §eVIP §cum für Zeit abzustimmen!");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§eZeit");

        inv.setItem(1, createItem(Material.DOUBLE_PLANT, "§eTag"));
        inv.setItem(3, createItem(Material.FEATHER, "§7Nacht"));
        inv.setItem(5, createItem(Material.GOLD_INGOT, "§6Sonnenaufgang"));
        inv.setItem(7, createItem(Material.REDSTONE, "§cSonnenuntergang"));

        player.openInventory(inv);
    }

    public void openWeatherVote(Player player) {
        if (!player.hasPermission("skywars.vote.weather")) {
            player.sendMessage(SkyWars.PREFIX + "§cDu benötigst §aMVP §cum für Wetter abzustimmen!");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§bWetter");

        inv.setItem(2, createItem(Material.DOUBLE_PLANT, "§aSonnig"));
        inv.setItem(4, createItem(Material.WATER_BUCKET, "§bRegen"));
        inv.setItem(6, createItem(Material.SNOW_BALL, "§fSchnee"));

        player.openInventory(inv);
    }

    public void openChestVote(Player player) {
        if (!player.hasPermission("skywars.vote.chest")) {
            player.sendMessage(SkyWars.PREFIX + "§cDu benötigst §dLegend §cum für Truhen abzustimmen!");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 9, "§6Truhen");

        inv.setItem(2, createItem(Material.STONE_SWORD, "§7Normal"));
        inv.setItem(4, createItem(Material.IRON_SWORD, "§6Gut"));
        inv.setItem(6, createItem(Material.DIAMOND_SWORD, "§bOP"));

        player.openInventory(inv);
    }

    public void vote(Player player, String category, String choice) {
        votes.get(category).put(player.getUniqueId(), choice);
        player.sendMessage(SkyWars.PREFIX + "§aDu hast für §e" + choice + " §aabgestimmt!");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.5f);
    }

    public void applyVotes() {
        World world = Bukkit.getWorlds().get(0);

        // Zeit
        String timeChoice = getMostVoted("time");
        if (timeChoice != null) {
            switch (timeChoice) {
                case "Tag":
                    world.setTime(1000);
                    break;
                case "Nacht":
                    world.setTime(13000);
                    break;
                case "Sonnenaufgang":
                    world.setTime(23000);
                    break;
                case "Sonnenuntergang":
                    world.setTime(12000);
                    break;
            }
        }

        // Wetter
        String weatherChoice = getMostVoted("weather");
        if (weatherChoice != null) {
            switch (weatherChoice) {
                case "Sonnig":
                    world.setStorm(false);
                    world.setThundering(false);
                    break;
                case "Regen":
                    world.setStorm(true);
                    world.setThundering(false);
                    break;
                case "Schnee":
                    world.setStorm(true);
                    world.setThundering(true);
                    break;
            }
        }

        // Chest-Level wird vom ChestManager verwendet
        String chestChoice = getMostVoted("chest");
        if (chestChoice != null) {
            plugin.getChestManager().setChestLevel(chestChoice);
        }

        votes.values().forEach(Map::clear);
    }

    private String getMostVoted(String category) {
        Map<UUID, String> categoryVotes = votes.get(category);
        if (categoryVotes.isEmpty()) return null;

        Map<String, Integer> count = new HashMap<>();
        for (String choice : categoryVotes.values()) {
            count.put(choice, count.getOrDefault(choice, 0) + 1);
        }

        return count.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public ItemStack getVoteItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lAbstimmung §7(Rechtsklick)");
        List<String> lore = Arrays.asList(
                "",
                "§7Stimme über die Spielbedingungen ab!",
                "",
                "§e» Rechtsklick zum Öffnen"
        );
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createVoteItem(Material mat, String name, boolean hasPermission) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (hasPermission) {
            lore.add("§a✔ Verfügbar");
            lore.add("§eKlicke zum Abstimmen!");
        } else {
            lore.add("§c✖ Keine Berechtigung");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList("", "§eKlicke zum Auswählen!"));
        item.setItemMeta(meta);
        return item;
    }
}