package me.leon.skywars.commands;

import me.leon.core.managers.RankManager;
import me.leon.skywars.SkyWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {
    private final SkyWars plugin;

    public BuildCommand(SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("skywars.lobby.build")) {
            RankManager.RankData builder = plugin.getRankManager().getRankData("homemc.builder");
            String builderPrefix = builder.prefix;
            String builderColor = builder.color;
            String builderName = builder.displayName;
            String builderFormatted = builderColor + builderName;
            player.sendMessage("§8§m=======§r §c§lZugriff verweigert §8§m=======§r");
            player.sendMessage("§3Um diesen Befehl auszuführen, benötigst du:");
            player.sendMessage(" §8• §2Rang: " + builderFormatted);
            player.sendMessage("§8§m====================================");
            return true;
        }

        plugin.getGameManager().toggleBuildMode(player);

        return true;
    }
}