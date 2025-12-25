package me.leon.skywars.commands;

import me.leon.core.managers.RankManager;
import me.leon.skywars.SkyWars;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ListSpawnsCommand implements CommandExecutor {
    private final SkyWars plugin;

    public ListSpawnsCommand(SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skywars.setup")) {
            RankManager.RankData dev = plugin.getRankManager().getRankData("homemc.developer");
            String devPrefix = dev.prefix;
            String devColor = dev.color;
            String devName = dev.displayName;
            String devFormatted = devColor + devName;
            sender.sendMessage("§8§m=======§r §c§lZugriff verweigert §8§m=======§r");
            sender.sendMessage("§3Um den Befehl zu nutzen, benötigst du:");
            sender.sendMessage(" §8• §2Rang: " + devFormatted);
            sender.sendMessage("§8§m====================================");
            return true;
        }

        List<Location> spawns = plugin.getMapManager().getSpawnLocations();

        sender.sendMessage("§8§m              §r §6§lSpawns §8§m              ");
        sender.sendMessage("§7Gesamt: §e" + spawns.size());
        sender.sendMessage("");

        for (int i = 0; i < spawns.size(); i++) {
            Location loc = spawns.get(i);
            sender.sendMessage("§7#" + (i + 1) + " §8» §e" +
                    String.format("X: %.1f Y: %.1f Z: %.1f", loc.getX(), loc.getY(), loc.getZ()));
        }

        sender.sendMessage("§8§m                                      ");

        return true;
    }
}
