package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.DAY)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.getWorld().setTime(1000);
            player.sendMessage(Message.getComponent("day.set", "<gradient:#48dbfb:#1dd1a1>✦ Time set to day!</gradient>"));
        } else {
            sender.getServer().getWorlds().forEach(world -> world.setTime(1000));
            sender.sendMessage(Message.getComponent("day.set", "<gradient:#48dbfb:#1dd1a1>✦ Time set to day!</gradient>"));
        }

        return true;
    }
}
