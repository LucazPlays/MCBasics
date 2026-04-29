package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.FormattedMessage;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RangePlaySoundCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.RANGEPLAYSOUND)) {
            sender.sendMessage(FormattedMessage.create("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        boolean isPlayer = sender instanceof Player;
        if ((isPlayer && args.length != 5 && args.length != 6) || (!isPlayer && args.length != 6)) {
            sender.sendMessage(FormattedMessage.create("rangeplaysound.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /rangeplaysound <range> <sound> <x> <y> <z> [world]</gradient>"));
            return true;
        }

        double range;
        double x;
        double y;
        double z;

        try {
            range = Double.parseDouble(args[0]);
            x = Double.parseDouble(args[2]);
            y = Double.parseDouble(args[3]);
            z = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(FormattedMessage.create("rangeplaysound.invalid_number", "<gradient:#ff6b6b:#ee5a24>✖ Range and coordinates must be valid numbers!</gradient>"));
            return true;
        }

        if (range < 0) {
            sender.sendMessage(FormattedMessage.create("rangeplaysound.invalid_range", "<gradient:#ff6b6b:#ee5a24>✖ Range must be 0 or greater!</gradient>"));
            return true;
        }

        Sound sound = parseSound(args[1]);
        if (sound == null) {
            sender.sendMessage(FormattedMessage.create("rangeplaysound.invalid_sound", "<gradient:#ff6b6b:#ee5a24>✖ Invalid sound!</gradient>"));
            return true;
        }

        World world;
        if (args.length == 6) {
            world = Bukkit.getWorld(args[5]);
            if (world == null) {
                sender.sendMessage(FormattedMessage.create("rangeplaysound.invalid_world", "<gradient:#ff6b6b:#ee5a24>✖ World not found!</gradient>"));
                return true;
            }
        } else if (sender instanceof Player player) {
            world = player.getWorld();
        } else {
            sender.sendMessage(FormattedMessage.create("rangeplaysound.world_required", "<gradient:#ff6b6b:#ee5a24>✖ Console must specify a world!</gradient>"));
            return true;
        }

        Location location = new Location(world, x, y, z);
        double rangeSquared = range * range;
        int count = 0;

        for (Player target : world.getPlayers()) {
            if (target.getLocation().distanceSquared(location) <= rangeSquared) {
                target.playSound(target.getLocation(), sound, 1.0f, 1.0f);
                count++;
            }
        }

        sender.sendMessage(FormattedMessage.create(
                "rangeplaysound.success",
                "<gradient:#48dbfb:#1dd1a1>✦ Played %sound% for %count% player(s) in a range of %range% blocks in %world%!</gradient>",
                "sound", sound.getKey().getKey(),
                "count", String.valueOf(count),
                "range", formatNumber(range),
                "world", world.getName()
        ));
        return true;
    }

    private Sound parseSound(String input) {
        for (Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(input)) {
                return sound;
            }

            if (sound.getKey().getKey().equalsIgnoreCase(input)) {
                return sound;
            }

            if (sound.name().replace("_", "").equalsIgnoreCase(input.replace("_", "").replace(".", ""))) {
                return sound;
            }
        }
        return null;
    }

    private String formatNumber(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("10");
            completions.add("20");
            completions.add("50");
            return filter(completions, args[0]);
        }

        if (args.length == 2) {
            Set<String> soundNames = new LinkedHashSet<>();
            for (Sound sound : Sound.values()) {
                soundNames.add(sound.getKey().getKey());
            }
            return filter(new ArrayList<>(soundNames), args[1]);
        }

        if (sender instanceof Player && args.length >= 3 && args.length <= 5) {
            Player player = (Player) sender;
            Location location = player.getLocation();
            String suggestion;
            if (args.length == 3) {
                suggestion = formatNumber(location.getX());
            } else if (args.length == 4) {
                suggestion = formatNumber(location.getY());
            } else {
                suggestion = formatNumber(location.getZ());
            }
            completions.add(suggestion);
            return filter(completions, args[args.length - 1]);
        }

        if (args.length == 6) {
            List<String> worlds = new ArrayList<>();
            for (World world : Bukkit.getWorlds()) {
                worlds.add(world.getName());
            }
            return filter(worlds, args[5]);
        }

        return completions;
    }

    private List<String> filter(List<String> values, String input) {
        List<String> filtered = new ArrayList<>();
        String lowerInput = input.toLowerCase();
        for (String value : values) {
            if (value.toLowerCase().startsWith(lowerInput)) {
                filtered.add(value);
            }
        }
        return filtered;
    }
}
