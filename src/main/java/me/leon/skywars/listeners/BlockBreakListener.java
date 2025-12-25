package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    private final SkyWars plugin;

    public BlockBreakListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
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

        Material type = event.getBlock().getType();

        // Nur erlaubte Blöcke abbauen
        if (type != Material.WOOD && type != Material.LOG && type != Material.LOG_2 &&
                !type.name().contains("LEAVES") && !type.name().contains("GLASS") &&
                type != Material.SANDSTONE && type != Material.SAND) {
            event.setCancelled(true);
        }
    }
}
