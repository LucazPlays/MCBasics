package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.MCBasics;
import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import dev.luca.mcbasics.api.TargetSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GodCommand implements CommandExecutor, Listener {

    private static final Set<UUID> godModePlayers = new HashSet<>();

    public GodCommand() {
        MCBasics.getInstance().getServer().getPluginManager().registerEvents(this, MCBasics.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.GOD)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        List<Player> targets;

        if (args.length > 0) {
            if (!sender.hasPermission(Permission.GOD_OTHERS)) {
                sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
                return true;
            }
            targets = TargetSelector.selectPlayers(sender, args[0]);
            if (targets.isEmpty()) {
                sender.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            targets = Collections.singletonList((Player) sender);
        } else {
            sender.sendMessage(FormattedMessage.create("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
            return true;
        }

        for (Player target : targets) {
            boolean isGod = toggleGodMode(target);

            if (isGod) {
                target.sendMessage(FormattedMessage.create("god.enabled", "<gradient:#48dbfb:#1dd1a1>✦ God mode enabled!</gradient>"));
            } else {
                target.sendMessage(FormattedMessage.create("god.disabled", "<gradient:#ff6b6b:#ee5a24>✦ God mode disabled!</gradient>"));
            }
        }

        if (targets.size() > 1) {
            sender.sendMessage(FormattedMessage.create("god.toggled_all", "<gradient:#48dbfb:#1dd1a1>✦ God mode toggled for %count% players!</gradient>", "count", String.valueOf(targets.size())));
        } else if (targets.get(0) != sender) {
            Player target = targets.get(0);
            if (hasGodMode(target)) {
                sender.sendMessage(FormattedMessage.create("god.enabled_other", "<gradient:#48dbfb:#1dd1a1>✦ God mode enabled for %target%!</gradient>", "target", target.getName()));
            } else {
                sender.sendMessage(FormattedMessage.create("god.disabled_other", "<gradient:#ff6b6b:#ee5a24>✦ God mode disabled for %target%!</gradient>", "target", target.getName()));
            }
        }

        return true;
    }

    public boolean toggleGodMode(Player player) {
        UUID uuid = player.getUniqueId();
        if (godModePlayers.contains(uuid)) {
            godModePlayers.remove(uuid);
            return false;
        } else {
            godModePlayers.add(uuid);
            return true;
        }
    }

    public boolean hasGodMode(Player player) {
        return godModePlayers.contains(player.getUniqueId());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (godModePlayers.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
