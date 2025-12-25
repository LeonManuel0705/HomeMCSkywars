package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {
    private final SkyWars plugin;

    public FoodLevelChangeListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Game game = plugin.getGameManager().getCurrentGame();

        if (game == null || game.getState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }
    }
}