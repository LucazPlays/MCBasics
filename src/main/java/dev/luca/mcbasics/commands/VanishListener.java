package dev.luca.mcbasics.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VanishListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiner = event.getPlayer();

        VanishManager.applyVanishOnJoin(joiner);
        VanishManager.hideVanishedForJoiner(joiner);
    }
}
