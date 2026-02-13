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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
        Inventory mainInv = createMainInventory(target);
        viewingPlayers.put(viewer.getUniqueId(), target);
        viewer.openInventory(mainInv);
        viewer.sendMessage(Message.getComponent("inventory.editing", "<gradient:#48dbfb:#1dd1a1>✦ Editing %target%'s inventory!</gradient>", "target", target.getName()));
    }

    private Inventory createMainInventory(Player target) {
        Inventory inv = Bukkit.createInventory(null, 45, "§8" + target.getName() + "'s Inventory");

        ItemStack[] contents = target.getInventory().getContents();
        for (int i = 0; i < contents.length && i < 36; i++) {
            inv.setItem(i, contents[i]);
        }

        inv.setItem(36, target.getInventory().getHelmet());
        inv.setItem(37, target.getInventory().getChestplate());
        inv.setItem(38, target.getInventory().getLeggings());
        inv.setItem(39, target.getInventory().getBoots());
        inv.setItem(40, target.getInventory().getItemInOffHand());

        return inv;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player viewer = (Player) event.getWhoClicked();
        Player target = viewingPlayers.get(viewer.getUniqueId());
        
        if (target == null) return;

        event.setCancelled(false);

        syncToPlayer(viewer, target);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player viewer = (Player) event.getWhoClicked();
        Player target = viewingPlayers.get(viewer.getUniqueId());
        
        if (target == null) return;

        event.setCancelled(false);

        Bukkit.getScheduler().runTaskLater(MCBasics.getInstance(), () -> {
            syncToPlayer(viewer, target);
        }, 1L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player viewer = (Player) event.getPlayer();
        Player target = viewingPlayers.remove(viewer.getUniqueId());
        
        if (target == null) return;

        syncToPlayer(viewer, target);
    }

    private void syncToPlayer(Player viewer, Player target) {
        Inventory viewInv = viewer.getOpenInventory().getTopInventory();
        
        ItemStack[] contents = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            contents[i] = viewInv.getItem(i);
        }
        target.getInventory().setContents(contents);

        target.getInventory().setHelmet(viewInv.getItem(36));
        target.getInventory().setChestplate(viewInv.getItem(37));
        target.getInventory().setLeggings(viewInv.getItem(38));
        target.getInventory().setBoots(viewInv.getItem(39));
        target.getInventory().setItemInOffHand(viewInv.getItem(40));

        ItemStack cursor = viewer.getItemOnCursor();
        if (cursor != null && cursor.getType() != org.bukkit.Material.AIR) {
            target.getInventory().addItem(cursor);
            viewer.setItemOnCursor(null);
        }

        target.updateInventory();
    }
}
