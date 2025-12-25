package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {
    private final SkyWars plugin;

    public PlayerDropItemListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Game game = plugin.getGameManager().getCurrentGame();

        if (game == null || game.getState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getSpectatorManager().isSpectator(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
