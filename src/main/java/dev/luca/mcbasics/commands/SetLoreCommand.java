package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SetLoreCommand implements CommandExecutor {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.ITEM)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Message.getComponent("setlore.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /setlore <linenumber> <text></gradient>"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            sender.sendMessage(Message.getComponent("setlore.no_item", "<gradient:#ff6b6b:#ee5a24>✖ You must hold an item!</gradient>"));
            return true;
        }

        int lineNumber;
        try {
            lineNumber = Integer.parseInt(args[0]);
            if (lineNumber < 1) {
                sender.sendMessage(Message.getComponent("setlore.invalid_line", "<gradient:#ff6b6b:#ee5a24>✖ Line number must be 1 or higher!</gradient>"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(Message.getComponent("setlore.invalid_line", "<gradient:#ff6b6b:#ee5a24>✖ Invalid line number!</gradient>"));
            return true;
        }

        StringBuilder loreBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            loreBuilder.append(args[i]).append(" ");
        }
        String loreText = loreBuilder.toString().trim();
        String formattedLore = convertColors(loreText);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            sender.sendMessage(Message.getComponent("setlore.failed", "<gradient:#ff6b6b:#ee5a24>✖ Failed to set lore!</gradient>"));
            return true;
        }

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        int index = lineNumber - 1;
        while (lore.size() <= index) {
            lore.add("");
        }
        lore.set(index, formattedLore);

        List<net.kyori.adventure.text.Component> adventureLore = new ArrayList<>();
        for (String line : lore) {
            adventureLore.add(miniMessage.deserialize(line));
        }
        meta.lore(adventureLore);

        item.setItemMeta(meta);

        sender.sendMessage(Message.getComponent("setlore.success", "<gradient:#48dbfb:#1dd1a1>✦ Lore line " + lineNumber + " set!</gradient>"));

        return true;
    }

    private String convertColors(String text) {
        return text.replace("&", "§");
    }
}
