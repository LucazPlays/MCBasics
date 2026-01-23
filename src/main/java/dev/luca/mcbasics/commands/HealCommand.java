package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.HEAL)) {
            sender.sendMessage(Message.get("general.no_permission", ""));
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.HEAL_OTHERS)) {
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

        target.setHealth(20.0);
        target.setFireTicks(0);
        target.sendMessage(Message.get("heal.healed", ""));

        if (target != sender) {
            sender.sendMessage(Message.get("heal.healed_other", "", "target", target.getName()));
        }

        return true;
    }
}
