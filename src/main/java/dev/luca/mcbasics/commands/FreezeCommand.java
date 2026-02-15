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
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeCommand implements CommandExecutor, Listener {

    private static final Set<UUID> frozenPlayers = new HashSet<>();

    public FreezeCommand() {
        MCBasics.getInstance().getServer().getPluginManager().registerEvents(this, MCBasics.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.FREEZE)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Message.getComponent("freeze.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /freeze <player></gradient>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
            return true;
        }

        boolean isFrozen = toggleFreeze(target);

        if (isFrozen) {
            target.sendMessage(Message.getComponent("freeze.frozen", "<gradient:#48dbfb:#1dd1a1>✦ You have been frozen!</gradient>"));
            sender.sendMessage(Message.getComponent("freeze.frozen_other", "<gradient:#48dbfb:#1dd1a1>✦ %target% has been frozen!</gradient>", "target", target.getName()));
        } else {
            target.sendMessage(Message.getComponent("freeze.unfrozen", "<gradient:#48dbfb:#1dd1a1>✦ You have been unfrozen!</gradient>"));
            sender.sendMessage(Message.getComponent("freeze.unfrozen_other", "<gradient:#48dbfb:#1dd1a1>✦ %target% has been unfrozen!</gradient>", "target", target.getName()));
        }

        return true;
    }

    public boolean toggleFreeze(Player player) {
        UUID uuid = player.getUniqueId();
        if (frozenPlayers.contains(uuid)) {
            frozenPlayers.remove(uuid);
            player.setAllowFlight(false);
            player.setFlying(false);
            return false;
        } else {
            frozenPlayers.add(uuid);
            player.setAllowFlight(true);
            player.setFlying(true);
            return true;
        }
    }

    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (frozenPlayers.contains(player.getUniqueId())) {
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getZ() != event.getTo().getZ()) {
                player.teleport(event.getFrom());
            }
        }
    }
}
