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

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Player> targets;

        if (args.length > 0) {
            if (!sender.hasPermission(Permission.PING_OTHERS)) {
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
            int ping = target.getPing();
            String pingDisplay = "<gradient:#48dbfb:#1dd1a1>" + ping + "ms</gradient>";
            sender.sendMessage(FormattedMessage.create("ping.message", "<gradient:#48dbfb:#1dd1a1>✦ %target%'s ping: " + pingDisplay + "</gradient>", "target", target.getName()));
        }

        return true;
    }
}
