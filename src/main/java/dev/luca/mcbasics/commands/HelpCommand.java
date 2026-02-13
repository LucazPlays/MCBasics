package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private static final CommandInfo[] COMMANDS = {
            new CommandInfo("help [page]", "Show help menu", Permission.HELP),
            new CommandInfo("i <item> [amount]", "Give yourself items", Permission.ITEM),
            new CommandInfo("feed [player]", "Restore hunger", Permission.FEED),
            new CommandInfo("heal [player]", "Restore health", Permission.HEAL),
            new CommandInfo("fly [player]", "Toggle flight", Permission.FLY),
            new CommandInfo("speed <1-10> [p]", "Set walk speed", Permission.SPEED),
            new CommandInfo("gamemode <0-3> [p]", "Change gamemode", Permission.GM),
            new CommandInfo("vanish [p]", "Toggle vanish", Permission.VANISH),
            new CommandInfo("invsee <p>", "Edit inventory live", Permission.INVSEE),
            new CommandInfo("tphere <p]", "Teleport to you", Permission.TPHERE),
            new CommandInfo("unsafeenchant <ench> <lvl>", "Add unsafe enchant", Permission.UNSAFEENCHANT),
            new CommandInfo("enchant <ench> <lvl>", "Add safe enchant", Permission.ENCHANT),
            new CommandInfo("enderchest [p]", "Open enderchest", Permission.ENDERCHEST),
            new CommandInfo("setdisplayname <name>", "Set item name", Permission.SETDISPLAYNAME),
            new CommandInfo("setlore <line> <text>", "Set lore line", Permission.SETLORE),
            new CommandInfo("sudo <p> <cmd>", "Force cmd exec", Permission.SUDO),
            new CommandInfo("ping [p]", "Show ping", Permission.PING),
            new CommandInfo("god [p]", "Toggle god mode", Permission.GOD),
            new CommandInfo("craft", "Open crafting", Permission.CRAFT),
            new CommandInfo("anvil", "Open anvil", Permission.ANVIL)
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

        sender.sendMessage(createHelpMenu(sender, page, totalPages));
        return true;
    }

    private Component createHelpMenu(CommandSender sender, int page, int totalPages) {
        int startIndex = (page - 1) * COMMANDS_PER_PAGE;
        int endIndex = Math.min(startIndex + COMMANDS_PER_PAGE, COMMANDS.length);

        Component header = miniMessage.deserialize("<gradient:#ff6b6b:#feca57:#48dbfb:#ff9ff3:#54a0ff:#5f27cd>✦ MCBasics Help ✦</gradient>\n");
        Component divider = miniMessage.deserialize("<gray>────────────────────────────</gray>\n");

        List<Component> parts = new ArrayList<>();
        parts.add(header);
        parts.add(divider);

        for (int i = startIndex; i < endIndex; i++) {
            CommandInfo cmd = COMMANDS[i];
            boolean hasPerm = senderHasPermission(sender, cmd.permission);

            Component hoverText = Component.text()
                    .append(Component.text("Permission: ", NamedTextColor.GOLD))
                    .append(Component.text(cmd.permission, NamedTextColor.RED))
                    .build();

            Component cmdLine = Component.text()
                    .append(Component.text(hasPerm ? "➜" : "⊘", hasPerm ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY))
                    .append(Component.text(" /", NamedTextColor.AQUA))
                    .append(Component.text(cmd.command, hasPerm ? NamedTextColor.YELLOW : NamedTextColor.DARK_GRAY))
                    .append(Component.text(" ", NamedTextColor.WHITE))
                    .append(Component.text(cmd.description, hasPerm ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))
                    .hoverEvent(hoverText)
                    .clickEvent(ClickEvent.suggestCommand("/" + cmd.command))
                    .append(Component.text("\n", NamedTextColor.WHITE))
                    .build();

            Component permLine = Component.text()
                    .append(Component.text("   └ ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(cmd.permission, NamedTextColor.RED))
                    .append(Component.text("\n", NamedTextColor.WHITE))
                    .build();

            parts.add(cmdLine);
            parts.add(permLine);
        }

        parts.add(divider);

        int prevPage = Math.max(1, page - 1);
        int nextPage = Math.min(totalPages, page + 1);

        Component prevArrow = Component.text("◀")
                .color(page > 1 ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY)
                .hoverEvent(Component.text("Go to page " + prevPage, NamedTextColor.AQUA))
                .clickEvent(ClickEvent.runCommand("/help " + prevPage));

        Component nextArrow = Component.text("▶")
                .color(page < totalPages ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY)
                .hoverEvent(Component.text("Go to page " + nextPage, NamedTextColor.AQUA))
                .clickEvent(ClickEvent.runCommand("/help " + nextPage));

        Component navLine = Component.text()
                .append(prevArrow)
                .append(Component.text(" Page ", NamedTextColor.AQUA))
                .append(Component.text(String.valueOf(page), NamedTextColor.YELLOW))
                .append(Component.text("/", NamedTextColor.WHITE))
                .append(Component.text(String.valueOf(totalPages), NamedTextColor.YELLOW))
                .append(Component.text(" ", NamedTextColor.WHITE))
                .append(nextArrow)
                .append(Component.text("\n", NamedTextColor.WHITE))
                .build();

        parts.add(navLine);

        Component helpLink = miniMessage.deserialize("<gold>/help <1-" + totalPages + "></gold>");
        Component navHint = Component.text()
                .append(Component.text("└ ", NamedTextColor.DARK_GRAY))
                .append(helpLink)
                .append(Component.text(" or ", NamedTextColor.GRAY))
                .append(Component.text("click arrows", NamedTextColor.YELLOW)
                        .hoverEvent(Component.text("Click arrows to navigate", NamedTextColor.AQUA)))
                .append(Component.text("\n", NamedTextColor.WHITE))
                .build();

        parts.add(navHint);

        Component footer = Component.text()
                .append(miniMessage.deserialize("<gradient:#ff6b6b:#5f27cd>v1.0.0</gradient>"))
                .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                .append(Component.text("LucazPlays", NamedTextColor.AQUA))
                .build();

        parts.add(footer);

        return Component.join(Component.text("", NamedTextColor.WHITE), parts);
    }

    private void sendError(CommandSender sender, String message) {
        sender.sendMessage(miniMessage.deserialize("<red>✖ " + message + "</red>"));
    }

    private boolean senderHasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
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
