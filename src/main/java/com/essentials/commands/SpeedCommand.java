package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.speed")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player target;
        float speed = 1.0f;

        if (args.length > 0) {
            try {
                int speedInt = Integer.parseInt(args[0]);
                if (speedInt < 1 || speedInt > 10) {
                    sender.sendMessage("§cSpeed must be between 1 and 10!");
                    return true;
                }
                speed = speedInt / 10.0f;
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid speed number!");
                return true;
            }
        }

        boolean isFlying = label.equalsIgnoreCase("flyspeed");
        String speedType = isFlying ? "flight" : "walking";

        if (args.length > 1 && sender.hasPermission("essentials.speed.others")) {
            target = Bukkit.getPlayer(args[1]);
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

        if (isFlying) {
            target.setFlySpeed(speed);
        } else {
            target.setWalkSpeed(speed);
        }

        target.sendMessage("§aYour " + speedType + " speed has been set to " + (int)(speed * 10) + "!");

        if (target != sender) {
            sender.sendMessage("§a" + target.getName() + "'s " + speedType + " speed set to " + (int)(speed * 10) + "!");
        }

        return true;
    }
}
