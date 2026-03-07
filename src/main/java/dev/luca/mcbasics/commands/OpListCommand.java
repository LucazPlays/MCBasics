package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.MCBasics;
import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OpListCommand implements CommandExecutor, Listener {

    private static final int PAGE_SIZE = 45;
    private static final String LIST_TITLE_PREFIX = "§8OP List §7(Page ";
    private static final String DETAIL_TITLE_PREFIX = "§8OP: ";

    private final NamespacedKey operatorUuidKey;
    private final NamespacedKey actionKey;
    private final Map<UUID, Integer> pageByViewer = new HashMap<>();
    private final Map<UUID, UUID> selectedOpByViewer = new HashMap<>();
    private final Map<UUID, Integer> configuredLevels = new HashMap<>();

    public OpListCommand() {
        MCBasics plugin = MCBasics.getInstance();
        this.operatorUuidKey = new NamespacedKey(plugin, "oplist_operator_uuid");
        this.actionKey = new NamespacedKey(plugin, "oplist_action");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.OPLIST)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(FormattedMessage.create("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        if (args.length != 0) {
            sender.sendMessage(FormattedMessage.create("oplist.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /oplist</gradient>"));
            return true;
        }

        openListMenu((Player) sender, 0);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player viewer = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.startsWith(LIST_TITLE_PREFIX)) {
            handleListClick(event, viewer);
            return;
        }

        if (title.startsWith(DETAIL_TITLE_PREFIX)) {
            handleDetailClick(event, viewer);
        }
    }

    private void handleListClick(InventoryClickEvent event, Player viewer) {
        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        String action = meta.getPersistentDataContainer().get(actionKey, PersistentDataType.STRING);
        if (action != null) {
            int currentPage = pageByViewer.getOrDefault(viewer.getUniqueId(), 0);
            if (action.equals("prev")) {
                openListMenu(viewer, Math.max(0, currentPage - 1));
            } else if (action.equals("next")) {
                openListMenu(viewer, currentPage + 1);
            }
            return;
        }

        String operatorUuidRaw = meta.getPersistentDataContainer().get(operatorUuidKey, PersistentDataType.STRING);
        if (operatorUuidRaw == null) {
            return;
        }

        try {
            UUID operatorUuid = UUID.fromString(operatorUuidRaw);
            selectedOpByViewer.put(viewer.getUniqueId(), operatorUuid);
            openDetailMenu(viewer, operatorUuid);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void handleDetailClick(InventoryClickEvent event, Player viewer) {
        event.setCancelled(true);

        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR || !clicked.hasItemMeta()) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        String action = meta.getPersistentDataContainer().get(actionKey, PersistentDataType.STRING);
        if (action == null) {
            return;
        }

        if (action.equals("back")) {
            openListMenu(viewer, pageByViewer.getOrDefault(viewer.getUniqueId(), 0));
            return;
        }

        if (!action.startsWith("set_level_")) {
            return;
        }

        UUID operatorUuid = selectedOpByViewer.get(viewer.getUniqueId());
        if (operatorUuid == null) {
            viewer.closeInventory();
            return;
        }

        OfflinePlayer operator = Bukkit.getOfflinePlayer(operatorUuid);
        String operatorName = operator.getName();
        if (operatorName == null || operatorName.isBlank()) {
            viewer.sendMessage(FormattedMessage.create("oplist.invalid_target", "<gradient:#ff6b6b:#ee5a24>✖ Could not resolve this operator's name.</gradient>"));
            return;
        }

        int level;
        try {
            level = Integer.parseInt(action.substring("set_level_".length()));
        } catch (NumberFormatException e) {
            return;
        }

        if (level < 1 || level > 4) {
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "op " + operatorName + " " + level + " true");
        configuredLevels.put(operatorUuid, level);
        viewer.sendMessage(FormattedMessage.create(
                "oplist.level_set",
                "<gradient:#48dbfb:#1dd1a1>✦ Set OP level of %target% to %level%!</gradient>",
                "target", operatorName,
                "level", String.valueOf(level)
        ));

        openDetailMenu(viewer, operatorUuid);
    }

    private void openListMenu(Player viewer, int requestedPage) {
        List<OfflinePlayer> operators = getSortedOperators();
        int totalPages = Math.max(1, (int) Math.ceil((double) operators.size() / PAGE_SIZE));
        int page = Math.max(0, Math.min(requestedPage, totalPages - 1));

        pageByViewer.put(viewer.getUniqueId(), page);
        Inventory inv = Bukkit.createInventory(null, 54, LIST_TITLE_PREFIX + (page + 1) + "§8)");

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, operators.size());
        int slot = 0;

        for (int i = start; i < end; i++) {
            inv.setItem(slot++, createOperatorHead(operators.get(i)));
        }

        if (page > 0) {
            inv.setItem(45, createActionItem(Material.ARROW, "§e« Previous Page", "prev"));
        }
        if (page < totalPages - 1) {
            inv.setItem(53, createActionItem(Material.ARROW, "§eNext Page »", "next"));
        }

        inv.setItem(49, createInfoItem("§bOperators: §f" + operators.size(), "§7Page §f" + (page + 1) + "§7/§f" + totalPages));
        viewer.openInventory(inv);
    }

    private void openDetailMenu(Player viewer, UUID operatorUuid) {
        OfflinePlayer operator = Bukkit.getOfflinePlayer(operatorUuid);
        String name = operator.getName() != null ? operator.getName() : operatorUuid.toString();
        int currentLevel = getKnownLevel(operatorUuid);

        Inventory inv = Bukkit.createInventory(null, 27, DETAIL_TITLE_PREFIX + name);
        inv.setItem(4, createDetailHead(operator, currentLevel));

        inv.setItem(10, createLevelItem(1));
        inv.setItem(12, createLevelItem(2));
        inv.setItem(14, createLevelItem(3));
        inv.setItem(16, createLevelItem(4));
        inv.setItem(22, createActionItem(Material.BARRIER, "§cBack to OP List", "back"));

        viewer.openInventory(inv);
    }

    private ItemStack createOperatorHead(OfflinePlayer operator) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) {
            return head;
        }

        String name = operator.getName() != null ? operator.getName() : operator.getUniqueId().toString();
        meta.setOwningPlayer(operator);
        meta.setDisplayName("§b" + name);

        List<String> lore = new ArrayList<>();
        int level = getKnownLevel(operator.getUniqueId());
        lore.add("§7Current OP level: §f" + (level > 0 ? level : "unknown"));
        lore.add("§8");
        lore.add("§eClick to manage");
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(operatorUuidKey, PersistentDataType.STRING, operator.getUniqueId().toString());
        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createDetailHead(OfflinePlayer operator, int currentLevel) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) {
            return head;
        }

        String name = operator.getName() != null ? operator.getName() : operator.getUniqueId().toString();
        meta.setOwningPlayer(operator);
        meta.setDisplayName("§b" + name);

        List<String> lore = new ArrayList<>();
        lore.add("§7UUID: §f" + operator.getUniqueId());
        lore.add("§7Current OP level: §f" + (currentLevel > 0 ? currentLevel : "unknown"));
        lore.add("§8");
        lore.add("§7Select one of the levels below.");
        meta.setLore(lore);

        head.setItemMeta(meta);
        return head;
    }

    private ItemStack createLevelItem(int level) {
        Material material;
        switch (level) {
            case 1:
                material = Material.LIME_DYE;
                break;
            case 2:
                material = Material.YELLOW_DYE;
                break;
            case 3:
                material = Material.ORANGE_DYE;
                break;
            default:
                material = Material.RED_DYE;
                break;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aSet OP Level §f" + level);
            List<String> lore = new ArrayList<>();
            lore.add("§7Apply OP level " + level + " to this player.");
            lore.add("§8");
            lore.add("§eClick to apply");
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, "set_level_" + level);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createActionItem(Material material, String name, String action) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.getPersistentDataContainer().set(actionKey, PersistentDataType.STRING, action);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createInfoItem(String title, String subtitle) {
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta meta = info.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title);
            List<String> lore = new ArrayList<>();
            lore.add(subtitle);
            lore.add("§8");
            lore.add("§7Click a head to configure OP level.");
            meta.setLore(lore);
            info.setItemMeta(meta);
        }
        return info;
    }

    private List<OfflinePlayer> getSortedOperators() {
        List<OfflinePlayer> operators = new ArrayList<>(Bukkit.getOperators());
        operators.sort(Comparator.comparing(op -> {
            String name = op.getName();
            return name == null ? "~" + op.getUniqueId() : ChatColor.stripColor(name).toLowerCase();
        }));
        return operators;
    }

    private int getKnownLevel(UUID operatorUuid) {
        Integer cached = configuredLevels.get(operatorUuid);
        return cached == null ? -1 : cached;
    }
}
