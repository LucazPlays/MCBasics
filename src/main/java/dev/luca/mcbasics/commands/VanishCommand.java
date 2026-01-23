package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.get("general.must_be_player", "&cThis command can only be used by players!"));
            return true;
        }

        Player player = (Player) sender;
        Player target = player;

        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Message.get("general.player_not_found", "&cPlayer not found!"));
                return true;
            }
        }

        if (VanishManager.isVanished(target)) {
            VanishManager.showPlayer(target);
            player.sendMessage(Message.get("vanish.now_visible", "&a%target% is now visible!", "target", target.getName()));
            if (target != player) {
                target.sendMessage(Message.get("vanish.you_visible", "&aYou are now visible!"));
            }
        } else {
            VanishManager.hidePlayer(target);
            player.sendMessage(Message.get("vanish.now_vanished", "&a%target% is now vanished!", "target", target.getName()));
            if (target != player) {
                target.sendMessage(Message.get("vanish.you_vanished", "&aYou are now vanished!"));
            }
        }

        return true;
    }
}
