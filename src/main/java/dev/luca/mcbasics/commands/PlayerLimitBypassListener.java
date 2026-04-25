package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Permission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLimitBypassListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.KICK_FULL) {
            return;
        }

        if (!event.getPlayer().hasPermission(Permission.BYPASS_PLAYER_LIMIT)) {
            return;
        }

        event.allow();
    }
}
