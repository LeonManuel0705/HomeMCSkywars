package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {
    private final SkyWars plugin;

    public InventoryClickListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getInventory().getTitle();
        ItemStack clicked = event.getCurrentItem();

        if (clicked == null || clicked.getType() == Material.AIR) return;

        // Kit Selector
        if (title.equals("§6§lKit Auswahl")) {
            event.setCancelled(true);

            if (!clicked.hasItemMeta()) return;

            String kitName = clicked.getItemMeta().getDisplayName();

            // Finde Kit ID
            String kitId = null;
            for (me.leon.skywars.managers.KitManager.Kit kit : plugin.getKitManager().getKits()) {
                if (kit.getDisplayName().equals(kitName)) {
                    kitId = kit.getId();
                    break;
                }
            }

            if (kitId != null) {
                Game game = plugin.getGameManager().getCurrentGame();
                if (game != null) {
                    GamePlayer gp = game.getGamePlayer(player.getUniqueId());
                    if (gp != null) {
                        gp.setSelectedKit(kitId);
                        player.sendMessage(SkyWars.PREFIX + "§aKit ausgewählt: " + kitName);
                        player.closeInventory();
                    }
                }
            }
        }

        // Vote Menu
        else if (title.equals("§6§lAbstimmung")) {
            event.setCancelled(true);

            if (!clicked.hasItemMeta()) return;

            String name = clicked.getItemMeta().getDisplayName();

            if (name.contains("Zeit")) {
                plugin.getVoteManager().openTimeVote(player);
            } else if (name.contains("Wetter")) {
                plugin.getVoteManager().openWeatherVote(player);
            } else if (name.contains("Truhen")) {
                plugin.getVoteManager().openChestVote(player);
            }
        }

        // Zeit Vote
        else if (title.equals("§eZeit")) {
            event.setCancelled(true);

            if (!clicked.hasItemMeta()) return;

            String choice = clicked.getItemMeta().getDisplayName();
            choice = choice.replace("§e", "").replace("§7", "").replace("§6", "").replace("§c", "");

            plugin.getVoteManager().vote(player, "time", choice);
            player.closeInventory();
        }

        // Wetter Vote
        else if (title.equals("§bWetter")) {
            event.setCancelled(true);

            if (!clicked.hasItemMeta()) return;

            String choice = clicked.getItemMeta().getDisplayName();
            choice = choice.replace("§a", "").replace("§b", "").replace("§f", "");

            plugin.getVoteManager().vote(player, "weather", choice);
            player.closeInventory();
        }

        // Chest Vote
        else if (title.equals("§6Truhen")) {
            event.setCancelled(true);

            if (!clicked.hasItemMeta()) return;

            String choice = clicked.getItemMeta().getDisplayName();
            choice = choice.replace("§7", "").replace("§6", "").replace("§b", "");

            plugin.getVoteManager().vote(player, "chest", choice);
            player.closeInventory();
        }

        // Stats GUI
        else if (title.contains("Stats")) {
            event.setCancelled(true);
        }
    }
}