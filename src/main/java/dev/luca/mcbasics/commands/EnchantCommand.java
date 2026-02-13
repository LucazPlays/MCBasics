package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EnchantCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.ENCHANT)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Message.getComponent("enchant.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /enchant <enchantment> <level></gradient>"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            sender.sendMessage(Message.getComponent("enchant.no_item", "<gradient:#ff6b6b:#ee5a24>✖ You must hold an item!</gradient>"));
            return true;
        }

        String enchantmentName = args[0].toUpperCase();
        int level;

        try {
            level = Integer.parseInt(args[1]);
            if (level < 1) {
                sender.sendMessage(Message.getComponent("enchant.invalid_level", "<gradient:#ff6b6b:#ee5a24>✖ Level must be 1 or higher!</gradient>"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Message.getComponent("enchant.invalid_level", "<gradient:#ff6b6b:#ee5a24>✖ Invalid level!</gradient>"));
            return true;
        }

        Enchantment enchantment = getEnchantmentByName(enchantmentName);
        if (enchantment == null) {
            sender.sendMessage(Message.getComponent("enchant.invalid_enchantment", "<gradient:#ff6b6b:#ee5a24>✖ Invalid enchantment!</gradient>"));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            sender.sendMessage(Message.getComponent("enchant.failed", "<gradient:#ff6b6b:#ee5a24>✖ Failed to add enchantment!</gradient>"));
            return true;
        }

        if (!enchantment.canEnchantItem(item)) {
            sender.sendMessage(Message.getComponent("enchant.cant_enchant", "<gradient:#ff6b6b:#ee5a24>✖ This enchantment cannot be applied to this item!</gradient>"));
            return true;
        }

        int maxLevel = enchantment.getMaxLevel();
        if (level > maxLevel) {
            sender.sendMessage(Message.getComponent("enchant.level_too_high", "<gradient:#ff6b6b:#ee5a24>✖ Max level for this enchantment is %max%!</gradient>", "max", String.valueOf(maxLevel)));
            return true;
        }

        boolean success = addEnchantment(item, meta, enchantment, level);

        if (!success) {
            sender.sendMessage(Message.getComponent("enchant.failed", "<gradient:#ff6b6b:#ee5a24>✖ Failed to add enchantment!</gradient>"));
            return true;
        }

        sender.sendMessage(Message.getComponent("enchant.success", "<gradient:#48dbfb:#1dd1a1>✦ Added %enchant% %level% to the item!</gradient>", "enchant", enchantmentName, "level", String.valueOf(level)));

        return true;
    }

    private boolean addEnchantment(ItemStack item, ItemMeta meta, Enchantment enchantment, int level) {
        try {
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
            return true;
        } catch (Exception e) {
            return false;
        }
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (Enchantment enchantment : Enchantment.values()) {
                String name = enchantment.getKey().getKey().toLowerCase();
                if (name.startsWith(partial)) {
                    completions.add(enchantment.getKey().getKey().toLowerCase());
                }
            }
        } else if (args.length == 2) {
            String partial = args[1];
            try {
                int currentLevel = Integer.parseInt(partial);
                int maxLevel = 5;
                if (args.length > 0) {
                    Enchantment enchant = getEnchantmentByName(args[0].toUpperCase());
                    if (enchant != null) {
                        maxLevel = enchant.getMaxLevel();
                    }
                }
                for (int i = 1; i <= maxLevel; i++) {
                    if (partial.isEmpty() || String.valueOf(i).startsWith(partial)) {
                        completions.add(String.valueOf(i));
                    }
                }
            } catch (NumberFormatException e) {
                for (int i = 1; i <= 5; i++) {
                    completions.add(String.valueOf(i));
                }
            }
        }

        return completions;
    }
}
