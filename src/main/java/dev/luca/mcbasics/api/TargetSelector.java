package dev.luca.mcbasics.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TargetSelector {

    /**
     * Selects players based on the provided argument.
     * Supports vanilla entity selectors (@a, @p, @r, @s) and player names.
     *
     * @param sender The command sender
     * @param arg    The target argument
     * @return A list of matching players
     */
    public static List<Player> selectPlayers(CommandSender sender, String arg) {
        if (arg == null || arg.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // Try vanilla selectors first
            List<Entity> entities = Bukkit.selectEntities(sender, arg);
            return entities.stream()
                    .filter(entity -> entity instanceof Player)
                    .map(entity -> (Player) entity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            // Not a selector, try as a player name
            Player player = Bukkit.getPlayer(arg);
            if (player != null) {
                return Collections.singletonList(player);
            }
        }

        return Collections.emptyList();
    }
}
