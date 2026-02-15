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
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("@a")) {
            if (!sender.hasPermission(Permission.FLY_OTHERS)) {
                sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
                return true;
            }

            int count = 0;
            for (Player target : Bukkit.getOnlinePlayers()) {
                boolean newFlyState = !target.getAllowFlight();
                target.setAllowFlight(newFlyState);
                target.setFlying(newFlyState);

                if (newFlyState) {
                    target.sendMessage(Message.getComponent("fly.enabled", "<gradient:#48dbfb:#1dd1a1>✦ Flight enabled!</gradient>"));
                } else {
                    target.sendMessage(Message.getComponent("fly.disabled", "<gradient:#ff6b6b:#ee5a24>✦ Flight disabled!</gradient>"));
                }
                count++;
            }
            sender.sendMessage(Message.getComponent("fly.toggled_all", "<gradient:#48dbfb:#1dd1a1>✦ Flight toggled for %count% players!</gradient>", "count", String.valueOf(count)));
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.FLY_OTHERS)) {
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

        boolean newFlyState = !target.getAllowFlight();

        target.setAllowFlight(newFlyState);
        target.setFlying(newFlyState);

        if (newFlyState) {
            target.sendMessage(Message.getComponent("fly.enabled", "<gradient:#48dbfb:#1dd1a1>✦ Flight enabled!</gradient>"));
            if (target != sender) {
                sender.sendMessage(Message.getComponent("fly.enabled_other", "<gradient:#48dbfb:#1dd1a1>✦ Flight enabled for %target%!</gradient>", "target", target.getName()));
            }
        } else {
            target.sendMessage(Message.getComponent("fly.disabled", "<gradient:#ff6b6b:#ee5a24>✦ Flight disabled!</gradient>"));
            if (target != sender) {
                sender.sendMessage(Message.getComponent("fly.disabled_other", "<gradient:#ff6b6b:#ee5a24>✦ Flight disabled for %target%!</gradient>", "target", target.getName()));
            }
        }

        return true;
    }
}
