package dev.luca.mcbasics.api;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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

    /**
     * Returns tab completion suggestions for a player target argument.
     * Includes online player names (filtered by prefix) and vanilla selectors (@a, @p, @r, @s).
     *
     * @param partial The current partial input (may be empty)
     * @return Filtered list of suggestions
     */
    public static List<String> getPlayerTabCompletions(String partial) {
        return getPlayerTabCompletions(partial, true);
    }

    /**
     * Returns tab completion suggestions for a player target argument.
     *
     * @param partial          The current partial input (may be empty)
     * @param includeSelectors Whether to include @a, @p, @r, @s selectors
     * @return Filtered list of suggestions
     */
    public static List<String> getPlayerTabCompletions(String partial, boolean includeSelectors) {
        Set<String> completions = new LinkedHashSet<>();
        String p = (partial == null ? "" : partial).toLowerCase();

        if (includeSelectors) {
            if ("@a".startsWith(p)) completions.add("@a");
            if ("@p".startsWith(p)) completions.add("@p");
            if ("@r".startsWith(p)) completions.add("@r");
            if ("@s".startsWith(p)) completions.add("@s");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(p)) {
                completions.add(player.getName());
            }
        }
        return new ArrayList<>(completions);
    }
}
