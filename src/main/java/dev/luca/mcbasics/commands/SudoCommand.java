package dev.luca.mcbasics.commands;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.api.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SudoCommand implements CommandExecutor {

    private static final String SUDO_PERMISSION = "mcbasics.sudo";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(SUDO_PERMISSION)) {
            sender.sendMessage(Message.getComponent("general.no_permission", "<gradient:#ff6b6b:#ee5a24>✖ You don't have permission!</gradient>"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Message.getComponent("sudo.usage", "<gradient:#ff6b6b:#ee5a24>✖ Usage: /sudo <player> <command [args]></gradient>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Message.getComponent("general.player_not_found", "<gradient:#ff6b6b:#ee5a24>✖ Player not found!</gradient>"));
            return true;
        }

        if (target.equals(sender)) {
            sender.sendMessage(Message.getComponent("sudo.cant_sudo_self", "<gradient:#ff6b6b:#ee5a24>✖ You can't sudo yourself!</gradient>"));
            return true;
        }

        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        String commandToRun = commandBuilder.toString().trim();

        if (commandToRun.startsWith("/")) {
            commandToRun = commandToRun.substring(1);
        }

        String commandName = commandToRun.split(" ")[0];

        try {
            SimpleCommandMap commandMap = getCommandMap();
            if (commandMap != null) {
                org.bukkit.command.Command bukkitCommand = commandMap.getCommand(commandName);
                if (bukkitCommand != null) {
                    String[] commandArgs = commandToRun.contains(" ") 
                        ? commandToRun.substring(commandName.length()).trim().split(" ") 
                        : new String[0];

                    bukkitCommand.execute(target, commandName, commandArgs);
                    sender.sendMessage(Message.getComponent("sudo.success", "<gradient:#48dbfb:#1dd1a1>✦ %target% executed: /%command%</gradient>", "target", target.getName(), "command", commandToRun));
                } else {
                    sender.sendMessage(Message.getComponent("sudo.unknown_command", "<gradient:#ff6b6b:#ee5a24>✖ Unknown command: %command%</gradient>", "command", commandName));
                }
            } else {
                sender.sendMessage(Message.getComponent("sudo.failed", "<gradient:#ff6b6b:#ee5a24>✖ Failed to execute command!</gradient>"));
            }
        } catch (Exception e) {
            sender.sendMessage(Message.getComponent("sudo.failed", "<gradient:#ff6b6b:#ee5a24>✖ Failed to execute command!</gradient>"));
        }

        return true;
    }

    private SimpleCommandMap getCommandMap() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            return (SimpleCommandMap) commandMapField.get(Bukkit.getServer());
        } catch (Exception e) {
            return null;
        }
    }
}
