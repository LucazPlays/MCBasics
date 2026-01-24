package dev.luca.mcbasics.api;

import dev.luca.minecraftapi.MessageAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.Plugin;

public class Message {

    private static Plugin plugin;
    private static final MiniMessage miniMessage = MiniMessage.builder()
            .tags(TagResolver.builder().build())
            .build();

    private static final TextColor[] GRADIENT_COLORS = {
            TextColor.fromHexString("#ff6b6b"),
            TextColor.fromHexString("#feca57"),
            TextColor.fromHexString("#48dbfb"),
            TextColor.fromHexString("#ff9ff3"),
            TextColor.fromHexString("#54a0ff"),
            TextColor.fromHexString("#5f27cd")
    };

    public static void init(Plugin mainPlugin) {
        plugin = mainPlugin;
        MessageAPI.init(plugin);
    }

    public static String get(String key, String fallback) {
        return MessageAPI.getMessage(key, fallback);
    }

    public static String get(String key, String fallback, String... placeholders) {
        return MessageAPI.getMessage(key, fallback, placeholders);
    }

    public static void reload() {
        MessageAPI.reloadMessages();
    }

    public static String getPrefix() {
        return MessageAPI.getMessage("general.prefix", "<gradient:#ff6b6b:#feca57:MCBasics</gradient> ");
    }

    public static Component gradientText(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            TextColor color = GRADIENT_COLORS[i % GRADIENT_COLORS.length];
            sb.append("<").append(color.toString()).append(">").append(text.charAt(i));
        }
        return MiniMessage.miniMessage().deserialize(sb.toString());
    }

    public static Component header(String text) {
        String border = "━".repeat(Math.max(0, text.length() + 4));
        return Component.text("\n")
                .append(Component.text(border, NamedTextColor.GRAY))
                .append(Component.text("\n  ", NamedTextColor.GRAY))
                .append(gradientText(text))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text(border, NamedTextColor.GRAY))
                .append(Component.text("\n"));
    }

    public static Component commandInfo(String command, String description, String permission) {
        return Component.text()
                .append(Component.text("  ➜ ", NamedTextColor.GOLD))
                .append(Component.text("/", NamedTextColor.AQUA))
                .append(Component.text(command, NamedTextColor.YELLOW))
                .append(Component.text(" ", NamedTextColor.WHITE))
                .append(Component.text(description, NamedTextColor.GRAY))
                .append(Component.text("\n    ", NamedTextColor.DARK_GRAY))
                .append(Component.text("└ ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Permission: ", NamedTextColor.DARK_GRAY))
                .append(Component.text(permission, NamedTextColor.RED))
                .build();
    }

    public static Component pageIndicator(int currentPage, int totalPages) {
        return Component.text()
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n", NamedTextColor.DARK_GRAY))
                .append(Component.text("    ", NamedTextColor.GRAY))
                .append(Component.text("◀", currentPage > 1 ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("Page ", NamedTextColor.AQUA))
                .append(Component.text(currentPage, NamedTextColor.YELLOW))
                .append(Component.text(" / ", NamedTextColor.WHITE))
                .append(Component.text(totalPages, NamedTextColor.YELLOW))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("▶", currentPage < totalPages ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                .append(Component.text("\n    ", NamedTextColor.DARK_GRAY))
                .append(Component.text("└ ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Use ", NamedTextColor.GRAY))
                .append(Component.text("/help <page>", NamedTextColor.GOLD))
                .append(Component.text(" to navigate", NamedTextColor.GRAY))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY))
                .build();
    }

    public static Component footer() {
        return Component.text()
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text("    ", NamedTextColor.GRAY))
                .append(gradientText("MCBasics"))
                .append(Component.text(" v1.0.0", NamedTextColor.GRAY))
                .append(Component.text(" • ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Made with ❤", NamedTextColor.RED))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .append(Component.text("  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━", NamedTextColor.DARK_GRAY))
                .append(Component.text("\n", NamedTextColor.GRAY))
                .build();
    }

    public static Component noPermission() {
        return Component.text()
                .append(Component.text("        ✖", NamedTextColor.RED))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("No Permission", NamedTextColor.RED))
                .build();
    }

    public static Component success(String message) {
        return Component.text()
                .append(Component.text("        ✔", NamedTextColor.GREEN))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.GREEN))
                .build();
    }

    public static Component error(String message) {
        return Component.text()
                .append(Component.text("        ✖", NamedTextColor.RED))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.RED))
                .build();
    }

    public static Component info(String message) {
        return Component.text()
                .append(Component.text("        ℹ", NamedTextColor.AQUA))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text(message, NamedTextColor.AQUA))
                .build();
    }

    public static Component playerNotFound() {
        return Component.text()
                .append(Component.text("        ✖", NamedTextColor.RED))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("Player not found!", NamedTextColor.RED))
                .build();
    }

    public static Component mustBePlayer() {
        return Component.text()
                .append(Component.text("        ✖", NamedTextColor.RED))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("This command can only be used by players!", NamedTextColor.RED))
                .build();
    }

    public static Component specifyPlayer() {
        return Component.text()
                .append(Component.text("        ✖", NamedTextColor.RED))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("Please specify a player!", NamedTextColor.RED))
                .build();
    }

    public static Component cantTargetSelf() {
        return Component.text()
                .append(Component.text("        ✖", NamedTextColor.RED))
                .append(Component.text("  ", NamedTextColor.WHITE))
                .append(Component.text("You can't target yourself!", NamedTextColor.RED))
                .build();
    }
}
