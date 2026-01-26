package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EnderchestCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.ENDERCHEST)) {
            sender.sendMessage(Message.get("general.no_permission", "&cYou don't have permission!"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.mustBePlayer());
            return true;
        }

        Player target;
        if (args.length > 0 && sender.hasPermission(Permission.ENDERCHEST_OTHERS)) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Message.get("general.player_not_found", "&cPlayer not found!"));
                return true;
            }
        } else {
            target = (Player) sender;
        }

        Player player = (Player) sender;
        player.openInventory(target.getEnderChest());
        player.sendMessage(Message.get("enderchest.opened", "Opening %target%'s enderchest!", "target", target.getName()));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1 && sender.hasPermission(Permission.ENDERCHEST_OTHERS)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }
        return completions;
    }
}
