package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.VANISH)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(FormattedMessage.create("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        Player player = (Player) sender;
        Player target = player;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        }

        if (VanishManager.isVanished(target)) {
            VanishManager.showPlayer(target);
            player.sendMessage(FormattedMessage.create("vanish.now_visible", "<gradient:#48dbfb:#1dd1a1>✦ %target% is now visible!</gradient>", "target", target.getName()));
            if (target != player) {
                target.sendMessage(FormattedMessage.create("vanish.you_visible", "<gradient:#48dbfb:#1dd1a1>✦ You are now visible!</gradient>"));
            }
        } else {
            VanishManager.hidePlayer(target);
            player.sendMessage(FormattedMessage.create("vanish.now_vanished", "<gradient:#48dbfb:#1dd1a1>✦ %target% is now vanished!</gradient>", "target", target.getName()));
            if (target != player) {
                target.sendMessage(FormattedMessage.create("vanish.you_vanished", "<gradient:#48dbfb:#1dd1a1>✦ You are now vanished!</gradient>"));
            }
        }

        return true;
    }
}
