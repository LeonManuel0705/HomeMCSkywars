package me.leon.skywars.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private final String name;
    private Location pos1;
    private Location pos2;
    private Location lobbySpawn;
    private Location spectatorSpawn;
    private final List<Location> spawns;
    private final List<Location> chests;
    private ArenaMode mode;

    public Arena(String name, Location pos1, Location pos2, ArenaMode mode) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.mode = mode;
        this.spawns = new ArrayList<>();
        this.chests = new ArrayList<>();
    }

    public void scanChests() {
        if (pos1 == null || pos2 == null) return;
        if (!pos1.getWorld().equals(pos2.getWorld())) return;

        chests.clear();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = pos1.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.CHEST) {
                        chests.add(block.getLocation());
                    }
                }
            }
        }
    }

    public boolean isInArena(Location loc) {
        if (pos1 == null || pos2 == null) return false;
        if (!loc.getWorld().equals(pos1.getWorld())) return false;

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        return loc.getBlockX() >= minX && loc.getBlockX() <= maxX &&
                loc.getBlockY() >= minY && loc.getBlockY() <= maxY &&
                loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ;
    }

    public boolean isSetup() {
        return pos1 != null && pos2 != null &&
                lobbySpawn != null && spectatorSpawn != null &&
                spawns.size() >= mode.getRequiredSpawns();
    }

    public void addSpawn(Location spawn) {
        spawns.add(spawn);
    }

    public void removeSpawn(int index) {
        if (index >= 0 && index < spawns.size()) {
            spawns.remove(index);
        }
    }

    public void clearSpawns() {
        spawns.clear();
    }

    // Getters & Setters
    public String getName() { return name; }
    public Location getPos1() { return pos1; }
    public void setPos1(Location pos1) { this.pos1 = pos1; }
    public Location getPos2() { return pos2; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }
    public Location getLobbySpawn() { return lobbySpawn; }
    public void setLobbySpawn(Location lobbySpawn) { this.lobbySpawn = lobbySpawn; }
    public Location getSpectatorSpawn() { return spectatorSpawn; }
    public void setSpectatorSpawn(Location spectatorSpawn) { this.spectatorSpawn = spectatorSpawn; }
    public List<Location> getSpawns() { return new ArrayList<>(spawns); }
    public List<Location> getChests() { return new ArrayList<>(chests); }
    public ArenaMode getMode() { return mode; }
    public void setMode(ArenaMode mode) { this.mode = mode; }
}