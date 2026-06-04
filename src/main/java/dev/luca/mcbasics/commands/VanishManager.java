package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.MCBasics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private static final Set<UUID> vanishedPlayers = new HashSet<>();
    private static File dataFile;
    private static YamlConfiguration dataConfig;

    public static void init(MCBasics plugin) {
        dataFile = new File(plugin.getDataFolder(), "vanish.yml");
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create vanish.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        load();
    }

    public static boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    public static boolean isVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    public static void hidePlayer(Player player) {
        vanishedPlayers.add(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.hidePlayer(player);
        }
        player.setCanPickupItems(false);
        save();
    }

    public static void showPlayer(Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.showPlayer(player);
        }
        player.setCanPickupItems(true);
        save();
    }

    public static void applyVanishOnJoin(Player player) {
        if (vanishedPlayers.contains(player.getUniqueId())) {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                    onlinePlayer.hidePlayer(player);
                }
            }
            player.setCanPickupItems(false);
        }
    }

    public static void hideVanishedForJoiner(Player joiner) {
        for (UUID uuid : vanishedPlayers) {
            if (!uuid.equals(joiner.getUniqueId())) {
                Player vanished = Bukkit.getPlayer(uuid);
                if (vanished != null && vanished.isOnline()) {
                    joiner.hidePlayer(vanished);
                }
            }
        }
    }

    private static void load() {
        vanishedPlayers.clear();
        var list = dataConfig.getStringList("vanished");
        for (String uuidStr : list) {
            try {
                vanishedPlayers.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static void save() {
        var list = new ArrayList<String>();
        for (UUID uuid : vanishedPlayers) {
            list.add(uuid.toString());
        }
        dataConfig.set("vanished", list);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            MCBasics.getInstance().getLogger().severe("Could not save vanish.yml: " + e.getMessage());
        }
    }
}
