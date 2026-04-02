package me.leon.skywars.commands;

import me.leon.skywars.SkyWars;
import me.leon.skywars.arena.Arena;
import me.leon.skywars.arena.ArenaMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {
    private final SkyWars plugin;

    public ArenaCommand(SkyWars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("homemc.skywars.setup")) {
            sender.sendMessage(SkyWars.PREFIX + "§cKeine Berechtigung!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "create":
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena create <Name>");
                    return true;
                }
                createArena(sender, args[1]);
                break;

            case "delete":
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena delete <Name>");
                    return true;
                }
                deleteArena(sender, args[1]);
                break;

            case "list":
                listArenas(sender);
                break;

            case "info":
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena info <Name>");
                    return true;
                }
                showArenaInfo(sender, args[1]);
                break;

            case "setpos1":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena setpos1 <Name>");
                    return true;
                }
                setPos1((Player) sender, args[1]);
                break;

            case "setpos2":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena setpos2 <Name>");
                    return true;
                }
                setPos2((Player) sender, args[1]);
                break;

            case "setlobby":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena setlobby <Name>");
                    return true;
                }
                setLobby((Player) sender, args[1]);
                break;

            case "setspectator":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena setspectator <Name>");
                    return true;
                }
                setSpectator((Player) sender, args[1]);
                break;

            case "addspawn":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cNur Spieler können diesen Befehl verwenden!");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena addspawn <Name>");
                    return true;
                }
                addSpawn((Player) sender, args[1]);
                break;

            case "removespawn":
                if (args.length < 3) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena removespawn <Name> <ID>");
                    return true;
                }
                removeSpawn(sender, args[1], args[2]);
                break;

            case "clearspawns":
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena clearspawns <Name>");
                    return true;
                }
                clearSpawns(sender, args[1]);
                break;

            case "setmode":
                if (args.length < 3) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena setmode <Name> <SOLO/DUOS/TRIOS/SQUADS>");
                    return true;
                }
                setMode(sender, args[1], args[2]);
                break;

            case "scan":
                if (args.length < 2) {
                    sender.sendMessage(SkyWars.PREFIX + "§cNutzung: /arena scan <Name>");
                    return true;
                }
                scanChests(sender, args[1]);
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void createArena(CommandSender sender, String name) {
        if (plugin.getArenaManager().arenaExists(name)) {
            sender.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert bereits!");
            return;
        }

        plugin.getArenaManager().createArena(name);
        plugin.getArenaManager().saveArenas();
        sender.sendMessage(SkyWars.PREFIX + "§aArena §e" + name + " §awurde erstellt!");
    }

    private void deleteArena(CommandSender sender, String name) {
        if (!plugin.getArenaManager().arenaExists(name)) {
            sender.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        plugin.getArenaManager().deleteArena(name);
        sender.sendMessage(SkyWars.PREFIX + "§aArena §e" + name + " §awurde gelöscht!");
    }

    private void listArenas(CommandSender sender) {
        sender.sendMessage("§8§m              §r §6§lArenas §8§m              ");

        if (plugin.getArenaManager().getArenas().isEmpty()) {
            sender.sendMessage("§7Keine Arenas vorhanden!");
        } else {
            for (Arena arena : plugin.getArenaManager().getArenas()) {
                String status = arena.isSetup() ? "§a✔" : "§c✖";
                sender.sendMessage(status + " §e" + arena.getName() + " §7(" +
                        arena.getMode().getDisplayShort() + ") §8- " +
                        arena.getSpawns().size() + " Spawns, " +
                        arena.getChests().size() + " Truhen");
            }
        }

        sender.sendMessage("§8§m                                      ");
    }

    private void showArenaInfo(CommandSender sender, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            sender.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        sender.sendMessage("§8§m          §r §6§l" + arena.getName() + " §8§m          ");
        sender.sendMessage("§7Modus: §e" + arena.getMode().getDisplayName() + " " +
                arena.getMode().getDisplayShort());
        sender.sendMessage("§7Position 1: " + (arena.getPos1() != null ? "§a✔" : "§c✖"));
        sender.sendMessage("§7Position 2: " + (arena.getPos2() != null ? "§a✔" : "§c✖"));
        sender.sendMessage("§7Lobby Spawn: " + (arena.getLobbySpawn() != null ? "§a✔" : "§c✖"));
        sender.sendMessage("§7Spectator Spawn: " + (arena.getSpectatorSpawn() != null ? "§a✔" : "§c✖"));
        sender.sendMessage("§7Spawns: §e" + arena.getSpawns().size() + "§7/§6" +
                arena.getMode().getRequiredSpawns());
        sender.sendMessage("§7Truhen: §e" + arena.getChests().size());
        sender.sendMessage("§7Setup: " + (arena.isSetup() ? "§aAbgeschlossen" : "§cUnvollständig"));
        sender.sendMessage("§8§m                                      ");
    }

    private void setPos1(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        arena.setPos1(player.getLocation());
        plugin.getArenaManager().saveArenas();
        player.sendMessage(SkyWars.PREFIX + "§aPosition 1 gesetzt!");
    }

    private void setPos2(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        arena.setPos2(player.getLocation());
        plugin.getArenaManager().saveArenas();

        arena.scanChests();
        player.sendMessage(SkyWars.PREFIX + "§aPosition 2 gesetzt!");
        player.sendMessage(SkyWars.PREFIX + "§7Truhen gescannt: §e" + arena.getChests().size());
    }

    private void setLobby(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        arena.setLobbySpawn(player.getLocation());
        plugin.getArenaManager().saveArenas();
        player.sendMessage(SkyWars.PREFIX + "§aLobby-Spawn gesetzt!");
    }

    private void setSpectator(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        arena.setSpectatorSpawn(player.getLocation());
        plugin.getArenaManager().saveArenas();
        player.sendMessage(SkyWars.PREFIX + "§aSpectator-Spawn gesetzt!");
    }

    private void addSpawn(Player player, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            player.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        arena.addSpawn(player.getLocation());
        plugin.getArenaManager().saveArenas();

        int current = arena.getSpawns().size();
        int required = arena.getMode().getRequiredSpawns();

        player.sendMessage(SkyWars.PREFIX + "§aSpawn #" + current + " hinzugefügt! " +
                "§7(§e" + current + "§7/§6" + required + "§7)");
    }

    private void removeSpawn(CommandSender sender, String name, String idStr) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            sender.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        try {
            int id = Integer.parseInt(idStr) - 1;
            arena.removeSpawn(id);
            plugin.getArenaManager().saveArenas();
            sender.sendMessage(SkyWars.PREFIX + "§aSpawn entfernt!");
        } catch (NumberFormatException e) {
            sender.sendMessage(SkyWars.PREFIX + "§cUngültige Zahl!");
        }
    }

    private void clearSpawns(CommandSender sender, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            sender.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        arena.clearSpawns();
        plugin.getArenaManager().saveArenas();
        sender.sendMessage(SkyWars.PREFIX + "§aAlle Spawns wurden entfernt!");
    }

    private void setMode(CommandSender sender, String name, String modeStr) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            sender.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        try {
            ArenaMode mode = ArenaMode.valueOf(modeStr.toUpperCase());
            arena.setMode(mode);
            plugin.getArenaManager().saveArenas();
            sender.sendMessage(SkyWars.PREFIX + "§aModus gesetzt: §e" + mode.getDisplayName() +
                    " " + mode.getDisplayShort());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(SkyWars.PREFIX + "§cUngültiger Modus! Verfügbar: SOLO, DUOS, TRIOS, SQUADS");
        }
    }

    private void scanChests(CommandSender sender, String name) {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) {
            sender.sendMessage(SkyWars.PREFIX + "§cDiese Arena existiert nicht!");
            return;
        }

        if (arena.getPos1() == null || arena.getPos2() == null) {
            sender.sendMessage(SkyWars.PREFIX + "§cSetze zuerst beide Positionen!");
            return;
        }

        arena.scanChests();
        plugin.getArenaManager().saveArenas();
        sender.sendMessage(SkyWars.PREFIX + "§aTruhen gescannt: §e" + arena.getChests().size());
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§8§m          §r §6§lArena Commands §8§m          ");
        sender.sendMessage("§e/arena create <Name> §7- Arena erstellen");
        sender.sendMessage("§e/arena delete <Name> §7- Arena löschen");
        sender.sendMessage("§e/arena list §7- Alle Arenas anzeigen");
        sender.sendMessage("§e/arena info <Name> §7- Arena-Info anzeigen");
        sender.sendMessage("§e/arena setpos1 <Name> §7- Position 1 setzen");
        sender.sendMessage("§e/arena setpos2 <Name> §7- Position 2 setzen");
        sender.sendMessage("§e/arena setlobby <Name> §7- Lobby-Spawn setzen");
        sender.sendMessage("§e/arena setspectator <Name> §7- Spectator-Spawn setzen");
        sender.sendMessage("§e/arena addspawn <Name> §7- Spieler-Spawn hinzufügen");
        sender.sendMessage("§e/arena removespawn <Name> <ID> §7- Spawn entfernen");
        sender.sendMessage("§e/arena clearspawns <Name> §7- Alle Spawns löschen");
        sender.sendMessage("§e/arena setmode <Name> <Modus> §7- Modus setzen");
        sender.sendMessage("§7Verfügbare Modi: §eSO LO, DUOS, TRIOS, SQUADS");
        sender.sendMessage("§e/arena scan <Name> §7- Truhen neu scannen");
        sender.sendMessage("§8§m                                              ");
    }
}