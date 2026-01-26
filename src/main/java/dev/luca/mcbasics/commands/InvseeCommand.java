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
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.getComponent("invsee.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /invsee <player></gradient>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
            return true;
        }

        if (target.equals(sender)) {
            sender.sendMessage(Message.getComponent("invsee.cant_view_self", "<gradient:#ff6b6b:#ee5a24>✖ You can't invsee yourself!</gradient>"));
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
            player.sendMessage(Message.getComponent("invsee.viewing", "<gradient:#48dbfb:#1dd1a1>✦ Viewing %target%'s inventory!</gradient>", "target", target.getName()));
        }

        return true;
    }
}
