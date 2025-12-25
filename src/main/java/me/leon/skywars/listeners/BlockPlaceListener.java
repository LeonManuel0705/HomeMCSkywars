package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    private final SkyWars plugin;

    public BlockPlaceListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE &&
                plugin.getGameManager().isInBuildMode(player)) {
            return;
        }

        Game game = plugin.getGameManager().getCurrentGame();

        if (game == null || game.getState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        // Platzierte Blöcke tracken für Cleanup
        Material type = event.getBlock().getType();

        // Verbotene Blöcke
        if (type == Material.TNT || type == Material.OBSIDIAN ||
                type == Material.BEDROCK) {
            event.setCancelled(true);
        }
    }
}