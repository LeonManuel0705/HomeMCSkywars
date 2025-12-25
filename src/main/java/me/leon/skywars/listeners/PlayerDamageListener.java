package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {
    private final SkyWars plugin;

    public PlayerDamageListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        Game game = plugin.getGameManager().getCurrentGame();

        if (game == null) {
            event.setCancelled(true);
            return;
        }

        if (game.getState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getSpectatorManager().isSpectator(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();

        Game game = plugin.getGameManager().getCurrentGame();

        if (game == null || game.getState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getSpectatorManager().isSpectator(victim.getUniqueId()) ||
                plugin.getSpectatorManager().isSpectator(damager.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
    }
}