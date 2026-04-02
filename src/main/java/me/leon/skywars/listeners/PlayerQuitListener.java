package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final SkyWars plugin;

    public PlayerQuitListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            plugin.getLobbyManager().removeFromLobby(player.getUniqueId());
        }

        if (plugin.getGameManager().isInGame(player)) {
            plugin.getGameManager().removePlayer(player);
        }

        if (plugin.getSpectatorManager().isSpectator(player.getUniqueId())) {
            plugin.getSpectatorManager().removeSpectator(player);
        }

    }
}