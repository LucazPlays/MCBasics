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
            sender.sendMessage(Message.get("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.FLY_OTHERS)) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Message.get("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.get("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
            return true;
        }

        boolean newFlyState = !target.getAllowFlight();

        target.setAllowFlight(newFlyState);
        target.setFlying(newFlyState);

        if (newFlyState) {
            target.sendMessage(Message.get("fly.enabled", "<gradient:#48dbfb:#1dd1a1>✦ Flight enabled!</gradient>"));
            if (target != sender) {
                sender.sendMessage(Message.get("fly.enabled_other", "<gradient:#48dbfb:#1dd1a1>✦ Flight enabled for %target%!</gradient>", "target", target.getName()));
            }
        } else {
            target.sendMessage(Message.get("fly.disabled", "<gradient:#ff6b6b:#ee5a24>✦ Flight disabled!</gradient>"));
            if (target != sender) {
                sender.sendMessage(Message.get("fly.disabled_other", "<gradient:#ff6b6b:#ee5a24>✦ Flight disabled for %target%!</gradient>", "target", target.getName()));
            }
        }

        return true;
    }
}
