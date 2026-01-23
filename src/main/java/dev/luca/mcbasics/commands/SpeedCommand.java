package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.SPEED)) {
            sender.sendMessage(Message.get("general.no_permission", "&cYou don't have permission!"));
            return true;
        }

        Player target;
        float speed = 1.0f;

        if (args.length > 0) {
            try {
                int speedInt = Integer.parseInt(args[0]);
                if (speedInt < 1 || speedInt > 10) {
                    sender.sendMessage(Message.get("speed.invalid_speed", "&cSpeed must be between 1 and 10!"));
                    return true;
                }
                speed = speedInt / 10.0f;
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.get("speed.invalid_speed", "&cSpeed must be between 1 and 10!"));
                return true;
            }
        }

        boolean isFlying = label.equalsIgnoreCase("flyspeed");
        String speedType = isFlying ? "flight" : "walking";

        if (args.length > 1 && sender.hasPermission(Permission.SPEED_OTHERS)) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Message.get("general.player_not_found", "&cPlayer not found!"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.get("general.specify_player", "&cSpecify a player!"));
            return true;
        }

        if (isFlying) {
            target.setFlySpeed(speed);
        } else {
            target.setWalkSpeed(speed);
        }

        String speedNum = Message.get("speed.speed_number", "%speed%", "speed", String.valueOf((int)(speed * 10)));

        if (isFlying) {
            target.sendMessage(Message.get("speed.flight_set", "Your flight speed has been set to %speed%!", "speed", speedNum));
            if (target != sender) {
                sender.sendMessage(Message.get("speed.flight_set_other", "&a%target%'s flight speed set to %speed%!", "target", target.getName(), "speed", speedNum));
            }
        } else {
            target.sendMessage(Message.get("speed.walking_set", "Your walking speed has been set to %speed%!", "speed", speedNum));
            if (target != sender) {
                sender.sendMessage(Message.get("speed.walking_set_other", "&a%target%'s walking speed set to %speed%!", "target", target.getName(), "speed", speedNum));
            }
        }

        return true;
    }
}
