package me.leon.skywars.arena;

public enum ArenaMode {
    SOLO(8, "1x8", "§eSolo"),
    DUOS(8, "2x8", "§bDuos"),
    TRIOS(6, "3x6", "§aTrios"),
    SQUADS(4, "4x4", "§cSquads");

    private final int requiredSpawns;
    private final String displayShort;
    private final String displayName;

    ArenaMode(int requiredSpawns, String displayShort, String displayName) {
        this.requiredSpawns = requiredSpawns;
        this.displayShort = displayShort;
        this.displayName = displayName;
    }

    public int getRequiredSpawns() { return requiredSpawns; }
    public String getDisplayShort() { return displayShort; }
    public String getDisplayName() { return displayName; }

    public int getMaxPlayers() {
        switch (this) {
            case SOLO: return 8;
            case DUOS: return 16;
            case TRIOS: return 18;
            case SQUADS: return 16;
            default: return 8;
        }
    }

    public int getTeamSize() {
        switch (this) {
            case SOLO: return 1;
            case DUOS: return 2;
            case TRIOS: return 3;
            case SQUADS: return 4;
            default: return 1;
        }
    }
}