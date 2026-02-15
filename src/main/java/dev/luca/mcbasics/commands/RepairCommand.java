package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.REPAIR)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            sender.sendMessage(Message.getComponent("repair.no_item", "<gradient:#ff6b6b:#ee5a24>✖ You must hold an item!</gradient>"));
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            sender.sendMessage(Message.getComponent("repair.cannot_repair", "<gradient:#ff6b6b:#ee5a24>✖ This item cannot be repaired!</gradient>"));
            return true;
        }

        Damageable damageable = (Damageable) meta;
        if (damageable.getDamage() == 0) {
            sender.sendMessage(Message.getComponent("repair.not_damaged", "<gradient:#ff6b6b:#ee5a24>✖ This item is not damaged!</gradient>"));
            return true;
        }

        damageable.setDamage(0);
        item.setItemMeta((ItemMeta) damageable);

        sender.sendMessage(Message.getComponent("repair.success", "<gradient:#48dbfb:#1dd1a1>✦ Item repaired!</gradient>"));

        return true;
    }
}
