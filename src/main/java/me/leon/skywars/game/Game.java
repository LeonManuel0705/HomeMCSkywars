package me.leon.skywars.game;

import me.leon.skywars.SkyWars;
import me.leon.skywars.arena.Arena;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private final SkyWars plugin;
    private GameState state;
    private final Map<UUID, GamePlayer> players;
    private final Set<UUID> alive;
    private final Arena arena;

    private int lobbyCountdown;
    private int gameTimer;
    private int chestRefillTimer;

    private final int MIN_PLAYERS;
    private final int MAX_PLAYERS;
    private final int LOBBY_TIME;
    private final int CHEST_REFILL_TIME;

    public Game(SkyWars plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.state = GameState.LOBBY;
        this.players = new HashMap<>();
        this.alive = new HashSet<>();

        this.MIN_PLAYERS = plugin.getConfig().getInt("game.min-players", 2);
        this.MAX_PLAYERS = arena.getMode().getMaxPlayers();
        this.LOBBY_TIME = plugin.getConfig().getInt("game.lobby-time", 60);
        this.CHEST_REFILL_TIME = plugin.getConfig().getInt("game.chest-refill-time", 180);

        this.lobbyCountdown = LOBBY_TIME;
        this.gameTimer = 0;
        this.chestRefillTimer = CHEST_REFILL_TIME;

        plugin.getChestManager().setCurrentArena(arena);
    }

    public void start() {
        state = GameState.LOBBY;

        broadcast("§8§m                                    ");
        broadcast("§6§lSKYWARS");
        broadcast("");
        broadcast("§7Map: §e" + arena.getName());
        broadcast("§7Modus: " + arena.getMode().getDisplayName() + " §7(" +
                arena.getMode().getDisplayShort() + ")");
        broadcast("");
        broadcast("§7Warte auf Spieler...");
        broadcast("§7§o(" + players.size() + "/" + MAX_PLAYERS + ")");
        broadcast("§8§m                                    ");
    }

    public void tick() {
        switch (state) {
            case LOBBY:
                tickLobby();
                break;
            case STARTING:
                tickStarting();
                break;
            case INGAME:
                tickIngame();
                break;
            case ENDING:
                tickEnding();
                break;
            case RESTARTING:
                tickRestarting();
                break;
        }

        updateScoreboards();
    }

    private void tickLobby() {
        int online = getAlivePlayers().size();

        if (online >= MIN_PLAYERS && state == GameState.LOBBY) {
            state = GameState.STARTING;
            lobbyCountdown = LOBBY_TIME;
        }

        if (online < MIN_PLAYERS && state == GameState.STARTING) {
            state = GameState.LOBBY;
            broadcast("§cZu wenig Spieler! Countdown abgebrochen.");
        }
    }

    private void tickStarting() {
        lobbyCountdown--;

        int online = getAlivePlayers().size();
        if (online < MIN_PLAYERS) {
            state = GameState.LOBBY;
            broadcast("§cZu wenig Spieler! Countdown abgebrochen.");
            return;
        }

        if (lobbyCountdown == 60 || lobbyCountdown == 30 || lobbyCountdown == 15 ||
                lobbyCountdown == 10 || (lobbyCountdown <= 5 && lobbyCountdown > 0)) {
            broadcast("§aDas Spiel startet in §e" + lobbyCountdown + " §aSekunden!");
            playSound(Sound.NOTE_PLING, 1.0f, 1.0f);
        }

        if (lobbyCountdown <= 0) {
            startIngame();
        }
    }

    private void startIngame() {
        state = GameState.INGAME;
        gameTimer = 0;

        plugin.getVoteManager().applyVotes();

        List<Location> spawns = new ArrayList<>(arena.getSpawns());
        Collections.shuffle(spawns);

        int spawnIndex = 0;
        for (GamePlayer gp : players.values()) {
            if (!gp.isAlive()) continue;

            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p == null || !p.isOnline()) continue;

            plugin.getLobbyManager().removeFromLobby(gp.getUuid());

            Location spawn = spawns.get(spawnIndex % spawns.size());
            p.teleport(spawn);

            plugin.getKitManager().giveKit(p, gp.getSelectedKit());

            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20.0);
            p.setFoodLevel(20);
            p.getInventory().setArmorContents(null);

            for (PotionEffect effect : p.getActivePotionEffects()) {
                p.removePotionEffect(effect.getType());
            }

            alive.add(gp.getUuid());
            spawnIndex++;
        }

        plugin.getChestManager().fillChests();

        broadcast("§8§m                                    ");
        broadcast("§a§lDAS SPIEL HAT BEGONNEN!");
        broadcast("");
        broadcast("§7Map: §e" + arena.getName());
        broadcast("§7Modus: " + arena.getMode().getDisplayName());
        broadcast("§7Spieler: §e" + alive.size());
        broadcast("");
        broadcast("§7Viel Erfolg!");
        broadcast("§8§m                                    ");

        playSound(Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
    }

    private void tickIngame() {
        gameTimer++;

        if (gameTimer > 0 && gameTimer % CHEST_REFILL_TIME == 0) {
            plugin.getChestManager().refillChests();
            broadcast("§6§lCHEST REFILL!");
            broadcast("§7Alle Truhen wurden neu gefüllt!");
            playSound(Sound.LEVEL_UP, 1.0f, 1.0f);
        }

        if (gameTimer < CHEST_REFILL_TIME) {
            int remaining = CHEST_REFILL_TIME - gameTimer;
            if (remaining == 60 || remaining == 30 || remaining == 10 || remaining == 5) {
                broadcast("§eChest Refill in §6" + remaining + " §eSekunden!");
            }
        }

        List<GamePlayer> alivePlayers = getAlivePlayers();
        if (alivePlayers.size() == 1) {
            end(alivePlayers.get(0));
        } else if (alivePlayers.isEmpty()) {
            end(null);
        }
    }

    private void tickEnding() {
    }

    private void tickRestarting() {
    }

    public void forceStart() {
        if (state != GameState.STARTING && state != GameState.LOBBY) return;

        if (getAlivePlayers().size() < 2) {
            broadcast("§cMindestens 2 Spieler werden benötigt!");
            return;
        }

        lobbyCountdown = 5;
        state = GameState.STARTING;
        broadcast("§6§lFORCESTART!");
        broadcast("§7Ein Admin hat den Countdown beschleunigt!");
    }

    public void end(GamePlayer winner) {
        state = GameState.ENDING;

        if (winner != null) {
            Player winnerPlayer = Bukkit.getPlayer(winner.getUuid());
            if (winnerPlayer != null) {
                String winnerName = plugin.getCore().getRankManager().getRankColor(winnerPlayer) +
                        winnerPlayer.getName();

                broadcast("§8§m                                    ");
                broadcast("§6§lSKYWARS");
                broadcast("");
                broadcast("§7Gewinner: " + winnerName);
                broadcast("");
                broadcast("§7Map: §e" + arena.getName());
                broadcast("§7Modus: " + arena.getMode().getDisplayName());
                broadcast("");
                broadcast("§7Kills: §e" + winner.getKills());
                broadcast("§7Spielzeit: §e" + formatTime(gameTimer));
                broadcast("");
                broadcast("§8§m                                    ");

                plugin.getStatsManager().addWin(winner.getUuid());
                plugin.getStatsManager().addKills(winner.getUuid(), winner.getKills());

                int coinReward = plugin.getConfig().getInt("rewards.win", 50) +
                        (winner.getKills() * plugin.getConfig().getInt("rewards.kill", 5));
                plugin.getCore().getCoinManager().addCoins(winner.getUuid(), coinReward);
                winnerPlayer.sendMessage(SkyWars.PREFIX + "§a+§6" + coinReward + " Coins");

                spawnFirework(winnerPlayer.getLocation());

                winnerPlayer.sendTitle("§6§lVICTORY!", "§7Du hast gewonnen!");
            }
        } else {
            broadcast("§8§m                                    ");
            broadcast("§6§lSKYWARS");
            broadcast("");
            broadcast("§cKein Gewinner! Unentschieden.");
            broadcast("§8§m                                    ");
        }

        for (GamePlayer gp : players.values()) {
            if (!gp.getUuid().equals(winner != null ? winner.getUuid() : null)) {
                plugin.getStatsManager().addLoss(gp.getUuid());
                plugin.getStatsManager().addKills(gp.getUuid(), gp.getKills());
                plugin.getStatsManager().addDeaths(gp.getUuid(), gp.getDeaths());
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            restart();
        }, 200L);
    }

    private void restart() {
        state = GameState.RESTARTING;
        broadcast("§c§lSERVER RESTART");
        broadcast("§7Der Server wird neugestartet...");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
        }, 40L);
    }

    public void addPlayer(Player player) {
        if (players.size() >= MAX_PLAYERS) {
            player.sendMessage(SkyWars.PREFIX + "§cDas Spiel ist voll!");
            return;
        }

        if (state != GameState.LOBBY && state != GameState.STARTING) {
            player.sendMessage(SkyWars.PREFIX + "§cDas Spiel läuft bereits!");
            return;
        }

        plugin.getLobbyManager().removeFromLobby(player.getUniqueId());

        GamePlayer gp = new GamePlayer(player.getUniqueId());
        players.put(player.getUniqueId(), gp);
        alive.add(player.getUniqueId());

        player.teleport(arena.getLobbySpawn());
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        giveLobbyItems(player);

        broadcast(plugin.getCore().getRankManager().getRankColor(player) + player.getName() +
                " §aist dem Spiel beigetreten! §7(" + players.size() + "/" + MAX_PLAYERS + ")");
    }

    public void removePlayer(Player player, boolean death) {
        UUID uuid = player.getUniqueId();
        GamePlayer gp = players.get(uuid);

        if (gp == null) return;

        if (death && state == GameState.INGAME) {
            gp.setAlive(false);
            alive.remove(uuid);

            player.teleport(arena.getSpectatorSpawn());
            plugin.getSpectatorManager().addSpectator(player);

            broadcast(plugin.getCore().getRankManager().getRankColor(player) + player.getName() +
                    " §7ist gestorben! §e(" + alive.size() + " §7übrig)");
        } else {
            players.remove(uuid);
            alive.remove(uuid);

            plugin.getLobbyManager().addToLobby(player);

            if (state == GameState.LOBBY || state == GameState.STARTING) {
                broadcast(plugin.getCore().getRankManager().getRankColor(player) + player.getName() +
                        " §chat das Spiel verlassen! §7(" + players.size() + "/" + MAX_PLAYERS + ")");
            }
        }
    }

    private void giveLobbyItems(Player player) {
        player.getInventory().setItem(0, plugin.getKitManager().getKitSelectorItem());
        player.getInventory().setItem(4, plugin.getStatsManager().getStatsItem());
        player.getInventory().setItem(8, plugin.getVoteManager().getVoteItem());
    }

    private void updateScoreboards() {
        for (GamePlayer gp : players.values()) {
            Player player = Bukkit.getPlayer(gp.getUuid());
            if (player == null || !player.isOnline()) continue;

            updateScoreboard(player);
        }
    }

    private void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("skywars", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§6§lSKYWARS");

        int line = 11;

        switch (state) {
            case LOBBY:
            case STARTING:
                setScore(obj, "§7", line--);
                setScore(obj, "§fMap: §e" + arena.getName(), line--);
                setScore(obj, "§fModus: " + arena.getMode().getDisplayName(), line--);
                setScore(obj, "§6", line--);
                setScore(obj, "§fSpieler: §e" + players.size() + "/" + MAX_PLAYERS, line--);
                setScore(obj, "§5", line--);
                if (state == GameState.STARTING) {
                    setScore(obj, "§fStart: §a" + lobbyCountdown + "s", line--);
                } else {
                    setScore(obj, "§7Warte auf Spieler...", line--);
                }
                break;

            case INGAME:
                GamePlayer gp = players.get(player.getUniqueId());
                setScore(obj, "§4", line--);
                setScore(obj, "§fSpieler: §e" + alive.size(), line--);
                setScore(obj, "§3", line--);
                setScore(obj, "§fKills: §e" + gp.getKills(), line--);
                setScore(obj, "§2", line--);

                int nextRefill = CHEST_REFILL_TIME - (gameTimer % CHEST_REFILL_TIME);
                setScore(obj, "§fRefill: §6" + formatTime(nextRefill), line--);
                setScore(obj, "§1", line--);
                setScore(obj, "§fZeit: §e" + formatTime(gameTimer), line--);
                break;
        }

        setScore(obj, "§8", line--);
        setScore(obj, "§eplay.homemc.org", line--);

        player.setScoreboard(board);
    }

    private void setScore(Objective obj, String text, int score) {
        obj.getScore(text).setScore(score);
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    public void broadcast(String message) {
        for (GamePlayer gp : players.values()) {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null && p.isOnline()) {
                p.sendMessage(SkyWars.PREFIX + message);
            }
        }

        for (UUID uuid : plugin.getSpectatorManager().getSpectators()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) {
                p.sendMessage(SkyWars.PREFIX + message);
            }
        }
    }

    public void playSound(Sound sound, float volume, float pitch) {
        for (GamePlayer gp : players.values()) {
            Player p = Bukkit.getPlayer(gp.getUuid());
            if (p != null && p.isOnline()) {
                p.playSound(p.getLocation(), sound, volume, pitch);
            }
        }
    }

    private void spawnFirework(Location loc) {
        for (int i = 0; i < 5; i++) {
            loc.getWorld().playEffect(loc.clone().add(0, i, 0), Effect.FIREWORKS_SPARK, 1);
        }
    }

    public List<GamePlayer> getAlivePlayers() {
        return players.values().stream()
                .filter(GamePlayer::isAlive)
                .collect(Collectors.toList());
    }

    public GameState getState() {
        return state;
    }

    public boolean isInGame(UUID uuid) {
        return players.containsKey(uuid);
    }

    public GamePlayer getGamePlayer(UUID uuid) {
        return players.get(uuid);
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return new HashMap<>(players);
    }

    public Arena getArena() {
        return arena;
    }
}