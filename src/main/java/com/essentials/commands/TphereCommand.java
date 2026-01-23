package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TphereCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.tphere")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /tphere <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to teleport others to you!");
            return true;
        }

        Player teleporter = (Player) sender;

        if (target.equals(teleporter)) {
            sender.sendMessage("§cYou can't teleport yourself!");
            return true;
        }

        target.teleport(teleporter.getLocation());
        target.sendMessage("§aYou have been teleported to " + teleporter.getName() + "!");
        teleporter.sendMessage("§a" + target.getName() + " has been teleported to you!");

        return true;
    }
}
