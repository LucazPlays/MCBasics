package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetItemDamageCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.ITEMDAMAGE)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(FormattedMessage.create("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(FormattedMessage.create("setitemdamage.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /setitemdamage <damage></gradient>"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(FormattedMessage.create("setitemdamage.no_item", "<gradient:#ff6b6b:#ee5a24>✖ You must hold an item!</gradient>"));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            sender.sendMessage(FormattedMessage.create("setitemdamage.not_damageable", "<gradient:#ff6b6b:#ee5a24>✖ This item cannot take durability damage!</gradient>"));
            return true;
        }

        int damage;
        try {
            damage = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(FormattedMessage.create("setitemdamage.invalid", "<gradient:#ff6b6b:#ee5a24>✖ Damage must be a number!</gradient>"));
            return true;
        }

        int maxDurability = item.getType().getMaxDurability();
        if (maxDurability <= 0) {
            sender.sendMessage(FormattedMessage.create("setitemdamage.not_damageable", "<gradient:#ff6b6b:#ee5a24>✖ This item cannot take durability damage!</gradient>"));
            return true;
        }

        if (damage < 0 || damage >= maxDurability) {
        sender.sendMessage(FormattedMessage.create(
                "setitemdamage.range",
                "<gradient:#ff6b6b:#ee5a24>✖ Damage must be between 0 and %max%!</gradient>",
                "max", String.valueOf(maxDurability - 1)
        ));
            return true;
        }

        Damageable damageable = (Damageable) meta;
        damageable.setDamage(damage);
        item.setItemMeta((ItemMeta) damageable);

        sender.sendMessage(FormattedMessage.create(
                "setitemdamage.success",
                "<gradient:#48dbfb:#1dd1a1>✦ Item damage set to %damage%/%max%!</gradient>",
                "damage", String.valueOf(damage),
                "max", String.valueOf(maxDurability - 1)
        ));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length != 1 || !(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            return completions;
        }

        int maxDurability = item.getType().getMaxDurability();
        if (maxDurability <= 0) {
            return completions;
        }

        String partial = args[0];
        List<String> suggestions = new ArrayList<>();
        suggestions.add("0");
        suggestions.add("1");
        suggestions.add(String.valueOf(Math.max(1, maxDurability / 2)));
        suggestions.add(String.valueOf(maxDurability - 1));

        for (String suggestion : suggestions) {
            if (suggestion.startsWith(partial)) {
                completions.add(suggestion);
            }
        }
        return completions;
    }
}
