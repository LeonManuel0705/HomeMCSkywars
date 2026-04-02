package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyInteractListener implements Listener {

    private final SkyWars plugin;

    public LobbyInteractListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        String displayName = item.getItemMeta().getDisplayName();

        if (displayName.contains("Spielen")) {
            if (plugin.getGameManager().getCurrentGame() != null) {
                plugin.getGameManager().addPlayer(player);
            } else {
                player.sendMessage(SkyWars.PREFIX + "§cKein Spiel verfügbar!");
            }
            event.setCancelled(true);
        } else if (displayName.contains("Statistiken")) {
            plugin.getStatsManager().showStats(player, player);
            event.setCancelled(true);
        } else if (displayName.contains("Zurück zur Lobby")) {
            plugin.getLobbyManager().sendToMainLobby(player);
            event.setCancelled(true);
        }
    }
}