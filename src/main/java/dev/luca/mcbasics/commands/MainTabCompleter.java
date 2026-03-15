package dev.luca.mcbasics.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            default:
                return new ArrayList<>();
        }
    }

    private List<String> vanishTabComplete(String[] args) {
        if (args.length == 1) {
            return Arrays.asList("list", "on", "off", "toggle");
        }
        if (args.length == 2) {
            return getOnlinePlayerNames();
        }
        return new ArrayList<>();
    }

    private List<String> gmTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.gm.others")) {
            return getOnlinePlayerNames();
        }
        return new ArrayList<>();
    }

    private List<String> invseeTabComplete(String[] args) {
        if (args.length == 1) {
            return getOnlinePlayerNames();
        }
        return new ArrayList<>();
    }

    private List<String> tphereTabComplete(String[] args) {
        if (args.length == 1) {
            return getOnlinePlayerNames();
        }
        return new ArrayList<>();
    }

    private List<String> feedTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.feed.others")) {
            return getOnlinePlayerNames();
        }
        return new ArrayList<>();
    }

    private List<String> healTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.heal.others")) {
            return getOnlinePlayerNames();
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
            completions = getOnlinePlayerNames();
        }
        return completions;
    }

    private List<String> flyTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1 && sender.hasPermission("mcbasics.fly.others")) {
            return getOnlinePlayerNames();
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
            return getOnlinePlayerNames();
        }
        return new ArrayList<>();
    }

    private List<String> getOnlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }
}
