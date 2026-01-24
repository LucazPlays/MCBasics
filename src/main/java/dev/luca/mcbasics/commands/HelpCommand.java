package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final TextColor[] GRADIENT_COLORS = {
            TextColor.fromHexString("#ff6b6b"),
            TextColor.fromHexString("#feca57"),
            TextColor.fromHexString("#48dbfb"),
            TextColor.fromHexString("#ff9ff3"),
            TextColor.fromHexString("#54a0ff"),
            TextColor.fromHexString("#5f27cd")
    };

    private static final CommandCategory[] CATEGORIES = {
            new CommandCategory("General", Arrays.asList(
                    new CommandInfo("help", "Show this help menu", Permission.ALL),
                    new CommandInfo("feed", "Restore your hunger", Permission.FEED),
                    new CommandInfo("heal", "Restore your health", Permission.HEAL),
                    new CommandInfo("fly", "Toggle flight mode", Permission.FLY),
                    new CommandInfo("speed [1-10]", "Set walk/fly speed", Permission.SPEED)
            )),
            new CommandCategory("Gamemode", Arrays.asList(
                    new CommandInfo("gm 0/1/2/3 [player]", "Change gamemode", Permission.GM),
                    new CommandInfo("gmc [player]", "Creative mode", Permission.GMC),
                    new CommandInfo("gms [player]", "Survival mode", Permission.GMS),
                    new CommandInfo("gma [player]", "Adventure mode", Permission.GMA),
                    new CommandInfo("gmsp [player]", "Spectator mode", Permission.GMSP)
            )),
            new CommandCategory("Moderation", Arrays.asList(
                    new CommandInfo("vanish [player]", "Toggle vanish mode", Permission.VANISH),
                    new CommandInfo("invsee <player>", "View player inventory", Permission.INVSEE),
                    new CommandInfo("tphere <player>", "Teleport player to you", Permission.TPHERE),
                    new CommandInfo("unsafeenchant <enchant> <level>", "Add custom enchant", Permission.UNSAFEENCHANT)
            ))
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 1;
        if (args.length > 0) {
            try {
                page = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.error("Invalid page number!"));
                return true;
            }
        }

        int totalPages = (int) Math.ceil((double) CATEGORIES.length / 1);
        page = Math.min(page, totalPages);

        sender.sendMessage(createHelpMenu(page, totalPages));
        return true;
    }

    private Component createHelpMenu(int page, int totalPages) {
        TextComponent.Builder builder = Component.text();

        // Header with gradient
        builder.append(decoratedHeader());
        builder.append(Component.text("\n"));

        // Show current category
        CommandCategory category = CATEGORIES[page - 1];
        builder.append(categoryHeader(category.name));
        builder.append(Component.text("\n"));

        // Commands
        for (CommandInfo cmd : category.commands) {
            boolean hasPermission = senderHasPermission(cmd.permission);
            builder.append(commandDisplay(cmd.command, cmd.description, cmd.permission, hasPermission));
            builder.append(Component.text("\n"));
        }

        // Page indicator
        builder.append(pageIndicator(page, totalPages));

        // Footer
        builder.append(footer());

        return builder.build();
    }

    private Component decoratedHeader() {
        String[] lines = {
                "╔════════════════════════════════════════════════════════════╗",
                "║                                                            ║",
                "║                    HELP  MENU                              ║",
                "║                                                            ║",
                "╚════════════════════════════════════════════════════════════╝"
        };

        TextComponent.Builder result = Component.text();
        for (int i = 0; i < lines.length; i++) {
            NamedTextColor color = switch (i) {
                case 0, 4 -> NamedTextColor.GOLD;
                case 2 -> NamedTextColor.AQUA;
                default -> NamedTextColor.GRAY;
            };
            result.append(Component.text(lines[i], color));
            if (i < lines.length - 1) {
                result.append(Component.text("\n"));
            }
        }
        return result.build();
    }

    private Component categoryHeader(String name) {
        String border = "━".repeat(name.length() + 6);
        return Component.text()
                .append(Component.text("    ", NamedTextColor.GRAY))
                .append(Component.text(border, NamedTextColor.DARK_PURPLE))
                .append(Component.text("\n    ", NamedTextColor.GRAY))
                .append(Component.text("│ ", NamedTextColor.DARK_PURPLE))
                .append(gradientText(name))
                .append(Component.text(" │", NamedTextColor.DARK_PURPLE))
                .append(Component.text("\n    ", NamedTextColor.GRAY))
                .append(Component.text(border, NamedTextColor.DARK_PURPLE))
                .build();
    }

    private Component commandDisplay(String command, String description, String permission, boolean hasPermission) {
        String icon = hasPermission ? "➜" : "⊘";

        Component hoverText = Component.text()
                .append(Component.text("Command: ", NamedTextColor.GOLD))
                .append(Component.text("/" + command, NamedTextColor.WHITE))
                .append(Component.text("\n\n", NamedTextColor.GRAY))
                .append(Component.text("Permission: ", NamedTextColor.GOLD))
                .append(Component.text(permission, NamedTextColor.RED))
                .append(Component.text("\n\n", NamedTextColor.GRAY))
                .append(Component.text("Click to copy command", NamedTextColor.AQUA))
                .build();

        return Component.text()
                .append(Component.text("    ", NamedTextColor.GRAY))
                .append(Component.text(icon, hasPermission ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("/", NamedTextColor.AQUA))
                .append(Component.text(command, hasPermission ? NamedTextColor.YELLOW : NamedTextColor.DARK_GRAY))
                .append(Component.text(" ", NamedTextColor.WHITE))
                .append(Component.text(description, hasPermission ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY))
                .hoverEvent(hoverText)
                .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/" + command))
                .build();
    }

    private Component pageIndicator(int currentPage, int totalPages) {
        return Component.text()
                .append(Component.text("\n"))
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n", NamedTextColor.DARK_GRAY))
                .append(Component.text("    ", NamedTextColor.GRAY))
                .append(Component.text("◀", currentPage > 1 ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("Category ", NamedTextColor.AQUA))
                .append(Component.text(currentPage, NamedTextColor.YELLOW))
                .append(Component.text(" / ", NamedTextColor.WHITE))
                .append(Component.text(totalPages, NamedTextColor.YELLOW))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("▶", currentPage < totalPages ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY))
                .append(Component.text("\n    ", NamedTextColor.DARK_GRAY))
                .append(Component.text("└ ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Use ", NamedTextColor.GRAY))
                .append(Component.text("/help <1-" + totalPages + ">", NamedTextColor.GOLD))
                .append(Component.text(" to navigate", NamedTextColor.GRAY))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY))
                .build();
    }

    private Component footer() {
        return Component.text()
                .append(Component.text("\n"))
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text("    ", NamedTextColor.GRAY))
                .append(gradientText("MCBasics"))
                .append(Component.text(" v1.0.0", NamedTextColor.GRAY))
                .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                .append(Component.text("✦ ", NamedTextColor.GOLD))
                .append(Component.text("Premium Essential Commands", NamedTextColor.AQUA))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .build();
    }

    private Component gradientText(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            TextColor color = GRADIENT_COLORS[i % GRADIENT_COLORS.length];
            sb.append("<").append(color.toString()).append(">").append(text.charAt(i));
        }
        return MiniMessage.miniMessage().deserialize(sb.toString());
    }

    private boolean senderHasPermission(String permission) {
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (int i = 1; i <= CATEGORIES.length; i++) {
                completions.add(String.valueOf(i));
            }
        }
        return completions;
    }

    private static class CommandCategory {
        public final String name;
        public final List<CommandInfo> commands;

        CommandCategory(String name, List<CommandInfo> commands) {
            this.name = name;
            this.commands = commands;
        }
    }

    private static class CommandInfo {
        public final String command;
        public final String description;
        public final String permission;

        CommandInfo(String command, String description, String permission) {
            this.command = command;
            this.description = description;
            this.permission = permission;
        }
    }
}
