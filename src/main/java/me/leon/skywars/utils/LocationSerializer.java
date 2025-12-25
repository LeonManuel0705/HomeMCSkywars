package me.leon.skywars.utils;

import me.leon.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {

    private static SkyWars plugin;

    public static void init(SkyWars pl) {
        plugin = pl;
    }

    public static String serialize(Location loc) {
        if (loc == null) return null;

        return loc.getWorld().getName() + ";" +
                loc.getX() + ";" +
                loc.getY() + ";" +
                loc.getZ() + ";" +
                loc.getYaw() + ";" +
                loc.getPitch();
    }

    public static Location deserialize(String str) {
        if (str == null || str.isEmpty()) return null;

        try {
            String[] parts = str.split(";");

            World world = Bukkit.getWorld(parts[0]);
            if (world == null) {
                plugin.getLogger().warning("World '" + parts[0] + "' not found!");
                return null;
            }

            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            return new Location(world, x, y, z, yaw, pitch);

        } catch (Exception e) {
            plugin.getLogger().severe("Fehler beim Deserialisieren von Location: " + str);
            e.printStackTrace();
            return null;
        }
    }
}