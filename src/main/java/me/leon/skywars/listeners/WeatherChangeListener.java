package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {

    private SkyWars plugin;

    public WeatherChangeListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        // Wetter-Änderungen durch Voting erlauben
        // Aber automatische Änderungen verhindern wenn kein Vote aktiv
    }
}