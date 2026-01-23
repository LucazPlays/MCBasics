package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.feed")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission("essentials.feed.others")) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cSpecify a player!");
            return true;
        }

        target.setFoodLevel(20);
        target.setSaturation(20f);
        target.sendMessage("§aYou have been fed!");

        if (target != sender) {
            sender.sendMessage("§a" + target.getName() + " has been fed!");
        }

        return true;
    }
}
