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

        if (plugin.getGameManager().isInBuildMode(player)) {
            return;
        }

        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (plugin.getGameManager().isInBuildMode(player)) {
            return;
        }

        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

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

        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

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

        if (plugin.getLobbyManager().isInLobby(damager.getUniqueId()) ||
                plugin.getLobbyManager().isInLobby(victim.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

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

        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (plugin.getGameManager().isInGame(player)) {
            if (plugin.getGameManager().getCurrentGame().getState() != GameState.INGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }
}