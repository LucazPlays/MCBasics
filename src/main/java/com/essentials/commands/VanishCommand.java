package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        Player target = player;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cPlayer not found!");
                return true;
            }
        }

        if (VanishManager.isVanished(target)) {
            VanishManager.showPlayer(target);
            player.sendMessage("§a" + target.getName() + " is now visible!");
            if (target != player) {
                target.sendMessage("§aYou are now visible!");
            }
        } else {
            VanishManager.hidePlayer(target);
            player.sendMessage("§a" + target.getName() + " is now vanished!");
            if (target != player) {
                target.sendMessage("§aYou are now vanished!");
            }
        }

        return true;
    }
}
