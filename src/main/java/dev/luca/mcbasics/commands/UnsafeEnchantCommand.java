package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UnsafeEnchantCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.unsafeenchant")) {
            sender.sendMessage(Message.get("general.no_permission", ""));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Message.get("unsafeenchant.usage", ""));
            return true;
        }

        String enchantmentName = args[0].toUpperCase();
        int level;

        try {
            level = Integer.parseInt(args[1]);
            if (level < 0) {
                sender.sendMessage(Message.get("unsafeenchant.level_negative", ""));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Message.get("unsafeenchant.invalid_level", ""));
            return true;
        }

        Enchantment enchantment = getEnchantmentByName(enchantmentName);
        if (enchantment == null) {
            sender.sendMessage(Message.get("unsafeenchant.invalid_enchantment", ""));
            return true;
        }

        Player target;
        if (args.length > 2) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Message.get("general.player_not_found", ""));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.get("general.specify_player", ""));
            return true;
        }

        ItemStack item = target.getInventory().getItemInMainHand();
        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            sender.sendMessage(Message.get("unsafeenchant.no_item", ""));
            return true;
        }

        try {
            Field maxLevelField = Enchantment.class.getDeclaredField("maxLevel");
            maxLevelField.setAccessible(true);
            int originalMax = maxLevelField.getInt(enchantment);
            maxLevelField.setInt(enchantment, Short.MAX_VALUE);
            item.addEnchantment(enchantment, level);
            maxLevelField.setInt(enchantment, originalMax);
        } catch (Exception e) {
            item.addEnchantment(enchantment, level);
        }

        sender.sendMessage(Message.get("unsafeenchant.enchanted", "", "enchant", enchantmentName, "level", String.valueOf(level)));
        if (target != sender) {
            target.sendMessage(Message.get("unsafeenchant.enchanted_other", ""));
        }

        return true;
    }

    private Enchantment getEnchantmentByName(String name) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getKey().getKey().equalsIgnoreCase(name) ||
                enchantment.getKey().getNamespace().equalsIgnoreCase(name)) {
                return enchantment;
            }
            String displayName = enchantment.getKey().getKey().replace("_", "");
            if (displayName.equalsIgnoreCase(name.replace("_", ""))) {
                return enchantment;
            }
        }
        return null;
    }

    public static List<String> getEnchantmentNames() {
        List<String> names = new ArrayList<>();
        for (Enchantment enchantment : Enchantment.values()) {
            names.add(enchantment.getKey().getKey());
        }
        return names;
    }
}
