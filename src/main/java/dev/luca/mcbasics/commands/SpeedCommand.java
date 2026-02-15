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
        boolean isFlying = label.equalsIgnoreCase("flyspeed");
        
        String basePermission = isFlying ? "mcbasics.flyspeed" : "mcbasics.speed";
        String othersPermission = isFlying ? "mcbasics.flyspeed.others" : "mcbasics.speed.others";
        
        if (!sender.hasPermission(basePermission)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        Player target;
        float speed = 1.0f;

        if (args.length > 0) {
            try {
                int speedInt = Integer.parseInt(args[0]);
                if (speedInt < 1 || speedInt > 10) {
                    sender.sendMessage(Message.getComponent("speed.invalid_speed", "<gradient:#ff6b6b:#ee5a24>✖ Speed must be between 1 and 10!</gradient>"));
                    return true;
                }
                speed = speedInt / 10.0f;
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.getComponent("speed.invalid_speed", "<gradient:#ff6b6b:#ee5a24>✖ Speed must be between 1 and 10!</gradient>"));
                return true;
            }
        }

        String speedType = isFlying ? "flight" : "walking";

        if (args.length > 1 && sender.hasPermission(othersPermission)) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.getComponent("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
            return true;
        }

        if (isFlying) {
            target.setFlySpeed(speed);
        } else {
            target.setWalkSpeed(speed);
        }

        String speedNum = Message.get("speed.speed_number", "%speed%", "speed", String.valueOf((int)(speed * 10)));

        if (isFlying) {
            target.sendMessage(Message.getComponent("speed.flight_set", "<gradient:#48dbfb:#1dd1a1>✦ Your flight speed has been set to %speed%!</gradient>", "speed", speedNum));
            if (target != sender) {
                sender.sendMessage(Message.getComponent("speed.flight_set_other", "<gradient:#48dbfb:#1dd1a1>✦ %target%'s flight speed set to %speed%!</gradient>", "target", target.getName(), "speed", speedNum));
            }
        } else {
            target.sendMessage(Message.getComponent("speed.walking_set", "<gradient:#48dbfb:#1dd1a1>✦ Your walking speed has been set to %speed%!</gradient>", "speed", speedNum));
            if (target != sender) {
                sender.sendMessage(Message.getComponent("speed.walking_set_other", "<gradient:#48dbfb:#1dd1a1>✦ %target%'s walking speed set to %speed%!</gradient>", "target", target.getName(), "speed", speedNum));
            }
        }

        return true;
    }
}
