package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.heal")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission("essentials.heal.others")) {
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

        target.setHealth(20.0);
        target.setFireTicks(0);
        target.sendMessage("§aYou have been healed!");

        if (target != sender) {
            sender.sendMessage("§a" + target.getName() + " has been healed!");
        }

        return true;
    }
}
