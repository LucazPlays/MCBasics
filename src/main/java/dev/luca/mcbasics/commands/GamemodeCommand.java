package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import dev.luca.mcbasics.api.TargetSelector;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GamemodeCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.GM)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        GameMode mode = null;
        String targetArg = null;
        String labelLower = label.toLowerCase();

        // 1. Determine Gamemode and Target Argument
        if (labelLower.equals("gmc")) {
            mode = GameMode.CREATIVE;
            if (args.length > 0) targetArg = args[0];
        } else if (labelLower.equals("gms") || labelLower.equals("survival")) {
            mode = GameMode.SURVIVAL;
            if (args.length > 0) targetArg = args[0];
        } else if (labelLower.equals("gma") || labelLower.equals("adventure")) {
            mode = GameMode.ADVENTURE;
            if (args.length > 0) targetArg = args[0];
        } else if (labelLower.equals("gmsp") || labelLower.equals("spectator")) {
            mode = GameMode.SPECTATOR;
            if (args.length > 0) targetArg = args[0];
        } else if (labelLower.equals("gamemode") || labelLower.equals("gm")) {
            if (args.length == 0) {
                mode = GameMode.SURVIVAL; // Default for /gm
            } else {
                mode = parseGamemode(args[0].toLowerCase());
                if (mode != null) {
                    if (args.length > 1) targetArg = args[1];
                } else {
                    // Argument might be a target instead of a mode (if using /gm <player>)
                    if (sender.hasPermission(Permission.GM_OTHERS)) {
                        targetArg = args[0];
                        mode = GameMode.SURVIVAL; // Default if only player specified
                    }
                }
            }
        }

        if (mode == null) {
            sender.sendMessage(FormattedMessage.create("gamemode.invalid_mode", "<gradient:#ff6b6b:#ee5a24>✖ Invalid gamemode! Use 0, 1, 2, 3, survival, creative, adventure, or spectator</gradient>"));
            return true;
        }

        // 2. Resolve Targets
        List<Player> targets;
        if (targetArg != null) {
            if (!sender.hasPermission(Permission.GM_OTHERS)) {
                sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
                return true;
            }
            targets = TargetSelector.selectPlayers(sender, targetArg);
            if (targets.isEmpty()) {
                sender.sendMessage(FormattedMessage.create("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player) {
            targets = Collections.singletonList((Player) sender);
        } else {
            sender.sendMessage(FormattedMessage.create("general.specify_player", "<gradient:#ff6b6b:#ee5a24>✖ Specify a player!</gradient>"));
            return true;
        }

        // 3. Apply changes
        String modeName = mode.name().toLowerCase();
        for (Player target : targets) {
            target.setGameMode(mode);
            target.sendMessage(FormattedMessage.create("gamemode.set_to", "<gradient:#48dbfb:#1dd1a1>✦ Your gamemode has been set to %mode%!</gradient>", "mode", modeName));
        }

        // 4. Feedback
        if (targets.size() > 1) {
            sender.sendMessage(FormattedMessage.create("gamemode.set_all", "<gradient:#48dbfb:#1dd1a1>✦ All players (%count%) gamemode set to %mode%!</gradient>", "count", String.valueOf(targets.size()), "mode", modeName));
        } else if (targets.get(0) != sender) {
            sender.sendMessage(FormattedMessage.create("gamemode.set_other", "<gradient:#48dbfb:#1dd1a1>✦ %target%'s gamemode set to %mode%!</gradient>", "target", targets.get(0).getName(), "mode", modeName));
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

            if ("survival".startsWith(partial) || "s".startsWith(partial)) completions.add("survival");
            if ("creative".startsWith(partial) || "c".startsWith(partial)) completions.add("creative");
            if ("adventure".startsWith(partial) || "a".startsWith(partial)) completions.add("adventure");
            if ("spectator".startsWith(partial) || "sp".startsWith(partial)) completions.add("spectator");

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
        if ("@a".startsWith(partial)) completions.add("@a");
        if ("@p".startsWith(partial)) completions.add("@p");
        if ("@r".startsWith(partial)) completions.add("@r");
        if ("@s".startsWith(partial)) completions.add("@s");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(partial)) {
                completions.add(player.getName());
            }
        }
    }
}
