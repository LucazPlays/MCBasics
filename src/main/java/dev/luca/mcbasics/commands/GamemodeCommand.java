package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("essentials.gm")) {
            sender.sendMessage(Message.get("general.no_permission", ""));
            return true;
        }

        Player target;
        GameMode mode;

        if (label.equalsIgnoreCase("gmc")) {
            mode = GameMode.CREATIVE;
        } else if (label.equalsIgnoreCase("gms")) {
            mode = GameMode.SURVIVAL;
        } else if (label.equalsIgnoreCase("gma")) {
            mode = GameMode.ADVENTURE;
        } else if (label.equalsIgnoreCase("gmsp")) {
            mode = GameMode.SPECTATOR;
        } else {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Message.get("general.specify_player", ""));
                    return true;
                }
                target = (Player) sender;
            } else {
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Message.get("general.player_not_found", ""));
                    return true;
                }
            }

            try {
                int modeNum = Integer.parseInt(args[0]);
                switch (modeNum) {
                    case 0:
                        mode = GameMode.SURVIVAL;
                        break;
                    case 1:
                        mode = GameMode.CREATIVE;
                        break;
                    case 2:
                        mode = GameMode.ADVENTURE;
                        break;
                    case 3:
                        mode = GameMode.SPECTATOR;
                        break;
                    default:
                        sender.sendMessage(Message.get("gamemode.invalid_mode", ""));
                        return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.get("gamemode.use_numbers", ""));
                return true;
            }
        }

        if (args.length > 1 && sender.hasPermission("essentials.gm.others")) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(Message.get("general.player_not_found", ""));
                return true;
            }
        } else if (!(sender instanceof Player) && label.equalsIgnoreCase("gm")) {
            sender.sendMessage(Message.get("general.specify_player", ""));
            return true;
        } else if (label.equalsIgnoreCase("gm")) {
            target = (Player) sender;
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Message.get("general.specify_player", ""));
            return true;
        }

        if (target == null) {
            sender.sendMessage(Message.get("general.player_not_found", ""));
            return true;
        }

        target.setGameMode(mode);
        String modeName = mode.name().toLowerCase();
        String modeKey = "gamemode.mode_names." + modeName;
        String localizedMode = Message.get(modeKey, modeName, "mode", modeName);

        target.sendMessage(Message.get("gamemode.set_to", "", "mode", localizedMode));
        if (target != sender) {
            sender.sendMessage(Message.get("gamemode.set_other", "", "target", target.getName(), "mode", localizedMode));
        }

        return true;
    }
}
