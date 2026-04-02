package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import me.leon.skywars.arena.Arena;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import me.leon.skywars.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameManager {

    private final SkyWars plugin;
    private Game currentGame;
    private final Set<UUID> buildMode;
    private BukkitTask gameTask;

    public GameManager(SkyWars plugin) {
        this.plugin = plugin;
        this.buildMode = new HashSet<>();
        startGameLoop();
    }

    private void startGameLoop() {
        gameTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (currentGame != null) {
                currentGame.tick();
            }
        }, 20L, 20L);
    }

    public boolean startGame() {
        if (currentGame != null && currentGame.getState() != GameState.ENDING &&
                currentGame.getState() != GameState.RESTARTING) {
            return false;
        }

        Arena arena = plugin.getArenaManager().getRandomArena(me.leon.skywars.arena.ArenaMode.SOLO);

        if (arena == null) {
            Bukkit.broadcastMessage(SkyWars.PREFIX + "§cKeine Arena verfügbar!");
            return false;
        }

        if (!arena.isSetup()) {
            Bukkit.broadcastMessage(SkyWars.PREFIX + "§cDie Arena ist nicht vollständig eingerichtet!");
            return false;
        }

        currentGame = new Game(plugin, arena);
        currentGame.start();
        return true;
    }

    public void stopGame() {
        if (currentGame == null) return;
        currentGame.end(null);
    }

    public void forceStart() {
        if (currentGame == null || currentGame.getState() != GameState.LOBBY) {
            return;
        }
        currentGame.forceStart();
    }

    public void addPlayer(Player player) {
        if (currentGame == null || currentGame.getState() != GameState.LOBBY) {
            player.kickPlayer("§cDas Spiel läuft bereits oder ist nicht verfügbar!");
            return;
        }

        if (plugin.getCore().getVanishManager().isVanished(player.getUniqueId())) {
            player.teleport(plugin.getMapManager().getLobbySpawn());
            plugin.getSpectatorManager().addSpectator(player);
            return;
        }

        currentGame.addPlayer(player);
    }

    public void removePlayer(Player player) {
        if (currentGame != null) {
            currentGame.removePlayer(player, false);
        }
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public boolean isInGame(Player player) {
        return currentGame != null && currentGame.isInGame(player.getUniqueId());
    }

    public GamePlayer getGamePlayer(Player player) {
        if (currentGame == null) return null;
        return currentGame.getGamePlayer(player.getUniqueId());
    }

    public void toggleBuildMode(Player player) {
        UUID uuid = player.getUniqueId();
        if (buildMode.contains(uuid)) {
            buildMode.remove(uuid);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            plugin.getLobbyManager().giveLobbyItems(player);
            player.sendMessage(SkyWars.PREFIX + "§cBuild-Modus deaktiviert!");

        } else {
            buildMode.add(uuid);
            player.getInventory().clear();
            player.setGameMode(GameMode.CREATIVE);
            player.sendMessage(SkyWars.PREFIX + "§aBuild-Modus aktiviert!");
        }
    }

    public boolean isInBuildMode(Player player) {
        return buildMode.contains(player.getUniqueId());
    }

    public void shutdown() {
        if (gameTask != null) {
            gameTask.cancel();
        }
        if (currentGame != null) {
            currentGame.end(null);
        }
    }
}