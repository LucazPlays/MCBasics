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

import java.util.List;

public class SetDisplayNameCommand implements CommandExecutor {

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

        if (args.length == 0) {
            sender.sendMessage(Message.getComponent("setdisplayname.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /setdisplayname <name></gradient>"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == org.bukkit.Material.AIR) {
            sender.sendMessage(Message.getComponent("setdisplayname.no_item", "<gradient:#ff6b6b:#ee5a24>✖ You must hold an item!</gradient>"));
            return true;
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (String arg : args) {
            nameBuilder.append(arg).append(" ");
        }
        String name = nameBuilder.toString().trim();

        String formattedName = convertColors(name);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(miniMessage.deserialize(formattedName));
            item.setItemMeta(meta);
        }

        sender.sendMessage(Message.getComponent("setdisplayname.success", "<gradient:#48dbfb:#1dd1a1>✦ Item display name set!</gradient>"));

        return true;
    }

    private String convertColors(String text) {
        return text.replace("&", "§");
    }
}
