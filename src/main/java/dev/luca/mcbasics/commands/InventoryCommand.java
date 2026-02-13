package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.MCBasics;
import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryCommand implements CommandExecutor, Listener {

    private static final Map<UUID, Player> viewingPlayers = new HashMap<>();

    public InventoryCommand() {
        MCBasics.getInstance().getServer().getPluginManager().registerEvents(this, MCBasics.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.INVSEE)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.getComponent("inventory.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /inventory <player></gradient>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        Player viewer = (Player) sender;

        if (target.equals(viewer)) {
            sender.sendMessage(Message.getComponent("inventory.cant_edit_self", "<gradient:#ff6b6b:#ee5a24>✖ You can't edit your own inventory!</gradient>"));
            return true;
        }

        openInventory(viewer, target);

        return true;
    }

    public void openInventory(Player viewer, Player target) {
        if (viewingPlayers.containsKey(viewer.getUniqueId())) {
             viewingPlayers.remove(viewer.getUniqueId());
        }

        viewingPlayers.put(viewer.getUniqueId(), target);
        viewer.openInventory(target.getInventory());
        viewer.sendMessage(Message.getComponent("inventory.editing", "<gradient:#48dbfb:#1dd1a1>✦ Editing %target%'s inventory!</gradient>", "target", target.getName()));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player viewer = (Player) event.getPlayer();
        viewingPlayers.remove(viewer.getUniqueId());
    }
}
