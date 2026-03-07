package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TphereCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.TPHERE)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(FormattedMessage.create("tphere.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /tphere <player></gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(FormattedMessage.create("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        Player teleporter = (Player) sender;

        if (args[0].equalsIgnoreCase("@a")) {
            int count = 0;
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target.equals(teleporter)) continue;
                target.teleport(teleporter.getLocation());
                target.sendMessage(FormattedMessage.create("tphere.you_teleported", "<gradient:#48dbfb:#1dd1a1>✦ You have been teleported to %sender%!</gradient>", "sender", teleporter.getName()));
                count++;
            }
            teleporter.sendMessage(FormattedMessage.create("tphere.teleported_all", "<gradient:#48dbfb:#1dd1a1>✦ %count% players teleported to you!</gradient>", "count", String.valueOf(count)));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
            return true;
        }

        if (target.equals(teleporter)) {
            sender.sendMessage(FormattedMessage.create("general.you_cant_target_self", "<gradient:#ff6b6b:#ee5a24>✖ You can't target yourself!</gradient>"));
            return true;
        }

        target.teleport(teleporter.getLocation());
        target.sendMessage(FormattedMessage.create("tphere.you_teleported", "<gradient:#48dbfb:#1dd1a1>✦ You have been teleported to %sender%!</gradient>", "sender", teleporter.getName()));
        teleporter.sendMessage(FormattedMessage.create("tphere.teleported", "<gradient:#48dbfb:#1dd1a1>✦ %target% has been teleported to you!</gradient>", "target", target.getName()));

        return true;
    }
}
