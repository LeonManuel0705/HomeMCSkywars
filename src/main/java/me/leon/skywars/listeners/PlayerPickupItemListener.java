package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItemListener implements Listener {
    private final SkyWars plugin;

    public PlayerPickupItemListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        if (plugin.getSpectatorManager().isSpectator(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}