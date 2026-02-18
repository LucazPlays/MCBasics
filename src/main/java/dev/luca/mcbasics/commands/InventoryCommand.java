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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryCommand implements CommandExecutor, Listener {

    private static final Map<UUID, Player> viewingPlayers = new HashMap<>();
    private static final Map<UUID, Inventory> viewInventories = new HashMap<>();
    private static final Map<UUID, BukkitTask> syncTasks = new HashMap<>();

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
        cleanup(viewer);

        Inventory inv = Bukkit.createInventory(null, 45, "§8" + target.getName() + "'s Inventory");
        
        syncInventoryToView(inv, target);

        viewingPlayers.put(viewer.getUniqueId(), target);
        viewInventories.put(viewer.getUniqueId(), inv);
        
        viewer.openInventory(inv);
        viewer.sendMessage(Message.getComponent("inventory.editing", "<gradient:#48dbfb:#1dd1a1>✦ Editing %target%'s inventory!</gradient>", "target", target.getName()));

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(MCBasics.getInstance(), () -> {
            if (!viewer.isOnline() || !target.isOnline()) {
                cleanup(viewer);
                return;
            }
            Inventory currentView = viewInventories.get(viewer.getUniqueId());
            if (currentView == null) {
                cleanup(viewer);
                return;
            }
            syncInventoryToView(currentView, target);
        }, 10L, 10L);
        syncTasks.put(viewer.getUniqueId(), task);
    }

    private void cleanup(Player viewer) {
        viewingPlayers.remove(viewer.getUniqueId());
        viewInventories.remove(viewer.getUniqueId());
        BukkitTask task = syncTasks.remove(viewer.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    private void syncInventoryToView(Inventory view, Player target) {
        PlayerInventory targetInv = target.getInventory();
        
        for (int i = 0; i < 36; i++) {
            view.setItem(i, targetInv.getItem(i));
        }

        view.setItem(36, targetInv.getHelmet());
        view.setItem(37, targetInv.getChestplate());
        view.setItem(38, targetInv.getLeggings());
        view.setItem(39, targetInv.getBoots());
        view.setItem(40, targetInv.getItemInOffHand());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player viewer = (Player) event.getWhoClicked();
        Player target = viewingPlayers.get(viewer.getUniqueId());
        
        if (target == null) return;
        
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) return;

        Inventory viewInv = viewInventories.get(viewer.getUniqueId());
        if (viewInv == null || clickedInv != viewInv) return;

        int slot = event.getRawSlot();
        
        Bukkit.getScheduler().runTaskLater(MCBasics.getInstance(), () -> {
            syncSlotToTarget(viewInv, target, slot);
        }, 1L);
    }

    private void syncSlotToTarget(Inventory view, Player target, int slot) {
        ItemStack item = view.getItem(slot);
        PlayerInventory targetInv = target.getInventory();
        
        if (slot < 36) {
            targetInv.setItem(slot, item);
        } else if (slot == 36) {
            targetInv.setHelmet(item);
        } else if (slot == 37) {
            targetInv.setChestplate(item);
        } else if (slot == 38) {
            targetInv.setLeggings(item);
        } else if (slot == 39) {
            targetInv.setBoots(item);
        } else if (slot == 40) {
            targetInv.setItemInOffHand(item);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player viewer = (Player) event.getWhoClicked();
        Player target = viewingPlayers.get(viewer.getUniqueId());
        
        if (target == null) return;
        
        Inventory viewInv = viewInventories.get(viewer.getUniqueId());
        if (viewInv == null) return;

        Bukkit.getScheduler().runTaskLater(MCBasics.getInstance(), () -> {
            for (int slot : event.getRawSlots()) {
                if (slot < 45) {
                    syncSlotToTarget(viewInv, target, slot);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player viewer = (Player) event.getPlayer();
        cleanup(viewer);
    }
}
