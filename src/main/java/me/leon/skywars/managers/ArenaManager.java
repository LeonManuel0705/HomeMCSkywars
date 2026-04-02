package me.leon.skywars.managers;

import me.leon.skywars.SkyWars;
import me.leon.skywars.arena.Arena;
import me.leon.skywars.arena.ArenaMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ArenaManager {

    private final SkyWars plugin;
    private final Map<String, Arena> arenas;
    private final File arenaFile;
    private FileConfiguration arenaConfig;

    public ArenaManager(SkyWars plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.arenaFile = new File(plugin.getDataFolder(), "arenas.yml");
        loadArenas();
    }

    private void loadArenas() {
        if (!arenaFile.exists()) {
            try {
                arenaFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);

        ConfigurationSection section = arenaConfig.getConfigurationSection("arenas");
        if (section != null) {
            for (String name : section.getKeys(false)) {
                try {
                    Arena arena = loadArena(name);
                    if (arena != null) {
                        arenas.put(name.toLowerCase(), arena);
                        plugin.getLogger().info("Arena geladen: " + name + " (" +
                                arena.getSpawns().size() + " Spawns, " +
                                arena.getChests().size() + " Truhen)");
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Fehler beim Laden der Arena " + name);
                    e.printStackTrace();
                }
            }
        }

        plugin.getLogger().info("Arenas geladen: " + arenas.size());
    }

    private Arena loadArena(String name) {
        String path = "arenas." + name + ".";

        if (!arenaConfig.contains(path + "pos1") || !arenaConfig.contains(path + "pos2")) {
            return null;
        }

        Location pos1 = deserializeLocation(arenaConfig.getString(path + "pos1"));
        Location pos2 = deserializeLocation(arenaConfig.getString(path + "pos2"));
        Location lobby = deserializeLocation(arenaConfig.getString(path + "lobby"));
        Location spectator = deserializeLocation(arenaConfig.getString(path + "spectator"));

        String modeStr = arenaConfig.getString(path + "mode", "SOLO");
        ArenaMode mode = ArenaMode.valueOf(modeStr);

        Arena arena = new Arena(name, pos1, pos2, mode);
        arena.setLobbySpawn(lobby);
        arena.setSpectatorSpawn(spectator);

        List<String> spawnStrings = arenaConfig.getStringList(path + "spawns");
        for (String spawnStr : spawnStrings) {
            Location spawn = deserializeLocation(spawnStr);
            if (spawn != null) {
                arena.addSpawn(spawn);
            }
        }

        arena.scanChests();

        return arena;
    }

    public void saveArenas() {
        for (Arena arena : arenas.values()) {
            saveArena(arena);
        }

        try {
            arenaConfig.save(arenaFile);
            plugin.getLogger().info("Arenas gespeichert: " + arenas.size());
        } catch (Exception e) {
            plugin.getLogger().severe("Fehler beim Speichern der Arenas");
            e.printStackTrace();
        }
    }

    private void saveArena(Arena arena) {
        String path = "arenas." + arena.getName() + ".";

        arenaConfig.set(path + "pos1", serializeLocation(arena.getPos1()));
        arenaConfig.set(path + "pos2", serializeLocation(arena.getPos2()));
        arenaConfig.set(path + "lobby", serializeLocation(arena.getLobbySpawn()));
        arenaConfig.set(path + "spectator", serializeLocation(arena.getSpectatorSpawn()));
        arenaConfig.set(path + "mode", arena.getMode().name());

        List<String> spawnStrings = new ArrayList<>();
        for (Location spawn : arena.getSpawns()) {
            spawnStrings.add(serializeLocation(spawn));
        }
        arenaConfig.set(path + "spawns", spawnStrings);
    }

    public void createArena(String name) {
        Arena arena = new Arena(name, null, null, ArenaMode.SOLO);
        arenas.put(name.toLowerCase(), arena);
    }

    public void deleteArena(String name) {
        arenas.remove(name.toLowerCase());
        arenaConfig.set("arenas." + name, null);
        saveArenas();
    }

    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    public List<Arena> getAvailableArenas() {
        List<Arena> available = new ArrayList<>();
        for (Arena arena : arenas.values()) {
            if (arena.isSetup()) {
                available.add(arena);
            }
        }
        return available;
    }

    public Arena getRandomArena(ArenaMode mode) {
        List<Arena> suitable = new ArrayList<>();
        for (Arena arena : arenas.values()) {
            if (arena.isSetup() && arena.getMode() == mode) {
                suitable.add(arena);
            }
        }

        if (suitable.isEmpty()) return null;
        return suitable.get(new Random().nextInt(suitable.size()));
    }

    public boolean arenaExists(String name) {
        return arenas.containsKey(name.toLowerCase());
    }

    private String serializeLocation(Location loc) {
        if (loc == null) return null;
        return loc.getWorld().getName() + ";" +
                loc.getX() + ";" +
                loc.getY() + ";" +
                loc.getZ() + ";" +
                loc.getYaw() + ";" +
                loc.getPitch();
    }

    private Location deserializeLocation(String str) {
        if (str == null || str.isEmpty()) return null;
        try {
            String[] parts = str.split(";");
            return new Location(
                    Bukkit.getWorld(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]),
                    Float.parseFloat(parts[4]),
                    Float.parseFloat(parts[5])
            );
        } catch (Exception e) {
            return null;
        }
    }
}