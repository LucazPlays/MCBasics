package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NightCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.NIGHT)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.getWorld().setTime(13000);
            player.sendMessage(Message.getComponent("night.set", "<gradient:#48dbfb:#1dd1a1>✦ Time set to night!</gradient>"));
        } else {
            sender.getServer().getWorlds().forEach(world -> world.setTime(13000));
            sender.sendMessage(Message.getComponent("night.set", "<gradient:#48dbfb:#1dd1a1>✦ Time set to night!</gradient>"));
        }

        return true;
    }
}
