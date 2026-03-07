package dev.luca.mcbasics.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class FormattedMessage {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static String prefix = "";

    public static void setPrefix(String newPrefix) {
        prefix = newPrefix;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static Component create(String key, String fallback) {
        Component prefixComponent = miniMessage.deserialize(prefix);
        Component messageComponent = Message.getComponent(key, fallback);
        return Component.text()
                .append(prefixComponent)
                .append(messageComponent)
                .build();
    }

    public static Component create(String key, String fallback, String... placeholders) {
        Component prefixComponent = miniMessage.deserialize(prefix);
        Component messageComponent = Message.getComponent(key, fallback, placeholders);
        return Component.text()
                .append(prefixComponent)
                .append(messageComponent)
                .build();
    }

    public static Component create(Component message) {
        Component prefixComponent = miniMessage.deserialize(prefix);
        return Component.text()
                .append(prefixComponent)
                .append(message)
                .build();
    }
}
