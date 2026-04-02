package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    private final SkyWars plugin;

    public EntityDamageListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.ARMOR_STAND ||
                event.getEntityType() == EntityType.ITEM_FRAME ||
                event.getEntityType() == EntityType.PAINTING) {
            event.setCancelled(true);
        }
    }
}