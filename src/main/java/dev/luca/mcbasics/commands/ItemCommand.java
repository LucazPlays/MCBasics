package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.ITEM)) {
            sender.sendMessage(Message.get("general.no_permission", "&cYou don't have permission!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.mustBePlayer());
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.get("item.usage", "&cUsage: /i <item> [amount]"));
            return true;
        }

        String itemName = args[0].toUpperCase();
        int amount = 1;

        if (args.length > 1) {
            try {
                amount = Math.max(1, Math.min(64, Integer.parseInt(args[1])));
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.get("item.invalid_amount", "&cInvalid amount!"));
                return true;
            }
        }

        Material material = getMaterialByName(itemName);
        if (material == null) {
            sender.sendMessage(Message.get("item.invalid_item", "&cInvalid item!"));
            return true;
        }

        if (material == Material.AIR) {
            sender.sendMessage(Message.get("item.invalid_item", "&cInvalid item!"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = new ItemStack(material, amount);
        player.getInventory().addItem(item);

        String displayName = formatItemName(material.name());
        sender.sendMessage(Message.get("item.given", "Given %amount%x %item% to your inventory!", "amount", String.valueOf(amount), "item", displayName));

        return true;
    }

    private Material getMaterialByName(String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            for (Material material : Material.values()) {
                if (material.name().equalsIgnoreCase(name)) {
                    return material;
                }
                if (material.name().replace("_", "").equalsIgnoreCase(name.replace("_", ""))) {
                    return material;
                }
            }
        }
        return null;
    }

    private String formatItemName(String name) {
        String[] parts = name.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    sb.append(part.substring(1).toLowerCase());
                }
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String partial = args[0].toUpperCase();
            for (Material material : Material.values()) {
                if (material != Material.AIR && material.name().startsWith(partial)) {
                    completions.add(material.name().toLowerCase());
                }
            }
        } else if (args.length == 2) {
            completions.add("1");
            completions.add("16");
            completions.add("32");
            completions.add("64");
        }
        return completions;
    }
}
