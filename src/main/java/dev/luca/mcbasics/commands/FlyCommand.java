package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.FLY)) {
            sender.sendMessage(Message.get("general.no_permission", ""));
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.FLY_OTHERS)) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Message.get("general.player_not_found", ""));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.get("general.specify_player", ""));
            return true;
        }

        boolean newFlyState = !target.getAllowFlight();

        target.setAllowFlight(newFlyState);
        target.setFlying(newFlyState);

        if (newFlyState) {
            target.sendMessage(Message.get("fly.enabled", ""));
            if (target != sender) {
                sender.sendMessage(Message.get("fly.enabled_other", "", "target", target.getName()));
            }
        } else {
            target.sendMessage(Message.get("fly.disabled", ""));
            if (target != sender) {
                sender.sendMessage(Message.get("fly.disabled_other", "", "target", target.getName()));
            }
        }

        return true;
    }
}
