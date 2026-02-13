package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.MCBasics;
import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryCommand implements CommandExecutor, Listener {

    private static final Map<UUID, Player> viewingPlayers = new HashMap<>();
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
        if (syncTasks.containsKey(viewer.getUniqueId())) {
            syncTasks.get(viewer.getUniqueId()).cancel();
        }

        Inventory mainInv = createMainInventory(target);
        viewingPlayers.put(viewer.getUniqueId(), target);
        viewer.openInventory(mainInv);
        viewer.sendMessage(Message.getComponent("inventory.editing", "<gradient:#48dbfb:#1dd1a1>✦ Editing %target%'s inventory!</gradient>", "target", target.getName()));

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(MCBasics.getInstance(), () -> {
            syncFromTarget(viewer, target);
        }, 2L, 2L);
        syncTasks.put(viewer.getUniqueId(), task);
    }

    private Inventory createMainInventory(Player target) {
        Inventory inv = Bukkit.createInventory(null, 45, "§8" + target.getName() + "'s Inventory");

        ItemStack[] contents = target.getInventory().getContents();
        for (int i = 0; i < contents.length && i < 36; i++) {
            inv.setItem(i, contents[i] != null ? contents[i] : new ItemStack(Material.AIR));
        }

        inv.setItem(36, target.getInventory().getHelmet());
        inv.setItem(37, target.getInventory().getChestplate());
        inv.setItem(38, target.getInventory().getLeggings());
        inv.setItem(39, target.getInventory().getBoots());
        inv.setItem(40, target.getInventory().getItemInOffHand());

        return inv;
    }

    private void syncFromTarget(Player viewer, Player target) {
        if (!viewer.isOnline() || !target.isOnline()) return;
        
        Inventory viewInv = viewer.getOpenInventory().getTopInventory();
        if (viewInv == null) return;

        ItemStack[] targetContents = target.getInventory().getContents();
        for (int i = 0; i < 36; i++) {
            ItemStack targetItem = i < targetContents.length ? targetContents[i] : null;
            ItemStack viewItem = viewInv.getItem(i);
            
            if (targetItem == null || targetItem.getType() == Material.AIR) {
                if (viewItem != null && viewItem.getType() != Material.AIR) {
                    viewInv.setItem(i, new ItemStack(Material.AIR));
                }
            } else if (viewItem == null || viewItem.getType() == Material.AIR || !viewItem.isSimilar(targetItem)) {
                viewInv.setItem(i, targetItem);
            }
        }

        viewInv.setItem(36, target.getInventory().getHelmet());
        viewInv.setItem(37, target.getInventory().getChestplate());
        viewInv.setItem(38, target.getInventory().getLeggings());
        viewInv.setItem(39, target.getInventory().getBoots());
        viewInv.setItem(40, target.getInventory().getItemInOffHand());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player viewer = (Player) event.getWhoClicked();
        Player target = viewingPlayers.get(viewer.getUniqueId());
        
        if (target == null) return;

        if (event.getInventory().getType() == InventoryType.CRAFTING || 
            event.getInventory().getType() == InventoryType.PLAYER) {
            return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player viewer = (Player) event.getPlayer();
        Player target = viewingPlayers.remove(viewer.getUniqueId());
        
        BukkitTask task = syncTasks.remove(viewer.getUniqueId());
        if (task != null) {
            task.cancel();
        }
        
        if (target == null) return;

        syncToPlayer(viewer, target);
    }

    private void syncToPlayer(Player viewer, Player target) {
        Inventory viewInv = viewer.getOpenInventory().getTopInventory();
        
        if (viewInv == null) return;

        ItemStack[] newContents = new ItemStack[36];

        for (int i = 0; i < 36; i++) {
            ItemStack item = viewInv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                newContents[i] = item;
            }
        }

        target.getInventory().setContents(newContents);

        ItemStack helmet = viewInv.getItem(36);
        ItemStack chestplate = viewInv.getItem(37);
        ItemStack leggings = viewInv.getItem(38);
        ItemStack boots = viewInv.getItem(39);
        ItemStack offhand = viewInv.getItem(40);

        target.getInventory().setHelmet(helmet != null && helmet.getType() != Material.AIR ? helmet : null);
        target.getInventory().setChestplate(chestplate != null && chestplate.getType() != Material.AIR ? chestplate : null);
        target.getInventory().setLeggings(leggings != null && leggings.getType() != Material.AIR ? leggings : null);
        target.getInventory().setBoots(boots != null && boots.getType() != Material.AIR ? boots : null);
        target.getInventory().setItemInOffHand(offhand != null && offhand.getType() != Material.AIR ? offhand : null);

        ItemStack cursor = viewer.getItemOnCursor();
        if (cursor != null && cursor.getType() != Material.AIR) {
            Map<Integer, ItemStack> leftover = target.getInventory().addItem(cursor);
            if (!leftover.isEmpty()) {
                for (ItemStack item : leftover.values()) {
                    viewer.getWorld().dropItemNaturally(viewer.getLocation(), item);
                }
            }
            viewer.setItemOnCursor(null);
        }

        target.updateInventory();
    }
}
