package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.MCBasics;
import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import dev.luca.mcbasics.api.TargetSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerTransferCommand implements CommandExecutor {

    private static final int DEFAULT_PORT = 25565;
    private static final long DEFAULT_INTERVAL_MS = 1000;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.PLAYERTRANSFER)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(FormattedMessage.create("playertransfer.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /playertransfer <player> <ip> [port] [millis]</gradient>"));
            return true;
        }

        List<Player> targets = TargetSelector.selectPlayers(sender, args[0]);
        if (targets.isEmpty()) {
            sender.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
            return true;
        }

        String host = args[1];
        int port = DEFAULT_PORT;
        long intervalMs = DEFAULT_INTERVAL_MS;

        if (args.length >= 3) {
            try {
                port = Integer.parseInt(args[2]);
                if (port < 1 || port > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(FormattedMessage.create("playertransfer.invalid_port", "<gradient:#ff6b6b:#ee5a24>✖ Invalid port! Must be a number between 1 and 65535.</gradient>"));
                return true;
            }
        }

        if (args.length >= 4) {
            try {
                intervalMs = Long.parseLong(args[3]);
                if (intervalMs < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(FormattedMessage.create("playertransfer.invalid_interval", "<gradient:#ff6b6b:#ee5a24>✖ Invalid interval! Must be a positive number in milliseconds.</gradient>"));
                return true;
            }
        }

        transferPlayers(sender, targets, host, port, intervalMs);
        return true;
    }

    private void transferPlayers(CommandSender sender, List<Player> targets, String host, int port, long intervalMs) {
        if (targets.size() == 1 || intervalMs == 0) {
            int count = 0;
            for (Player target : targets) {
                try {
                    target.sendMessage(FormattedMessage.create("playertransfer.transferring", "<gradient:#48dbfb:#1dd1a1>✦ Transferring you to %host%:%port%...</gradient>", "host", host, "port", String.valueOf(port)));
                    target.transfer(host, port);
                    count++;
                } catch (Exception ex) {
                    // Transfer may fail if not supported or player left
                }
            }
            sendCompletionMessage(sender, count, targets, host, port);
        } else {
            MCBasics plugin = MCBasics.getInstance();
            long delayTicks = intervalMs / 50;

            for (int i = 0; i < targets.size(); i++) {
                final Player target = targets.get(i);
                final int index = i;

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            target.sendMessage(FormattedMessage.create("playertransfer.transferring", "<gradient:#48dbfb:#1dd1a1>✦ Transferring you to %host%:%port%...</gradient>", "host", host, "port", String.valueOf(port)));
                            target.transfer(host, port);
                        } catch (Exception ex) {
                            // Transfer may fail if not supported or player left
                        }
                        if (index == targets.size() - 1) {
                            sendCompletionMessage(sender, targets.size(), targets, host, port);
                        }
                    }
                }.runTaskLater(plugin, i * delayTicks);
            }
        }
    }

    private void sendCompletionMessage(CommandSender sender, int count, List<Player> targets, String host, int port) {
        if (count > 0) {
            if (count > 1) {
                sender.sendMessage(FormattedMessage.create("playertransfer.transferred_all", "<gradient:#48dbfb:#1dd1a1>✦ Transferred %count% players to %host%:%port%!</gradient>", "count", String.valueOf(count), "host", host, "port", String.valueOf(port)));
            } else {
                Player target = targets.get(0);
                sender.sendMessage(FormattedMessage.create("playertransfer.transferred", "<gradient:#48dbfb:#1dd1a1>✦ Transferred %target% to %host%:%port%!</gradient>", "target", target.getName(), "host", host, "port", String.valueOf(port)));
            }
        }
    }
}
