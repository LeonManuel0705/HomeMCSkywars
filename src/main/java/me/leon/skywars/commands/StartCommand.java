package me.leon.skywars.commands;

import me.leon.core.managers.RankManager;
import me.leon.skywars.SkyWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
    private final SkyWars plugin;

    public StartCommand(SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skywars.start")) {
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

        if (plugin.getGameManager().startGame()) {
            sender.sendMessage(SkyWars.PREFIX + "§aSpiel wurde gestartet!");
        } else {
            sender.sendMessage(SkyWars.PREFIX + "§cSpiel läuft bereits oder konnte nicht gestartet werden!");
        }

        return true;
    }
}