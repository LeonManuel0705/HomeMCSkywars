package me.leon.skywars.game;

import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;
    private boolean alive;
    private int kills;
    private int deaths;
    private String selectedKit;

    public GamePlayer(UUID uuid) {
        this.uuid = uuid;
        this.alive = true;
        this.kills = 0;
        this.deaths = 0;
        this.selectedKit = "default";
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        this.deaths++;
    }

    public String getSelectedKit() {
        return selectedKit;
    }

    public void setSelectedKit(String selectedKit) {
        this.selectedKit = selectedKit;
    }
}
