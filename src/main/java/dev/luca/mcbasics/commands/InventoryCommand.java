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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryCommand implements CommandExecutor, Listener {

    private static final Map<UUID, Player> viewingPlayers = new HashMap<>();
    private static final Map<UUID, Boolean> syncPending = new HashMap<>();

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
        syncPending.put(viewer.getUniqueId(), false);
        viewer.openInventory(mainInv);
        viewer.sendMessage(Message.getComponent("inventory.editing", "<gradient:#48dbfb:#1dd1a1>✦ Editing %target%'s inventory!</gradient>", "target", target.getName()));
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

        if (event.getAction() == InventoryAction.NOTHING) return;

        event.setCancelled(true);

        syncPending.put(viewer.getUniqueId(), true);

        Bukkit.getScheduler().runTaskLater(MCBasics.getInstance(), () -> {
            if (Boolean.TRUE.equals(syncPending.get(viewer.getUniqueId()))) {
                syncToPlayer(viewer, target);
                syncPending.put(viewer.getUniqueId(), false);
            }
        }, 2L);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        
        Player viewer = (Player) event.getPlayer();
        Player target = viewingPlayers.remove(viewer.getUniqueId());
        
        if (target == null) return;

        syncPending.remove(viewer.getUniqueId());
        syncToPlayer(viewer, target);
    }

    private void syncToPlayer(Player viewer, Player target) {
        Inventory viewInv = viewer.getOpenInventory().getTopInventory();
        
        if (viewInv == null) return;

        ItemStack[] currentContents = target.getInventory().getContents();
        ItemStack[] newContents = new ItemStack[36];

        for (int i = 0; i < 36; i++) {
            ItemStack item = viewInv.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                newContents[i] = item;
            } else if (i < currentContents.length && currentContents[i] != null) {
                newContents[i] = currentContents[i];
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
