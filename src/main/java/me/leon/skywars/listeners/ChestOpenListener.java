package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ChestOpenListener implements Listener {
    private final SkyWars plugin;

    public ChestOpenListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof Chest)) return;

        Player player = (Player) event.getPlayer();
        Game game = plugin.getGameManager().getCurrentGame();

        if (game == null || game.getState() != GameState.INGAME) {
            if (!plugin.getGameManager().isInBuildMode(player)) {
                event.setCancelled(true);
            }
        }
    }
}