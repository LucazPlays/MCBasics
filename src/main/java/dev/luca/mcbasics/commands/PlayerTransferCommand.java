package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import dev.luca.mcbasics.api.TargetSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerTransferCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.PLAYERTRANSFER)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(FormattedMessage.create("playertransfer.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /playertransfer <player> <ip> <port></gradient>"));
            return true;
        }

        List<Player> targets = TargetSelector.selectPlayers(sender, args[0]);
        if (targets.isEmpty()) {
            sender.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
            return true;
        }

        String host = args[1];
        int port;
        try {
            port = Integer.parseInt(args[2]);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(FormattedMessage.create("playertransfer.invalid_port", "<gradient:#ff6b6b:#ee5a24>✖ Invalid port! Must be a number between 1 and 65535.</gradient>"));
            return true;
        }

        int count = 0;
        for (Player target : targets) {
            try {
                target.sendMessage(FormattedMessage.create("playertransfer.transferring", "<gradient:#48dbfb:#1dd1a1>✦ Transferring you to %host%:%port%...</gradient>", "host", host, "port", String.valueOf(port)));
                target.transfer(host, port);
                count++;
            } catch (Exception ex) {
                // Transfer may fail if not supported or player left; ignore per player
            }
        }

        if (count > 0) {
            if (count > 1) {
                sender.sendMessage(FormattedMessage.create("playertransfer.transferred_all", "<gradient:#48dbfb:#1dd1a1>✦ Transferred %count% players to %host%:%port%!</gradient>", "count", String.valueOf(count), "host", host, "port", String.valueOf(port)));
            } else {
                Player target = targets.get(0);
                sender.sendMessage(FormattedMessage.create("playertransfer.transferred", "<gradient:#48dbfb:#1dd1a1>✦ Transferred %target% to %host%:%port%!</gradient>", "target", target.getName(), "host", host, "port", String.valueOf(port)));
            }
        }

        return true;
    }
}
