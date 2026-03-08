package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommand implements CommandExecutor {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean showUuids = args.length > 0 && args[0].equalsIgnoreCase("uuids");

        if (showUuids && !sender.hasPermission(Permission.LIST_UUIDS)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        int onlineCount = onlinePlayers.size();
        int maxPlayers = Bukkit.getMaxPlayers();

        Component header = miniMessage.deserialize("<gradient:#48dbfb:#1dd1a1>✦ Online Players</gradient> <gray>(</gray><gradient:#feca57:#ff9ff3>" + onlineCount + "</gradient><gray>/</gray><gradient:#54a0ff:#5f27cd>" + maxPlayers + "</gradient><gray>)</gray>");
        sender.sendMessage(header);

        if (onlineCount == 0) {
            sender.sendMessage(Component.text("  No players online.", NamedTextColor.GRAY));
            return true;
        }

        for (Player player : onlinePlayers) {
            Component playerLine;
            
            if (showUuids) {
                String uuid = player.getUniqueId().toString();
                String shortUuid = uuid.substring(0, 8) + "..." + uuid.substring(uuid.length() - 4);
                playerLine = Component.text()
                        .append(Component.text("  ", NamedTextColor.DARK_GRAY))
                        .append(miniMessage.deserialize("<gradient:#ff6b6b:#feca57>➜</gradient>"))
                        .append(Component.text(" ", NamedTextColor.WHITE))
                        .append(miniMessage.deserialize("<gradient:#48dbfb:#1dd1a1>" + player.getName() + "</gradient>"))
                        .append(Component.text(" ", NamedTextColor.GRAY))
                        .append(Component.text("(", NamedTextColor.DARK_GRAY))
                        .append(miniMessage.deserialize("<gradient:#54a0ff:#5f27cd>" + shortUuid + "</gradient>"))
                        .append(Component.text(")", NamedTextColor.DARK_GRAY))
                        .build();
            } else {
                playerLine = Component.text()
                        .append(Component.text("  ", NamedTextColor.DARK_GRAY))
                        .append(miniMessage.deserialize("<gradient:#ff6b6b:#feca57>➜</gradient>"))
                        .append(Component.text(" ", NamedTextColor.WHITE))
                        .append(miniMessage.deserialize("<gradient:#48dbfb:#1dd1a1>" + player.getName() + "</gradient>"))
                        .build();
            }
            
            sender.sendMessage(playerLine);
        }

        Component footer = Component.text()
                .append(Component.text("  ", NamedTextColor.DARK_GRAY))
                .append(miniMessage.deserialize("<gradient:#ff6b6b:#ee5a24>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gradient>"))
                .build();
        sender.sendMessage(footer);

        return true;
    }
}
