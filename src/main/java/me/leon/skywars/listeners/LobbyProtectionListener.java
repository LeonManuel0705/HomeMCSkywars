package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class LobbyProtectionListener implements Listener {

    private final SkyWars plugin;

    public LobbyProtectionListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Build-Mode Check
        if (plugin.getGameManager().isInBuildMode(player)) {
            return;
        }

        // In der Lobby nicht abbauen
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Im Spiel nur während INGAME abbauen
        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        // Build-Mode Check
        if (plugin.getGameManager().isInBuildMode(player)) {
            return;
        }

        // In der Lobby nicht platzieren
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Im Spiel nur während INGAME platzieren
        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        // In der Lobby kein Schaden
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Im Spiel nur während INGAME Schaden nehmen
        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        // In der Lobby kein PvP
        if (plugin.getLobbyManager().isInLobby(damager.getUniqueId()) ||
                plugin.getLobbyManager().isInLobby(victim.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Im Spiel nur während INGAME PvP
        if (plugin.getGameManager().isInGame(damager) || plugin.getGameManager().isInGame(victim)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        // In der Lobby kein Hunger
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Im Spiel nur während INGAME Hunger
        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        // In der Lobby keine Items droppen
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Im Spiel nur während INGAME Items droppen
        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        // In der Lobby keine Items aufheben
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // Im Spiel nur während INGAME Items aufheben
        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        // Wetter immer schön halten
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}