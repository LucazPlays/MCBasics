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

        if (!viewInventories.containsValue(clickedInv)) {
            return;
        }

        int slot = event.getRawSlot();
        
        if (slot < 36) {
            target.getInventory().setItem(slot, event.getCursor());
        } else if (slot == 36) {
            target.getInventory().setHelmet(event.getCursor());
        } else if (slot == 37) {
            target.getInventory().setChestplate(event.getCursor());
        } else if (slot == 38) {
            target.getInventory().setLeggings(event.getCursor());
        } else if (slot == 39) {
            target.getInventory().setBoots(event.getCursor());
        } else if (slot == 40) {
            target.getInventory().setItemInOffHand(event.getCursor());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player viewer = (Player) event.getWhoClicked();
        Player target = viewingPlayers.get(viewer.getUniqueId());
        
        if (target == null) return;
        
        Inventory topInv = event.getView().getTopInventory();
        if (!viewInventories.containsValue(topInv)) return;

        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            int slot = entry.getKey();
            ItemStack item = entry.getValue();
            
            if (slot < 36) {
                target.getInventory().setItem(slot, item);
            } else if (slot == 36) {
                target.getInventory().setHelmet(item);
            } else if (slot == 37) {
                target.getInventory().setChestplate(item);
            } else if (slot == 38) {
                target.getInventory().setLeggings(item);
            } else if (slot == 39) {
                target.getInventory().setBoots(item);
            } else if (slot == 40) {
                target.getInventory().setItemInOffHand(item);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player viewer = (Player) event.getPlayer();
        cleanup(viewer);
    }
}
