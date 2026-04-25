package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import net.kyori.adventure.text.Component;
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
    private static final int PLAYERS_PER_LINE = 3;
    private static final int PLAYERS_PER_LINE_WITH_UUIDS = 2;
    private static final String BORDER = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

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

        sender.sendMessage(styledLine("✦ Online Players"));
        sender.sendMessage(styledDivider());

        if (onlineCount == 0) {
            sender.sendMessage(styledLine("No players online right now."));
            sender.sendMessage(styledDivider());
            sender.sendMessage(styledLine("Currently online: " + onlineCount + " / " + maxPlayers));
            return true;
        }

        int playersPerLine = showUuids ? PLAYERS_PER_LINE_WITH_UUIDS : PLAYERS_PER_LINE;
        List<String> playerEntries = new ArrayList<>(onlinePlayers.size());

        for (Player player : onlinePlayers) {
            playerEntries.add(formatPlayerEntry(player, showUuids));
        }

        for (int i = 0; i < playerEntries.size(); i += playersPerLine) {
            int end = Math.min(i + playersPerLine, playerEntries.size());
            sender.sendMessage(styledLine(String.join("  •  ", playerEntries.subList(i, end))));
        }

        sender.sendMessage(styledDivider());
        sender.sendMessage(styledLine("Tip: Use /list uuids for short UUIDs."));
        sender.sendMessage(styledLine("Currently online: " + onlineCount + " / " + maxPlayers));

        return true;
    }

    private Component styledLine(String text) {
        return miniMessage.deserialize("<gray>  </gray><gradient:#48dbfb:#1dd1a1>" + text + "</gradient>");
    }

    private Component styledDivider() {
        return miniMessage.deserialize("<gray>  </gray><gradient:#ff6b6b:#feca57:#48dbfb:#1dd1a1:#54a0ff:#5f27cd>" + BORDER + "</gradient>");
    }

    private String formatPlayerEntry(Player player, boolean showUuids) {
        if (!showUuids) {
            return "➜ " + player.getName();
        }

        String uuid = player.getUniqueId().toString();
        String shortUuid = uuid.substring(0, 8) + "..." + uuid.substring(uuid.length() - 4);
        return "➜ " + player.getName() + " (" + shortUuid + ")";
    }
}
