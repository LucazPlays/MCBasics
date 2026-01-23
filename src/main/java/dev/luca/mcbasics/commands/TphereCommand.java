package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TphereCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.tphere")) {
            sender.sendMessage(Message.get("general.no_permission", ""));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.get("tphere.usage", "/tphere <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.get("general.player_not_found", ""));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.get("general.must_be_player", ""));
            return true;
        }

        Player teleporter = (Player) sender;

        if (target.equals(teleporter)) {
            sender.sendMessage(Message.get("general.you_cant_target_self", ""));
            return true;
        }

        target.teleport(teleporter.getLocation());
        target.sendMessage(Message.get("tphere.you_teleported", "", "sender", teleporter.getName()));
        teleporter.sendMessage(Message.get("tphere.teleported", "", "target", target.getName()));

        return true;
    }
}
