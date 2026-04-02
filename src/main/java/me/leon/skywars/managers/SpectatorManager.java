package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SpectatorManager {

    private final SkyWars plugin;
    private final Set<UUID> spectators;

    public SpectatorManager(SkyWars plugin) {
        this.plugin = plugin;
        this.spectators = new HashSet<>();
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        player.setGameMode(GameMode.SPECTATOR);
        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!spectators.contains(online.getUniqueId())) {
                player.hidePlayer(online);
            }
        }

        player.sendMessage(SkyWars.PREFIX + "§7Du bist jetzt ein §eZuschauer§7!");
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);

        for (Player online : Bukkit.getOnlinePlayers()) {
            player.showPlayer(online);
        }
    }

    public boolean isSpectator(UUID uuid) {
        return spectators.contains(uuid);
    }

    public Set<UUID> getSpectators() {
        return new HashSet<>(spectators);
    }
}
