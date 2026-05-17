package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import dev.luca.mcbasics.api.TargetSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.FLY)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        List<Player> targets;

        if (args.length > 0) {
            if (!sender.hasPermission(Permission.FLY_OTHERS)) {
                sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
                return true;
            }
            targets = TargetSelector.selectPlayers(sender, args[0]);
            if (targets.isEmpty()) {
                sender.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            targets = Collections.singletonList((Player) sender);
        } else {
            sender.sendMessage(FormattedMessage.create("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
            return true;
        }

        for (Player target : targets) {
            boolean newFlyState = !target.getAllowFlight();
            target.setAllowFlight(newFlyState);
            target.setFlying(newFlyState);

            if (newFlyState) {
                target.sendMessage(FormattedMessage.create("fly.enabled", "<gradient:#48dbfb:#1dd1a1>✦ Flight enabled!</gradient>"));
            } else {
                target.sendMessage(FormattedMessage.create("fly.disabled", "<gradient:#ff6b6b:#ee5a24>✦ Flight disabled!</gradient>"));
            }
        }

        if (targets.size() > 1) {
            sender.sendMessage(FormattedMessage.create("fly.toggled_all", "<gradient:#48dbfb:#1dd1a1>✦ Flight toggled for %count% players!</gradient>", "count", String.valueOf(targets.size())));
        } else if (targets.get(0) != sender) {
            Player target = targets.get(0);
            if (target.getAllowFlight()) {
                sender.sendMessage(FormattedMessage.create("fly.enabled_other", "<gradient:#48dbfb:#1dd1a1>✦ Flight enabled for %target%!</gradient>", "target", target.getName()));
            } else {
                sender.sendMessage(FormattedMessage.create("fly.disabled_other", "<gradient:#ff6b6b:#ee5a24>✦ Flight disabled for %target%!</gradient>", "target", target.getName()));
            }
        }

        return true;
    }
}
