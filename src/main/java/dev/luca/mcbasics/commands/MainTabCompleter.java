package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.TargetSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String cmdName = command.getName().toLowerCase();

        switch (cmdName) {
            case "vanish":
                return vanishTabComplete(args);
            case "gm":
            case "gmc":
            case "gms":
            case "gma":
            case "gmsp":
                return gmTabComplete(sender, args);
            case "invsee":
                return invseeTabComplete(args);
            case "tphere":
                return tphereTabComplete(args);
            case "feed":
                return feedTabComplete(sender, args);
            case "heal":
                return healTabComplete(sender, args);
            case "speed":
            case "flyspeed":
                return speedTabComplete(sender, command, args);
            case "fly":
                return flyTabComplete(sender, args);
            case "unsafeenchant":
                return unsafeEnchantTabComplete(args);
            case "playertransfer":
                return playerTransferTabComplete(args);
            case "sudo":
            case "skull":
            case "freeze":
                return playerOnlyTargetTabComplete(args, 0);
            case "ping":
                return playerTargetTabComplete(sender, args, 0, "mcbasics.ping.others");
            case "god":
                return playerTargetTabComplete(sender, args, 0, "mcbasics.god.others");
            default:
                return new ArrayList<>();
        }
    }

    private List<String> vanishTabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "on", "off", "toggle");
        }
        if (args.length == 2) {
            // vanish [sub] <player>
            return TargetSelector.getPlayerTabCompletions(args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> gmTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.gm.others")) {
            return TargetSelector.getPlayerTabCompletions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> invseeTabComplete(String[] args) {
        if (args.length == 1) {
            return TargetSelector.getPlayerTabCompletions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> tphereTabComplete(String[] args) {
        if (args.length == 1) {
            return TargetSelector.getPlayerTabCompletions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> feedTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.feed.others")) {
            return TargetSelector.getPlayerTabCompletions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> healTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.heal.others")) {
            return TargetSelector.getPlayerTabCompletions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> speedTabComplete(CommandSender sender, Command command, String[] args) {
        String othersPermission = command.getName().equalsIgnoreCase("flyspeed")
                ? "mcbasics.flyspeed.others"
                : "mcbasics.speed.others";
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            for (int i = 1; i <= 10; i++) {
                completions.add(String.valueOf(i));
            }
        } else if (args.length == 2 && sender.hasPermission(othersPermission)) {
            completions = TargetSelector.getPlayerTabCompletions(args[1]);
        }
        return completions;
    }

    private List<String> flyTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.fly.others")) {
            return TargetSelector.getPlayerTabCompletions(args[0]);
        }
        return new ArrayList<>();
    }

    private List<String> unsafeEnchantTabComplete(String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String ench : UnsafeEnchantCommand.getEnchantmentNames()) {
                if (ench.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(ench);
                }
            }
            return completions;
        } else if (args.length == 2) {
            return Arrays.asList("1", "5", "10", "50", "100", "32767");
        } else if (args.length == 3) {
            return TargetSelector.getPlayerTabCompletions(args[2]);
        }
        return new ArrayList<>();
    }

    private List<String> playerTransferTabComplete(String[] args) {
        if (args.length == 1) {
            return TargetSelector.getPlayerTabCompletions(args[0]);
        } else if (args.length == 3) {
            return Arrays.asList("25565", "19132", "25575", "25566");
        } else if (args.length == 4) {
            return Arrays.asList("100", "500", "1000", "2000", "5000");
        }
        return new ArrayList<>();
    }

    /**
     * For commands like sudo/skull/freeze where the first arg is always a player target
     * (no separate "others" permission gating for tab suggestions).
     */
    private List<String> playerOnlyTargetTabComplete(String[] args, int playerArgIndex) {
        if (args.length != playerArgIndex + 1) {
            return new ArrayList<>();
        }
        return TargetSelector.getPlayerTabCompletions(args[playerArgIndex]);
    }

    /**
     * For commands with explicit <perm>.others gating for suggesting other players.
     */
    private List<String> playerTargetTabComplete(CommandSender sender, String[] args, int playerArgIndex, String othersPermission) {
        if (args.length != playerArgIndex + 1) {
            return new ArrayList<>();
        }
        if (othersPermission != null && !sender.hasPermission(othersPermission)) {
            return new ArrayList<>();
        }
        return TargetSelector.getPlayerTabCompletions(args[playerArgIndex]);
    }
}
