package dev.luca.mcbasics.api;

import dev.luca.minecraftapi.MessageAPI;
import org.bukkit.plugin.Plugin;

public class Message {

    private static Plugin plugin;

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
        return MessageAPI.getMessage("general.prefix", "&8[&6MCBasics&8] ");
    }
}
