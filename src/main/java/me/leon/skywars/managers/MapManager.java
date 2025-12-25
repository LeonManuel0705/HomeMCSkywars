package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import me.leon.skywars.utils.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MapManager {

    private final SkyWars plugin;
    private Location lobbySpawn;
    private final List<Location> spawnLocations;

    public MapManager(SkyWars plugin) {
        this.plugin = plugin;
        this.spawnLocations = new ArrayList<>();
        loadLocations();
    }

    private void loadLocations() {
        FileConfiguration config = plugin.getConfig();

        // Lobby Spawn laden
        if (config.contains("lobby-spawn")) {
            String serialized = config.getString("lobby-spawn");
            lobbySpawn = LocationSerializer.deserialize(serialized);
            plugin.getLogger().info("Lobby-Spawn geladen: " + lobbySpawn);
        }

        // Spawn Locations laden
        if (config.contains("spawns")) {
            List<String> spawns = config.getStringList("spawns");
            for (String spawn : spawns) {
                Location loc = LocationSerializer.deserialize(spawn);
                if (loc != null) {
                    spawnLocations.add(loc);
                }
            }
            plugin.getLogger().info("Spawns geladen: " + spawnLocations.size());
        }
    }

    public void setLobbySpawn(Location location) {
        this.lobbySpawn = location;
        plugin.getConfig().set("lobby-spawn", LocationSerializer.serialize(location));
        plugin.saveConfig();
    }

    public void addSpawn(Location location) {
        spawnLocations.add(location);
        saveSpawns();
    }

    public void removeSpawn(int index) {
        if (index >= 0 && index < spawnLocations.size()) {
            spawnLocations.remove(index);
            saveSpawns();
        }
    }

    private void saveSpawns() {
        List<String> spawns = new ArrayList<>();
        for (Location loc : spawnLocations) {
            spawns.add(LocationSerializer.serialize(loc));
        }
        plugin.getConfig().set("spawns", spawns);
        plugin.saveConfig();
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public List<Location> getSpawnLocations() {
        return new ArrayList<>(spawnLocations);
    }
}