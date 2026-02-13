package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.MCBasics;
import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
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
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        Player target;

        if (args.length > 0 && sender.hasPermission(Permission.GOD_OTHERS)) {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        boolean isGod = toggleGodMode(target);

        if (isGod) {
            target.sendMessage(Message.getComponent("god.enabled", "<gradient:#48dbfb:#1dd1a1>✦ God mode enabled!</gradient>"));
            if (target != sender) {
                sender.sendMessage(Message.getComponent("god.enabled_other", "<gradient:#48dbfb:#1dd1a1>✦ God mode enabled for %target%!</gradient>", "target", target.getName()));
            }
        } else {
            target.sendMessage(Message.getComponent("god.disabled", "<gradient:#ff6b6b:#ee5a24>✦ God mode disabled!</gradient>"));
            if (target != sender) {
                sender.sendMessage(Message.getComponent("god.disabled_other", "<gradient:#ff6b6b:#ee5a24>✦ God mode disabled for %target%!</gradient>", "target", target.getName()));
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
