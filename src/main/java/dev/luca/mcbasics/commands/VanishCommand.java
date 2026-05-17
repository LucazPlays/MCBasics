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

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.VANISH)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        List<Player> targets;

        if (args.length > 0) {
            targets = TargetSelector.selectPlayers(sender, args[0]);
            if (targets.isEmpty()) {
                sender.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            targets = Collections.singletonList((Player) sender);
        } else {
            sender.sendMessage(FormattedMessage.create("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        for (Player target : targets) {
            if (VanishManager.isVanished(target)) {
                VanishManager.showPlayer(target);
                target.sendMessage(FormattedMessage.create("vanish.you_visible", "<gradient:#48dbfb:#1dd1a1>✦ You are now visible!</gradient>"));
            } else {
                VanishManager.hidePlayer(target);
                target.sendMessage(FormattedMessage.create("vanish.you_vanished", "<gradient:#48dbfb:#1dd1a1>✦ You are now vanished!</gradient>"));
            }
        }

        if (targets.size() > 1) {
            sender.sendMessage(FormattedMessage.create("vanish.toggled_all", "<gradient:#48dbfb:#1dd1a1>✦ Vanish toggled for %count% players!</gradient>", "count", String.valueOf(targets.size())));
        } else if (targets.get(0) != sender) {
            Player target = targets.get(0);
            if (VanishManager.isVanished(target)) {
                sender.sendMessage(FormattedMessage.create("vanish.now_vanished", "<gradient:#48dbfb:#1dd1a1>✦ %target% is now vanished!</gradient>", "target", target.getName()));
            } else {
                sender.sendMessage(FormattedMessage.create("vanish.now_visible", "<gradient:#48dbfb:#1dd1a1>✦ %target% is now visible!</gradient>", "target", target.getName()));
            }
        }

        return true;
    }
}
