package com.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class InvseeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.invsee")) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /invsee <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        if (target.equals(sender)) {
            sender.sendMessage("§cYou can't invsee yourself!");
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
            player.sendMessage("§aViewing " + target.getName() + "'s inventory!");
        }

        return true;
    }
}
