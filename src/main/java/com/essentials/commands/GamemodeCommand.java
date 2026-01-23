package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.gm")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        Player target;
        GameMode mode;

        if (label.equalsIgnoreCase("gmc")) {
            mode = GameMode.CREATIVE;
        } else if (label.equalsIgnoreCase("gms")) {
            mode = GameMode.SURVIVAL;
        } else if (label.equalsIgnoreCase("gma")) {
            mode = GameMode.ADVENTURE;
        } else if (label.equalsIgnoreCase("gmsp")) {
            mode = GameMode.SPECTATOR;
        } else {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cUsage: /gm <0/1/2/3> [player]");
                    return true;
                }
                target = (Player) sender;
            } else {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage("§cPlayer not found!");
                    return true;
                }
            }

            try {
                int modeNum = Integer.parseInt(args[0]);
                switch (modeNum) {
                    case 0:
                        mode = GameMode.SURVIVAL;
                        break;
                    case 1:
                        mode = GameMode.CREATIVE;
                        break;
                    case 2:
                        mode = GameMode.ADVENTURE;
                        break;
                    case 3:
                        mode = GameMode.SPECTATOR;
                        break;
                    default:
                        sender.sendMessage("§cInvalid gamemode! Use 0, 1, 2, or 3");
                        return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid gamemode number!");
                return true;
            }
        }

        if (args.length > 1 && sender.hasPermission("essentials.gm.others")) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
        } else if (!(sender instanceof Player) && label.equalsIgnoreCase("gm")) {
            sender.sendMessage("§cSpecify a player!");
            return true;
        } else if (label.equalsIgnoreCase("gm")) {
            target = (Player) sender;
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage("§cYou must specify a player!");
            return true;
        }

        if (target == null) {
            sender.sendMessage("§cTarget player not found!");
            return true;
        }

        target.setGameMode(mode);
        String modeName = mode.name().toLowerCase();
        target.sendMessage("§aYour gamemode has been set to " + modeName + "!");
        if (target != sender) {
            sender.sendMessage("§a" + target.getName() + "'s gamemode set to " + modeName + "!");
        }

        return true;
    }
}
