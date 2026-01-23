package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvseeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.INVSEE)) {
            sender.sendMessage(Message.get("general.no_permission", "&cYou don't have permission!"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.get("invsee.usage", "&cUsage: /invsee <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.get("general.player_not_found", "&cPlayer not found!"));
            return true;
        }

        if (target.equals(sender)) {
            sender.sendMessage(Message.get("invsee.cant_view_self", "&cYou can't invsee yourself!"));
            return true;
        }

        Inventory targetInventory = target.getInventory();
        Inventory viewerInventory = Bukkit.createInventory(null, 45, "§8" + target.getName() + "'s Inventory");

        viewerInventory.setContents(targetInventory.getContents());

        if (target.getInventory().getExtraContents() != null) {
            for (int i = 0; i < target.getInventory().getExtraContents().length; i++) {
                viewerInventory.setItem(36 + i, target.getInventory().getExtraContents()[i]);
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.openInventory(viewerInventory);
            player.sendMessage(Message.get("invsee.viewing", "Viewing %target%'s inventory!", "target", target.getName()));
        }

        return true;
    }
}
