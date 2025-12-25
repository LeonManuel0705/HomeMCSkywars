package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitManager {

    private final SkyWars plugin;
    private final Map<String, Kit> kits;

    public KitManager(SkyWars plugin) {
        this.plugin = plugin;
        this.kits = new HashMap<>();
        loadKits();
    }

    private void loadKits() {
        // Default Kit (für alle)
        kits.put("default", new Kit("default", "§7Standard", Material.STONE_SWORD,
                "skywars.kit.default", new ArrayList<>()));

        // Warrior Kit
        List<ItemStack> warriorItems = Arrays.asList(
                createItem(Material.STONE_SWORD, 1, 0, "§cKrieger Schwert", null),
                createItem(Material.CHAINMAIL_CHESTPLATE, 1, 0, null, null),
                createItem(Material.CHAINMAIL_LEGGINGS, 1, 0, null, null)
        );
        kits.put("warrior", new Kit("warrior", "§cKrieger", Material.IRON_SWORD,
                "skywars.kit.warrior", warriorItems));

        // Archer Kit
        List<ItemStack> archerItems = Arrays.asList(
                createItem(Material.BOW, 1, 0, "§aBogen", null),
                createItem(Material.ARROW, 16, 0, null, null),
                createItem(Material.LEATHER_CHESTPLATE, 1, 0, null, null)
        );
        kits.put("archer", new Kit("archer", "§aSchütze", Material.BOW,
                "skywars.kit.archer", archerItems));

        // Tank Kit
        List<ItemStack> tankItems = Arrays.asList(
                createItem(Material.WOOD_SWORD, 1, 0, null, null),
                createItem(Material.IRON_CHESTPLATE, 1, 0, null, null),
                createItem(Material.IRON_LEGGINGS, 1, 0, null, null),
                createItem(Material.IRON_BOOTS, 1, 0, null, null)
        );
        kits.put("tank", new Kit("tank", "§9Tank", Material.IRON_CHESTPLATE,
                "skywars.kit.tank", tankItems));

        // Scout Kit
        List<ItemStack> scoutItems = Arrays.asList(
                createItem(Material.WOOD_SWORD, 1, 0, null, null),
                createItem(Material.LEATHER_BOOTS, 1, 0, "§eSchnelle Stiefel",
                        Collections.singletonMap(Enchantment.PROTECTION_FALL, 2))
        );
        kits.put("scout", new Kit("scout", "§eKundschafter", Material.LEATHER_BOOTS,
                "skywars.kit.scout", scoutItems));

        // Miner Kit
        List<ItemStack> minerItems = Arrays.asList(
                createItem(Material.STONE_PICKAXE, 1, 0, null, null),
                createItem(Material.STONE_AXE, 1, 0, null, null),
                createItem(Material.WOOD, 16, 0, null, null)
        );
        kits.put("miner", new Kit("miner", "§6Bergarbeiter", Material.STONE_PICKAXE,
                "skywars.kit.miner", minerItems));

        // Alchemist Kit (VIP+)
        List<ItemStack> alchemistItems = Arrays.asList(
                createItem(Material.WOOD_SWORD, 1, 0, null, null),
                createItem(Material.POTION, 1, 16389, "§dSchnelligkeitstrank", null), // Speed II
                createItem(Material.POTION, 1, 16385, "§cHeilungstrank", null) // Instant Health
        );
        kits.put("alchemist", new Kit("alchemist", "§dAlchemist", Material.POTION,
                "homemc.vip", alchemistItems));

        // Pyro Kit (MVP+)
        List<ItemStack> pyroItems = Arrays.asList(
                createItem(Material.FLINT_AND_STEEL, 1, 0, null, null),
                createItem(Material.LAVA_BUCKET, 1, 0, null, null),
                createItem(Material.FIRE, 8, 0, null, null)
        );
        kits.put("pyro", new Kit("pyro", "§6Pyromane", Material.FLINT_AND_STEEL,
                "homemc.mvp", pyroItems));

        // Knight Kit (Legend+)
        List<ItemStack> knightItems = Arrays.asList(
                createItem(Material.IRON_SWORD, 1, 0, "§bRitter Schwert", null),
                createItem(Material.DIAMOND_CHESTPLATE, 1, 0, null, null),
                createItem(Material.IRON_LEGGINGS, 1, 0, null, null),
                createItem(Material.IRON_BOOTS, 1, 0, null, null)
        );
        kits.put("knight", new Kit("knight", "§bRitter", Material.DIAMOND_SWORD,
                "homemc.legend", knightItems));
    }

    public void openKitSelector(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§6§lKit Auswahl");

        int slot = 10;
        for (Kit kit : kits.values()) {
            ItemStack item = new ItemStack(kit.getIcon());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(kit.getDisplayName());

            List<String> lore = new ArrayList<>();
            lore.add("");

            if (player.hasPermission(kit.getPermission())) {
                lore.add("§a✔ Verfügbar");
                lore.add("");
                lore.add("§eKlicke zum Auswählen!");
            } else {
                lore.add("§c✖ Nicht verfügbar");
                lore.add("");
                lore.add("§7Benötigt: §c" + getRequiredRank(kit.getPermission()));
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

            inv.setItem(slot, item);
            slot++;

            if (slot == 17) slot = 19;
        }

        player.openInventory(inv);
    }

    public void giveKit(Player player, String kitName) {
        Kit kit = kits.get(kitName);
        if (kit == null) {
            kit = kits.get("default");
        }

        if (!player.hasPermission(kit.getPermission())) {
            kit = kits.get("default");
        }

        for (ItemStack item : kit.getItems()) {
            if (item.getType().name().contains("CHESTPLATE")) {
                player.getInventory().setChestplate(item);
            } else if (item.getType().name().contains("LEGGINGS")) {
                player.getInventory().setLeggings(item);
            } else if (item.getType().name().contains("BOOTS")) {
                player.getInventory().setBoots(item);
            } else if (item.getType().name().contains("HELMET")) {
                player.getInventory().setHelmet(item);
            } else {
                player.getInventory().addItem(item);
            }
        }
    }

    public ItemStack getKitSelectorItem() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6§lKit Auswahl §7(Rechtsklick)");
        List<String> lore = Arrays.asList(
                "",
                "§7Wähle dein Kit für die Runde!",
                "",
                "§e» Rechtsklick zum Öffnen"
        );
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createItem(Material mat, int amount, int data, String name,
                                 Map<Enchantment, Integer> enchants) {
        ItemStack item = new ItemStack(mat, amount, (short) data);
        ItemMeta meta = item.getItemMeta();

        if (name != null) {
            meta.setDisplayName(name);
        }

        if (enchants != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private String getRequiredRank(String permission) {
        if (permission.contains("legend")) return "§dLegend";
        if (permission.contains("mvp")) return "§aMVP";
        if (permission.contains("vip")) return "§6VIP";
        return "§7Spieler";
    }

    public Kit getKit(String name) {
        return kits.get(name);
    }

    public Collection<Kit> getKits() {
        return kits.values();
    }

    public static class Kit {
        private final String id;
        private final String displayName;
        private final Material icon;
        private final String permission;
        private final List<ItemStack> items;

        public Kit(String id, String displayName, Material icon, String permission,
                   List<ItemStack> items) {
            this.id = id;
            this.displayName = displayName;
            this.icon = icon;
            this.permission = permission;
            this.items = items;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public Material getIcon() { return icon; }
        public String getPermission() { return permission; }
        public List<ItemStack> getItems() { return new ArrayList<>(items); }
    }
}