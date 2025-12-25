package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final SkyWars plugin;

    public PlayerJoinListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        Game game = plugin.getGameManager().getCurrentGame();

        // Vanished Spieler direkt als Spectator
        if (plugin.getCore().getVanishManager().isVanished(player.getUniqueId())) {
            if (game != null) {
                plugin.getSpectatorManager().addSpectator(player);
            } else {
                plugin.getLobbyManager().addToLobby(player);
            }
            return;
        }

        // Spiel existiert und ist in LOBBY oder STARTING
        if (game != null && (game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING)) {
            plugin.getGameManager().addPlayer(player);
        }
        // Spiel läuft bereits (INGAME, ENDING, RESTARTING)
        else if (game != null) {
            plugin.getSpectatorManager().addSpectator(player);
            player.sendMessage(SkyWars.PREFIX + "§7Das Spiel läuft bereits! Du wurdest als Zuschauer hinzugefügt.");
        }
        // Kein Spiel vorhanden - Lobby
        else {
            plugin.getLobbyManager().addToLobby(player);
        }

        // Stats laden
        plugin.getStatsManager().loadStats(player.getUniqueId());
    }
}