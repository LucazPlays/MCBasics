package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.fly")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission("essentials.fly.others")) {
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

        boolean newFlyState = !target.getAllowFlight();

        target.setAllowFlight(newFlyState);
        target.setFlying(newFlyState);

        if (newFlyState) {
            target.sendMessage("§aFlight enabled!");
        } else {
            target.sendMessage("§cFlight disabled!");
        }

        if (target != sender) {
            if (newFlyState) {
                sender.sendMessage("§aFlight enabled for " + target.getName() + "!");
            } else {
                sender.sendMessage("§cFlight disabled for " + target.getName() + "!");
            }
        }

        return true;
    }
}
