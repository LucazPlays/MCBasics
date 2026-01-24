package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private static final CommandInfo[] COMMANDS = {
            new CommandInfo("help", "Show help menu", Permission.ALL),
            new CommandInfo("feed [player]", "Restore hunger", Permission.FEED),
            new CommandInfo("heal [player]", "Restore health", Permission.HEAL),
            new CommandInfo("fly [player]", "Toggle flight", Permission.FLY),
            new CommandInfo("speed <1-10> [player]", "Set walk speed", Permission.SPEED),
            new CommandInfo("gm 0/1/2/3 [p]", "Change gamemode", Permission.GM),
            new CommandInfo("gmc [p]", "Creative mode", Permission.GMC),
            new CommandInfo("gms [p]", "Survival mode", Permission.GMS),
            new CommandInfo("gma [p]", "Adventure mode", Permission.GMA),
            new CommandInfo("gmsp [p]", "Spectator mode", Permission.GMSP),
            new CommandInfo("vanish [p]", "Toggle vanish", Permission.VANISH),
            new CommandInfo("invsee <p>", "View inventory", Permission.INVSEE),
            new CommandInfo("tphere <p>", "Teleport to you", Permission.TPHERE),
            new CommandInfo("unsafeenchant <ench> <lvl>", "Add enchant", Permission.UNSAFEENCHANT)
    };

    private static final int COMMANDS_PER_PAGE = 5;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 1;
        if (args.length > 0) {
            try {
                page = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                sendError(sender, "Invalid page number!");
                return true;
            }
        }

        int totalPages = (int) Math.ceil((double) COMMANDS.length / COMMANDS_PER_PAGE);
        page = Math.min(page, totalPages);

        sender.sendMessage(createHelpMenu(page, totalPages));
        return true;
    }

    private Component createHelpMenu(int page, int totalPages) {
        int startIndex = (page - 1) * COMMANDS_PER_PAGE;
        int endIndex = Math.min(startIndex + COMMANDS_PER_PAGE, COMMANDS.length);

        StringBuilder sb = new StringBuilder();

        sb.append("<gradient:#ff6b6b:#feca57:#48dbfb:#ff9ff3:#54a0ff:#5f27cd>✦ MCBasics Help ✦</gradient>\n");
        sb.append("<gray>────────────────────────────</gray>\n");

        for (int i = startIndex; i < endIndex; i++) {
            CommandInfo cmd = COMMANDS[i];
            boolean hasPerm = senderHasPermission(cmd.permission);

            String icon = hasPerm ? "<green>➜</green>" : "<dark_gray>⊘</dark_gray>";
            String cmdColor = hasPerm ? "<yellow>" : "<dark_gray>";
            String descColor = hasPerm ? "<gray>" : "<dark_gray>";

            sb.append(icon).append(" <aqua>/").append(cmd.command).append("</aqua> ")
              .append(cmdColor).append(cmd.description).append("</")
              .append(hasPerm ? "yellow" : "dark_gray").append(">\n");
            sb.append("   <dark_gray>└</dark_gray> <red>").append(cmd.permission).append("</red>\n");
        }

        sb.append("<gray>────────────────────────────</gray>\n");

        String prevPage = String.valueOf(Math.max(1, page - 1));
        String nextPage = String.valueOf(Math.min(totalPages, page + 1));
        String currPage = String.valueOf(page);
        String totPage = String.valueOf(totalPages);

        String prevColor = page > 1 ? "<green>◀</green>" : "<dark_gray>◀</dark_gray>";
        String nextColor = page < totalPages ? "<green>▶</green>" : "<dark_gray>▶</dark_gray>";

        sb.append(prevColor).append(" <aqua>Page ").append(currPage).append("/")
          .append(totPage).append("</aqua> ").append(nextColor).append("\n");

        sb.append("<dark_gray>└</dark_gray> <gold>/help <1-").append(totPage)
          .append("></gold> <gray>or click arrows</gray>\n");

        sb.append("<gradient:#ff6b6b:#5f27cd>v1.0.0</gradient> <gray>•</gray> <aqua>LucazPlays</aqua>");

        return miniMessage.deserialize(sb.toString());
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(miniMessage.deserialize("<red>✖ " + message + "</red>"));
    }

    private boolean senderHasPermission(String permission) {
        return senderHasPermission(permission);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            int totalPages = (int) Math.ceil((double) COMMANDS.length / COMMANDS_PER_PAGE);
            for (int i = 1; i <= totalPages; i++) {
                completions.add(String.valueOf(i));
            }
        }
        return completions;
    }

    private static class CommandInfo {
        final String command;
        final String description;
        final String permission;

        CommandInfo(String command, String description, String permission) {
            this.command = command;
            this.description = description;
            this.permission = permission;
        }
    }
}
