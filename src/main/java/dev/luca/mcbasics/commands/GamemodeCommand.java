package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.GM)) {
            sender.sendMessage(Message.get("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        Player target = null;
        GameMode mode = null;

        String labelLower = label.toLowerCase();

        if (labelLower.equals("gmc")) {
            mode = GameMode.CREATIVE;
        } else if (labelLower.equals("gms") || labelLower.equals("survival")) {
            mode = GameMode.SURVIVAL;
        } else if (labelLower.equals("gma") || labelLower.equals("adventure")) {
            mode = GameMode.ADVENTURE;
        } else if (labelLower.equals("gmsp") || labelLower.equals("spectator")) {
            mode = GameMode.SPECTATOR;
        } else if (labelLower.equals("gamemode") || labelLower.equals("gm")) {
            if (args.length > 0) {
                String firstArg = args[0].toLowerCase();

                GameMode parsedMode = parseGamemode(firstArg);
                if (parsedMode != null) {
                    mode = parsedMode;
                    if (args.length > 1 && sender.hasPermission(Permission.GM_OTHERS)) {
                        target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            sender.sendMessage(Message.get("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                            return true;
                        }
                    }
                } else if (sender.hasPermission(Permission.GM_OTHERS)) {
                    target = Bukkit.getPlayer(args[0]);
                    if (target == null) {
                        sender.sendMessage(Message.get("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                        return true;
                    }
                } else {
                    sender.sendMessage(Message.get("gamemode.invalid_mode", "<gradient:#ff6b6b:#ee5a24>✖ Invalid gamemode! Use 0, 1, 2, 3, survival, creative, adventure, or spectator</gradient>"));
                    return true;
                }
            }
        }

        if (mode == null) {
            sender.sendMessage(Message.get("gamemode.invalid_mode", "<gradient:#ff6b6b:#ee5a24>✖ Invalid gamemode! Use 0, 1, 2, 3, survival, creative, adventure, or spectator</gradient>"));
            return true;
        }

        if (target == null) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(Message.get("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
                return true;
            }
        }

        target.setGameMode(mode);
        String modeName = mode.name().toLowerCase();

        target.sendMessage(Message.get("gamemode.set_to", "<gradient:#48dbfb:#1dd1a1>✦ Your gamemode has been set to %mode%!</gradient>", "mode", modeName));
        if (target != sender) {
            sender.sendMessage(Message.get("gamemode.set_other", "<gradient:#48dbfb:#1dd1a1>✦ %target%'s gamemode set to %mode%!</gradient>", "target", target.getName(), "mode", modeName));
        }

        return true;
    }

    private GameMode parseGamemode(String input) {
        switch (input) {
            case "0":
            case "survival":
            case "s":
                return GameMode.SURVIVAL;
            case "1":
            case "creative":
            case "c":
                return GameMode.CREATIVE;
            case "2":
            case "adventure":
            case "a":
                return GameMode.ADVENTURE;
            case "3":
            case "spectator":
            case "sp":
                return GameMode.SPECTATOR;
            default:
                return null;
        }
    }
}
