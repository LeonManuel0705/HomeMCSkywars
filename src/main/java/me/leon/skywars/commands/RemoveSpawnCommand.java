package me.leon.skywars.commands;

import me.leon.core.managers.RankManager;
import me.leon.skywars.SkyWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveSpawnCommand implements CommandExecutor {
    private final SkyWars plugin;

    public RemoveSpawnCommand(SkyWars plugin) {
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

        if (args.length == 0) {
            sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /removespawn <ID>");
            return true;
        }

        try {
            int index = Integer.parseInt(args[0]) - 1;
            plugin.getMapManager().removeSpawn(index);
            sender.sendMessage(SkyWars.PREFIX + "§aSpawn entfernt!");
        } catch (NumberFormatException e) {
            sender.sendMessage(SkyWars.PREFIX + "§cUngültige Zahl!");
        }

        return true;
    }
}