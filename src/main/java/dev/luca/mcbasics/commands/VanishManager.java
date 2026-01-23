package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class VanishManager {

    private static final Set<Player> vanishedPlayers = new HashSet<>();

    public static boolean isVanished(Player player) {
        return vanishedPlayers.contains(player);
    }

    public static void hidePlayer(Player player) {
        vanishedPlayers.add(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission(Permission.VANISH_SEE)) {
                onlinePlayer.hidePlayer(player);
            }
        }
        player.setCanPickupItems(false);
    }

    public static void showPlayer(Player player) {
        vanishedPlayers.remove(player);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
        }
        player.setCanPickupItems(true);
    }
}
