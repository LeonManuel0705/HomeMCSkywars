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

        // Aus Lobby entfernen
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            plugin.getLobbyManager().removeFromLobby(player.getUniqueId());
        }

        // Aus Spiel entfernen
        if (plugin.getGameManager().isInGame(player)) {
            plugin.getGameManager().removePlayer(player);
        }

        // Aus Spectator entfernen
        if (plugin.getSpectatorManager().isSpectator(player.getUniqueId())) {
            plugin.getSpectatorManager().removeSpectator(player);
        }

        // Stats aus Cache entfernen (werden automatisch in DB gespeichert durch addStat Methoden)
        // Kein manuelles saveStats() nötig, da StatsManager async updates macht
    }
}