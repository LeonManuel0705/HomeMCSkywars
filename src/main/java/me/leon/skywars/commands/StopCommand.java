package me.leon.skywars.commands;

import me.leon.core.managers.RankManager;
import me.leon.skywars.SkyWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StopCommand implements CommandExecutor {
    private final SkyWars plugin;

    public StopCommand(SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skywars.stop")) {
            RankManager.RankData mod = plugin.getRankManager().getRankData("homemc.mod");
            String modPrefix = mod.prefix;
            String modColor = mod.color;
            String modName = mod.displayName;
            String modFormatted = modColor + modName;
            sender.sendMessage("§8§m=======§r §c§lZugriff verweigert §8§m=======§r");
            sender.sendMessage("§3Um ein anderes Inventar zu sehen, benötigst du:");
            sender.sendMessage(" §8• §2Rang: " + modFormatted);
            sender.sendMessage("§8§m====================================");
            return true;
        }

        plugin.getGameManager().stopGame();
        sender.sendMessage(SkyWars.PREFIX + "§cSpiel wurde beendet!");

        return true;
    }
}