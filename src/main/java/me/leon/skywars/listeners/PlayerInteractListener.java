package me.leon.skywars.listeners;

import me.leon.skywars.SkyWars;
import me.leon.skywars.game.Game;
import me.leon.skywars.game.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    private final SkyWars plugin;

    public PlayerInteractListener(SkyWars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        String displayName = item.getItemMeta().getDisplayName();

        // Lobby Items (wenn Spieler in der Lobby ist)
        if (plugin.getLobbyManager().isInLobby(player.getUniqueId())) {
            event.setCancelled(true);

            // Spielen Item (Compass)
            if (item.getType() == Material.COMPASS && displayName.contains("Spielen")) {
                if (plugin.getGameManager().getCurrentGame() != null) {
                    plugin.getLobbyManager().removeFromLobby(player.getUniqueId());
                    plugin.getGameManager().addPlayer(player);
                } else {
                    player.sendMessage(SkyWars.PREFIX + "§cKein Spiel verfügbar!");
                }
            }

            // Stats Item (Paper)
            else if (item.getType() == Material.PAPER && displayName.contains("Statistiken")) {
                plugin.getStatsManager().showStats(player, player);
            }

            // Zurück zur Lobby (Red Dye)
            else if (item.getType() == Material.INK_SACK && displayName.contains("Zurück zur Lobby")) {
                plugin.getLobbyManager().sendToMainLobby(player);
            }
            return;
        }

        // Game Items (wenn Spieler im Spiel ist)
        Game game = plugin.getGameManager().getCurrentGame();
        if (game == null) return;

        // Nur in LOBBY und STARTING Phase Items nutzbar
        if (game.getState() == GameState.LOBBY || game.getState() == GameState.STARTING) {
            event.setCancelled(true);

            if (displayName.contains("Kit Auswahl")) {
                plugin.getKitManager().openKitSelector(player);
            } else if (displayName.contains("Statistiken")) {
                plugin.getStatsManager().showStats(player, player);
            } else if (displayName.contains("Abstimmung")) {
                plugin.getVoteManager().openVoteMenu(player);
            }
        }
        // INGAME Phase - normale Interaktionen erlaubt, keine Cancellation
    }
}