package dev.luca.mcbasics.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

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
            if (!onlinePlayer.hasPermission("essentials.vanish.see")) {
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
