package me.leon.skywars.commands;

import me.leon.core.managers.RankManager;
import me.leon.skywars.SkyWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceStartCommand implements CommandExecutor {
    private final SkyWars plugin;

    public ForceStartCommand(SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skywars.forcestart")) {
            RankManager.RankData yt = plugin.getRankManager().getRankData("homemc.yt");
            String ytPrefix = yt.prefix;
            String ytColor = yt.color;
            String ytName = yt.displayName;
            String ytFormatted = ytColor + ytName;
            sender.sendMessage("§8§m=======§r §c§lZugriff verweigert §8§m=======§r");
            sender.sendMessage("§3Um diesen Befehl zu nutzen, benötigst du:");
            sender.sendMessage(" §8• §2Rang: " + ytFormatted);
            sender.sendMessage("§8§m====================================");
            return true;
        }

        plugin.getGameManager().forceStart();
        sender.sendMessage(SkyWars.PREFIX + "§aCountdown beschleunigt!");

        return true;
    }
}