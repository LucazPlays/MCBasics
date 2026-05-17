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

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.FEED)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        List<Player> targets;

        if (args.length > 0) {
            if (!sender.hasPermission(Permission.FEED_OTHERS)) {
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
            target.setFoodLevel(20);
            target.setSaturation(20f);
            target.sendMessage(FormattedMessage.create("feed.fed", "<gradient:#48dbfb:#1dd1a1>✦ You have been fed!</gradient>"));
        }

        if (targets.size() > 1) {
            sender.sendMessage(FormattedMessage.create("feed.fed_all", "<gradient:#48dbfb:#1dd1a1>✦ All players (%count%) have been fed!</gradient>", "count", String.valueOf(targets.size())));
        } else if (targets.get(0) != sender) {
            sender.sendMessage(FormattedMessage.create("feed.fed_other", "<gradient:#48dbfb:#1dd1a1>✦ %target% has been fed!</gradient>", "target", targets.get(0).getName()));
        }

        return true;
    }
}
