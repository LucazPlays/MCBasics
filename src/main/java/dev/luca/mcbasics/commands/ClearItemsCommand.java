package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ClearItemsCommand implements CommandExecutor, TabCompleter {

    private static final int DEFAULT_RADIUS = 30;
    private static final int MAX_RADIUS = 200;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Permission.CLEARITEMS)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.getComponent("general.must_be_player", "<gradient:#ff6b6b:#ee5a24>✖ This command can only be used by players!</gradient>"));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(Message.getComponent("clearitems.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /clearitems [radius]</gradient>"));
            return true;
        }

        int radius = DEFAULT_RADIUS;
        if (args.length == 1) {
            try {
                radius = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Message.getComponent("clearitems.invalid_radius", "<gradient:#ff6b6b:#ee5a24>✖ Radius must be a number!</gradient>"));
                return true;
            }

            if (radius < 1 || radius > MAX_RADIUS) {
                sender.sendMessage(Message.getComponent("clearitems.radius_range", "<gradient:#ff6b6b:#ee5a24>✖ Radius must be between 1 and 200!</gradient>"));
                return true;
            }
        }

        Player player = (Player) sender;
        Location center = player.getLocation();
        int removed = 0;

        for (Entity entity : player.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(entity instanceof Item) && !(entity instanceof ExperienceOrb)) {
                continue;
            }

            if (entity.getLocation().distanceSquared(center) <= (double) radius * radius) {
                entity.remove();
                removed++;
            }
        }

        sender.sendMessage(Message.getComponent(
                "clearitems.success",
                "<gradient:#48dbfb:#1dd1a1>✦ Cleared %count% dropped item(s) and XP orb(s) in a radius of %radius% blocks!</gradient>",
                "count", String.valueOf(removed),
                "radius", String.valueOf(radius)
        ));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("30");
            completions.add("50");
        }
        return completions;
    }
}
