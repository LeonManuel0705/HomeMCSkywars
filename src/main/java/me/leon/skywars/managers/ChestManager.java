package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import me.leon.skywars.arena.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ChestManager {

    private final SkyWars plugin;
    private String chestLevel;
    private Arena currentArena;

    public ChestManager(SkyWars plugin) {
        this.plugin = plugin;
        this.chestLevel = "Normal";
    }

    public void setCurrentArena(Arena arena) {
        this.currentArena = arena;
    }

    public void fillChests() {
        if (currentArena == null) return;

        for (Location loc : currentArena.getChests()) {
            Block block = loc.getBlock();
            if (block.getType() == Material.CHEST) {
                Chest chest = (Chest) block.getState();
                fillChest(chest.getInventory());
            }
        }
    }

    public void refillChests() {
        fillChests();
    }

    private void fillChest(Inventory inv) {
        inv.clear();

        List<ItemStack> items = getItemsByLevel();
        Collections.shuffle(items);

        int itemCount;
        switch (chestLevel) {
            case "Normal":
                itemCount = 3 + new Random().nextInt(4);
                break;
            case "Gut":
                itemCount = 4 + new Random().nextInt(4);
                break;
            case "OP":
                itemCount = 5 + new Random().nextInt(5);
                break;
            default:
                itemCount = 3 + new Random().nextInt(4);
        }

        for (int i = 0; i < Math.min(itemCount, items.size()); i++) {
            int slot;
            do {
                slot = new Random().nextInt(27);
            } while (inv.getItem(slot) != null);

            inv.setItem(slot, items.get(i));
        }
    }

    private List<ItemStack> getItemsByLevel() {
        List<ItemStack> items = new ArrayList<>();
        Random random = new Random();

        switch (chestLevel) {
            case "Normal":
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.STONE_SWORD));
                if (random.nextInt(100) < 15) items.add(new ItemStack(Material.WOOD_AXE));
                if (random.nextInt(100) < 10) items.add(new ItemStack(Material.WOOD_SWORD));

                if (random.nextInt(100) < 15) {
                    items.add(new ItemStack(Material.BOW));
                    items.add(new ItemStack(Material.ARROW, 8 + random.nextInt(9)));
                }

                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.LEATHER_HELMET));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.LEATHER_CHESTPLATE));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.LEATHER_LEGGINGS));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.LEATHER_BOOTS));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                if (random.nextInt(100) < 15) items.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));

                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.APPLE, 2 + random.nextInt(3)));
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.BREAD, 2 + random.nextInt(3)));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.COOKED_CHICKEN, 1 + random.nextInt(3)));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.COOKED_BEEF, 1 + random.nextInt(2)));

                if (random.nextInt(100) < 50) items.add(new ItemStack(Material.WOOD, 8 + random.nextInt(17)));
                if (random.nextInt(100) < 45) items.add(new ItemStack(Material.COBBLESTONE, 16 + random.nextInt(17)));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.GLASS, 4 + random.nextInt(9)));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.SANDSTONE, 8 + random.nextInt(9)));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.WOOD_STEP, 4 + random.nextInt(5)));

                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.STONE_PICKAXE));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.WOOD_PICKAXE));
                if (random.nextInt(100) < 15) items.add(new ItemStack(Material.STONE_AXE));

                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.STICK, 4 + random.nextInt(5)));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.STRING, 2 + random.nextInt(3)));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.FEATHER, 2 + random.nextInt(3)));
                if (random.nextInt(100) < 15) items.add(new ItemStack(Material.FLINT, 1 + random.nextInt(2)));
                if (random.nextInt(100) < 10) items.add(new ItemStack(Material.SNOW_BALL, 8 + random.nextInt(9)));
                if (random.nextInt(100) < 10) items.add(new ItemStack(Material.EGG, 4 + random.nextInt(5)));

                if (random.nextInt(100) < 5) items.add(new ItemStack(Material.IRON_INGOT, 1 + random.nextInt(3)));
                if (random.nextInt(100) < 3) items.add(new ItemStack(Material.GOLD_INGOT, 1 + random.nextInt(2)));
                break;

            case "Gut":
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.IRON_SWORD));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.STONE_SWORD));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.IRON_AXE));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.STONE_AXE));

                if (random.nextInt(100) < 30) {
                    ItemStack bow = new ItemStack(Material.BOW);
                    if (random.nextInt(100) < 30) {
                        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                    }
                    items.add(bow);
                    items.add(new ItemStack(Material.ARROW, 16 + random.nextInt(17)));
                }

                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.IRON_HELMET));
                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.IRON_CHESTPLATE));
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.IRON_LEGGINGS));
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.IRON_BOOTS));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.CHAINMAIL_HELMET));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));

                if (random.nextInt(100) < 45) items.add(new ItemStack(Material.COOKED_BEEF, 3 + random.nextInt(4)));
                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.COOKED_CHICKEN, 3 + random.nextInt(4)));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.GOLDEN_APPLE, 1 + random.nextInt(2)));

                if (random.nextInt(100) < 55) items.add(new ItemStack(Material.WOOD, 16 + random.nextInt(17)));
                if (random.nextInt(100) < 50) items.add(new ItemStack(Material.COBBLESTONE, 24 + random.nextInt(25)));
                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.GLASS, 8 + random.nextInt(9)));
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.SANDSTONE, 12 + random.nextInt(13)));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.BRICK, 8 + random.nextInt(9)));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.OBSIDIAN, 2 + random.nextInt(3)));

                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.IRON_PICKAXE));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.IRON_AXE));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.IRON_SPADE));

                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.FLINT_AND_STEEL));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.FISHING_ROD));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.SHEARS));
                if (random.nextInt(100) < 15) items.add(new ItemStack(Material.WATER_BUCKET));
                if (random.nextInt(100) < 10) items.add(new ItemStack(Material.LAVA_BUCKET));

                if (random.nextInt(100) < 20) {
                    Potion speedPotion = new Potion(PotionType.SPEED, 1);
                    items.add(speedPotion.toItemStack(1));
                }
                if (random.nextInt(100) < 15) {
                    Potion healPotion = new Potion(PotionType.INSTANT_HEAL, 1);
                    items.add(healPotion.toItemStack(1));
                }

                if (random.nextInt(100) < 15) items.add(new ItemStack(Material.ENDER_PEARL, 1));
                if (random.nextInt(100) < 10) items.add(new ItemStack(Material.DIAMOND, 1 + random.nextInt(2)));
                if (random.nextInt(100) < 10) items.add(new ItemStack(Material.GOLD_INGOT, 2 + random.nextInt(3)));
                if (random.nextInt(100) < 8) items.add(new ItemStack(Material.TNT, 1 + random.nextInt(2)));
                break;

            case "OP":
                if (random.nextInt(100) < 50) {
                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                    if (random.nextInt(100) < 40) {
                        sword.addEnchantment(Enchantment.DAMAGE_ALL, 1 + random.nextInt(2));
                    }
                    items.add(sword);
                }
                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.IRON_SWORD));
                if (random.nextInt(100) < 35) {
                    ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
                    if (random.nextInt(100) < 30) {
                        axe.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    }
                    items.add(axe);
                }

                if (random.nextInt(100) < 45) {
                    ItemStack bow = new ItemStack(Material.BOW);
                    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1 + random.nextInt(2));
                    if (random.nextInt(100) < 30) {
                        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
                    }
                    items.add(bow);
                    items.add(new ItemStack(Material.ARROW, 32 + random.nextInt(33)));
                }

                if (random.nextInt(100) < 60) {
                    ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                    if (random.nextInt(100) < 40) {
                        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + random.nextInt(2));
                    }
                    items.add(helmet);
                }
                if (random.nextInt(100) < 60) {
                    ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    if (random.nextInt(100) < 40) {
                        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1 + random.nextInt(2));
                    }
                    items.add(chest);
                }
                if (random.nextInt(100) < 55) {
                    ItemStack legs = new ItemStack(Material.DIAMOND_LEGGINGS);
                    if (random.nextInt(100) < 40) {
                        legs.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    }
                    items.add(legs);
                }
                if (random.nextInt(100) < 55) {
                    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                    if (random.nextInt(100) < 40) {
                        boots.addEnchantment(Enchantment.PROTECTION_FALL, 2 + random.nextInt(2));
                    }
                    items.add(boots);
                }

                if (random.nextInt(100) < 50) items.add(new ItemStack(Material.GOLDEN_APPLE, 2 + random.nextInt(3)));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.COOKED_BEEF, 5 + random.nextInt(6)));
                if (random.nextInt(100) < 15) {
                    ItemStack gapple = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
                    items.add(gapple);
                }

                if (random.nextInt(100) < 60) items.add(new ItemStack(Material.WOOD, 32 + random.nextInt(33)));
                if (random.nextInt(100) < 55) items.add(new ItemStack(Material.COBBLESTONE, 48 + random.nextInt(49)));
                if (random.nextInt(100) < 45) items.add(new ItemStack(Material.GLASS, 16 + random.nextInt(17)));
                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.OBSIDIAN, 4 + random.nextInt(5)));
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.BRICK, 16 + random.nextInt(17)));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.GLOWSTONE, 8 + random.nextInt(9)));

                if (random.nextInt(100) < 40) {
                    ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
                    if (random.nextInt(100) < 30) {
                        pick.addEnchantment(Enchantment.DIG_SPEED, 1 + random.nextInt(2));
                    }
                    items.add(pick);
                }
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.DIAMOND_AXE));

                if (random.nextInt(100) < 45) items.add(new ItemStack(Material.ENDER_PEARL, 2 + random.nextInt(3)));
                if (random.nextInt(100) < 40) items.add(new ItemStack(Material.TNT, 4 + random.nextInt(5)));
                if (random.nextInt(100) < 35) items.add(new ItemStack(Material.FLINT_AND_STEEL));
                if (random.nextInt(100) < 30) items.add(new ItemStack(Material.LAVA_BUCKET));
                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.WATER_BUCKET));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.FISHING_ROD));

                if (random.nextInt(100) < 40) {
                    Potion speedPotion = new Potion(PotionType.SPEED, 2);
                    items.add(speedPotion.toItemStack(1));
                }
                if (random.nextInt(100) < 35) {
                    Potion healPotion = new Potion(PotionType.INSTANT_HEAL, 2);
                    items.add(healPotion.toItemStack(1));
                }
                if (random.nextInt(100) < 25) {
                    Potion regenPotion = new Potion(PotionType.REGEN, 1);
                    items.add(regenPotion.toItemStack(1));
                }
                if (random.nextInt(100) < 20) {
                    Potion strPotion = new Potion(PotionType.STRENGTH, 1);
                    items.add(strPotion.toItemStack(1));
                }

                if (random.nextInt(100) < 25) items.add(new ItemStack(Material.DIAMOND, 3 + random.nextInt(4)));
                if (random.nextInt(100) < 20) items.add(new ItemStack(Material.GOLD_INGOT, 4 + random.nextInt(5)));
                if (random.nextInt(100) < 15) items.add(new ItemStack(Material.EMERALD, 1 + random.nextInt(2)));
                if (random.nextInt(100) < 10) {
                    ItemStack exp = new ItemStack(Material.EXP_BOTTLE, 8 + random.nextInt(9));
                    items.add(exp);
                }
                break;
        }

        return items;
    }

    public void setChestLevel(String level) {
        this.chestLevel = level;
    }

    public String getChestLevel() {
        return chestLevel;
    }
}