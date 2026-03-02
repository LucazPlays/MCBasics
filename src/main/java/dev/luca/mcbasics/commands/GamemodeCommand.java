package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.GM)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        Player target = null;
        GameMode mode = null;
        boolean targetAll = false;

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
            if (args.length == 0) {
                mode = GameMode.SURVIVAL;
            } else {
                String firstArg = args[0].toLowerCase();

                GameMode parsedMode = parseGamemode(firstArg);
                if (parsedMode != null) {
                    mode = parsedMode;
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("@a")) {
                            if (!sender.hasPermission(Permission.GM_OTHERS)) {
                                sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
                                return true;
                            }
                            targetAll = true;
                        } else if (sender.hasPermission(Permission.GM_OTHERS)) {
                            target = Bukkit.getPlayer(args[1]);
                            if (target == null) {
                                sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                                return true;
                            }
                        }
                    }
                } else if (sender.hasPermission(Permission.GM_OTHERS)) {
                    if (args[0].equalsIgnoreCase("@a")) {
                        targetAll = true;
                    } else {
                        target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                            return true;
                        }
                    }
                } else {
                    sender.sendMessage(Message.getComponent("gamemode.invalid_mode", "<gradient:#ff6b6b:#ee5a24>✖ Invalid gamemode! Use 0, 1, 2, 3, survival, creative, adventure, or spectator</gradient>"));
                    return true;
                }
            }
        }

        if (mode == null) {
            sender.sendMessage(Message.getComponent("gamemode.invalid_mode", "<gradient:#ff6b6b:#ee5a24>✖ Invalid gamemode! Use 0, 1, 2, 3, survival, creative, adventure, or spectator</gradient>"));
            return true;
        }

        if (!targetAll && target == null) {
            if (labelLower.equals("gmc") || labelLower.equals("gms") || labelLower.equals("gma") || labelLower.equals("gmsp")) {
                if (sender.hasPermission(Permission.GM_OTHERS) && args.length > 0) {
                    if (args[0].equalsIgnoreCase("@a")) {
                        targetAll = true;
                    } else {
                        target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                            return true;
                        }
                    }
                }
            }
        }

        if (targetAll) {
            int count = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setGameMode(mode);
                player.sendMessage(Message.getComponent("gamemode.set_to", "<gradient:#48dbfb:#1dd1a1>✦ Your gamemode has been set to %mode%!</gradient>", "mode", mode.name().toLowerCase()));
                count++;
            }
            sender.sendMessage(Message.getComponent("gamemode.set_all", "<gradient:#48dbfb:#1dd1a1>✦ All players (%count%) gamemode set to %mode%!</gradient>", "count", String.valueOf(count), "mode", mode.name().toLowerCase()));
            return true;
        }

        if (target == null) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage(Message.getComponent("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
                return true;
            }
        }

        target.setGameMode(mode);
        String modeName = mode.name().toLowerCase();

        target.sendMessage(Message.getComponent("gamemode.set_to", "<gradient:#48dbfb:#1dd1a1>✦ Your gamemode has been set to %mode%!</gradient>", "mode", modeName));
        if (target != sender) {
            sender.sendMessage(Message.getComponent("gamemode.set_other", "<gradient:#48dbfb:#1dd1a1>✦ %target%'s gamemode set to %mode%!</gradient>", "target", target.getName(), "mode", modeName));
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Set<String> completions = new LinkedHashSet<>();

        if (!sender.hasPermission(Permission.GM)) {
            return new ArrayList<>();
        }

        String aliasLower = alias.toLowerCase();
        boolean fixedModeAlias = aliasLower.equals("gmc")
                || aliasLower.equals("gms")
                || aliasLower.equals("gma")
                || aliasLower.equals("gmsp")
                || aliasLower.equals("survival")
                || aliasLower.equals("creative")
                || aliasLower.equals("adventure")
                || aliasLower.equals("spectator");

        if (fixedModeAlias) {
            if (args.length == 1 && sender.hasPermission(Permission.GM_OTHERS)) {
                addPlayerCompletions(completions, args[0]);
            }
            return new ArrayList<>(completions);
        }

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            if ("survival".startsWith(partial) || "s".startsWith(partial)) {
                completions.add("survival");
            }
            if ("creative".startsWith(partial) || "c".startsWith(partial)) {
                completions.add("creative");
            }
            if ("adventure".startsWith(partial) || "a".startsWith(partial)) {
                completions.add("adventure");
            }
            if ("spectator".startsWith(partial) || "sp".startsWith(partial)) {
                completions.add("spectator");
            }

            if (sender.hasPermission(Permission.GM_OTHERS)) {
                addPlayerCompletions(completions, args[0]);
            }
        } else if (args.length == 2 && sender.hasPermission(Permission.GM_OTHERS)) {
            addPlayerCompletions(completions, args[1]);
        }

        return new ArrayList<>(completions);
    }

    private void addPlayerCompletions(Set<String> completions, String input) {
        String partial = input.toLowerCase();
        if ("@a".startsWith(partial)) {
            completions.add("@a");
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(partial)) {
                completions.add(player.getName());
            }
        }
    }
}
