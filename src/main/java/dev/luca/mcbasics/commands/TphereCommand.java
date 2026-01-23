package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TphereCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.TPHERE)) {
            sender.sendMessage(Message.get("general.no_permission", "&cYou don't have permission!"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.get("tphere.usage", "&cUsage: /tphere <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.get("general.player_not_found", "&cPlayer not found!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.get("general.must_be_player", "&cThis command can only be used by players!"));
            return true;
        }

        Player teleporter = (Player) sender;

        if (target.equals(teleporter)) {
            sender.sendMessage(Message.get("general.you_cant_target_self", "&cYou can't target yourself!"));
            return true;
        }

        target.teleport(teleporter.getLocation());
        target.sendMessage(Message.get("tphere.you_teleported", "You have been teleported to %sender%!", "sender", teleporter.getName()));
        teleporter.sendMessage(Message.get("tphere.teleported", "%target% has been teleported to you!", "target", target.getName()));

        return true;
    }
}
