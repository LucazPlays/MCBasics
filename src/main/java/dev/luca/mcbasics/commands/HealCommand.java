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
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("@a")) {
            if (!sender.hasPermission(Permission.HEAL_OTHERS)) {
                sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
                return true;
            }

            int count = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setHealth(20.0);
                player.setFireTicks(0);
                player.sendMessage(Message.getComponent("heal.healed", "<gradient:#48dbfb:#1dd1a1>✦ You have been healed!</gradient>"));
                count++;
            }

            sender.sendMessage(Message.getComponent("heal.healed_all", "<gradient:#48dbfb:#1dd1a1>✦ All players (%count%) have been healed!</gradient>", "count", String.valueOf(count)));
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.HEAL_OTHERS)) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.getComponent("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
            return true;
        }

        target.setHealth(20.0);
        target.setFireTicks(0);
        target.sendMessage(Message.getComponent("heal.healed", "<gradient:#48dbfb:#1dd1a1>✦ You have been healed!</gradient>"));

        if (target != sender) {
            sender.sendMessage(Message.getComponent("heal.healed_other", "<gradient:#48dbfb:#1dd1a1>✦ %target% has been healed!</gradient>", "target", target.getName()));
        }

        return true;
    }
}
