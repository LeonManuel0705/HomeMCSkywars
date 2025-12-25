package me.leon.skywars.commands;

import me.leon.core.managers.RankManager;
import me.leon.skywars.SkyWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLobbyCommand implements CommandExecutor {
    private final SkyWars plugin;

    public SetLobbyCommand(SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("skywars.setup")) {
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

        plugin.getMapManager().setLobbySpawn(player.getLocation());
        player.sendMessage(SkyWars.PREFIX + "§aLobby-Spawn gesetzt!");

        return true;
    }
}