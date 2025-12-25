package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GamePlayer;
import me.leon.skywars.game.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final SkyWars plugin;

    public PlayerDeathListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        Game game = plugin.getGameManager().getCurrentGame();
        if (game == null || game.getState() != GameState.INGAME) {
            return;
        }

        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);

        GamePlayer victimGP = game.getGamePlayer(victim.getUniqueId());
        if (victimGP != null) {
            victimGP.addDeath();
        }

        String victimName = getRankedName(victim);

        if (killer != null && !killer.equals(victim)) {
            GamePlayer killerGP = game.getGamePlayer(killer.getUniqueId());
            if (killerGP != null) {
                killerGP.addKill();

                killer.setHealth(Math.min(20.0, killer.getHealth() + 4.0));
                killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            }

            String killerName = getRankedName(killer);
            game.broadcast(victimName + " §7wurde von " + killerName + " §7getötet!");
        } else {
            game.broadcast(victimName + " §7ist gestorben!");
        }

        // Respawn und Spectator
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            victim.spigot().respawn();
            plugin.getGameManager().removePlayer(victim);
            plugin.getSpectatorManager().addSpectator(victim);
        }, 5L);
    }

    private String getRankedName(Player player) {
        try {
            Object core = plugin.getServer().getPluginManager().getPlugin("Core");
            if (core != null) {
                Object rankManager = core.getClass().getMethod("getRankManager").invoke(core);
                Object rankColor = rankManager.getClass().getMethod("getRankColor", Player.class).invoke(rankManager, player);
                return rankColor + player.getName();
            }
        } catch (Exception e) {
            // Fallback wenn Core-Plugin nicht verfügbar oder Methode nicht existiert
        }
        return player.getName();
    }
}
