package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.PING_OTHERS)) {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        int ping = target.getPing();

        String pingColor;
        if (ping < 50) {
            pingColor = "<green>";
        } else if (ping < 100) {
            pingColor = "<yellow>";
        } else if (ping < 200) {
            pingColor = "<orange>";
        } else {
            pingColor = "<red>";
        }

        String pingDisplay = "<gradient:#48dbfb:#1dd1a1>" + ping + "ms</gradient>";

        sender.sendMessage(Message.getComponent("ping.message", "<gradient:#48dbfb:#1dd1a1>✦ %target%'s ping: " + pingDisplay + "</gradient>", "target", target.getName()));

        return true;
    }
}
