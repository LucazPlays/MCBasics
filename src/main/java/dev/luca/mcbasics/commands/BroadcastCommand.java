package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.BROADCAST)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.getComponent("broadcast.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /broadcast <message></gradient>"));
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (String arg : args) {
            messageBuilder.append(arg).append(" ");
        }
        String message = messageBuilder.toString().trim();

        Component broadcastMessage = Component.text()
                .append(Component.text("[Broadcast] ", NamedTextColor.GOLD))
                .append(Component.text(message, NamedTextColor.WHITE))
                .build();

        Bukkit.broadcast(broadcastMessage);

        return true;
    }
}
