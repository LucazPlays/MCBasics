package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.FEED)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("@a")) {
            if (!sender.hasPermission(Permission.FEED_OTHERS)) {
                sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
                return true;
            }

            int count = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setFoodLevel(20);
                player.setSaturation(20f);
                player.sendMessage(Message.getComponent("feed.fed", "<gradient:#48dbfb:#1dd1a1>✦ You have been fed!</gradient>"));
                count++;
            }

            sender.sendMessage(Message.getComponent("feed.fed_all", "<gradient:#48dbfb:#1dd1a1>✦ All players (%count%) have been fed!</gradient>", "count", String.valueOf(count)));
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.FEED_OTHERS)) {
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

        target.setFoodLevel(20);
        target.setSaturation(20f);
        target.sendMessage(Message.getComponent("feed.fed", "<gradient:#48dbfb:#1dd1a1>✦ You have been fed!</gradient>"));

        if (target != sender) {
            sender.sendMessage(Message.getComponent("feed.fed_other", "<gradient:#48dbfb:#1dd1a1>✦ %target% has been fed!</gradient>", "target", target.getName()));
        }

        return true;
    }
}
