package dev.luca.mcbasics;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.commands.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import top.insights.deployment.MinecraftSelfUpdater;
import org.bukkit.plugin.java.JavaPlugin;

public final class MCBasics extends JavaPlugin {

    private static MCBasics instance;

    private static final String PROJECT_UUID = "220e4f0e-8163-4d91-b7bc-7ea1c6ab4264";
    private static final String PROJECT_KEY = "xTesBisS8Tc7luCqdOWGs88dcDoetuIw";

    @Override
    public void onEnable() {
        instance = this;
        Message.init(this);

        MinecraftSelfUpdater.start(this, PROJECT_UUID, PROJECT_KEY, true);

        registerCommands();

        getLogger().info("MCBasics plugin enabled!");
        getLogger().info("Loaded " + dev.luca.minecraftapi.MessageAPI.getLoadedMessageCount() + " messages");

        broadcastStartup();
    }

    private void broadcastStartup() {
        Component mcbasics = Component.text()
                .append(Component.text("M", TextColor.fromHexString("#ff6b6b")))
                .append(Component.text("C", TextColor.fromHexString("#feca57")))
                .append(Component.text("B", TextColor.fromHexString("#48dbfb")))
                .append(Component.text("a", TextColor.fromHexString("#ff9ff3")))
                .append(Component.text("s", TextColor.fromHexString("#54a0ff")))
                .append(Component.text("i", TextColor.fromHexString("#5f27cd")))
                .append(Component.text("c", TextColor.fromHexString("#ff6b6b")))
                .append(Component.text("s", TextColor.fromHexString("#feca57")))
                .build();

        Component fullMessage = Component.text()
                .append(Component.text("╔═══════════════════════════════════════════════════╗\n", NamedTextColor.GOLD))
                .append(Component.text("║  ", NamedTextColor.GOLD))
                .append(mcbasics)
                .append(Component.text(" has been loaded!", NamedTextColor.GREEN))
                .append(Component.text("  ║\n", NamedTextColor.GOLD))
                .append(Component.text("║  Version: ", NamedTextColor.GOLD))
                .append(Component.text("1.0.0", NamedTextColor.YELLOW))
                .append(Component.text("  •  ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Type ", NamedTextColor.GRAY))
                .append(Component.text("/help", NamedTextColor.GOLD))
                .append(Component.text(" for commands", NamedTextColor.GRAY))
                .append(Component.text("  ║\n", NamedTextColor.GOLD))
                .append(Component.text("╚═══════════════════════════════════════════════════╝", NamedTextColor.GOLD))
                .build();

        Bukkit.broadcast(fullMessage);
    }

    @Override
    public void onDisable() {
        dev.luca.minecraftapi.MessageAPI.disableAutoReload();
        getLogger().info("MCBasics plugin disabled!");
    }

    private void registerCommands() {
        MainTabCompleter tabCompleter = new MainTabCompleter();
        GamemodeCommand gamemodeCmd = new GamemodeCommand();

        getCommand("help").setExecutor(new HelpCommand());
        getCommand("help").setTabCompleter(new HelpCommand());

        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("vanish").setTabCompleter(tabCompleter);

        getCommand("gamemode").setExecutor(gamemodeCmd);
        getCommand("gamemode").setTabCompleter(tabCompleter);

        getCommand("invsee").setExecutor(new InventoryCommand());
        getCommand("invsee").setTabCompleter(tabCompleter);

        getCommand("tphere").setExecutor(new TphereCommand());
        getCommand("tphere").setTabCompleter(tabCompleter);

        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("feed").setTabCompleter(tabCompleter);

        getCommand("heal").setExecutor(new HealCommand());
        getCommand("heal").setTabCompleter(tabCompleter);

        getCommand("speed").setExecutor(new SpeedCommand());
        getCommand("speed").setTabCompleter(tabCompleter);

        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("fly").setTabCompleter(tabCompleter);

        getCommand("unsafeenchant").setExecutor(new UnsafeEnchantCommand());
        getCommand("unsafeenchant").setTabCompleter(tabCompleter);

        getCommand("enchant").setExecutor(new EnchantCommand());
        getCommand("enchant").setTabCompleter(new EnchantCommand());

        getCommand("i").setExecutor(new ItemCommand());
        getCommand("i").setTabCompleter(new ItemCommand());

        getCommand("enderchest").setExecutor(new EnderchestCommand());
        getCommand("enderchest").setTabCompleter(new EnderchestCommand());
        getCommand("ec").setExecutor(new EnderchestCommand());
        getCommand("ec").setTabCompleter(new EnderchestCommand());

        getCommand("setdisplayname").setExecutor(new SetDisplayNameCommand());
        getCommand("setdisplayname").setTabCompleter(tabCompleter);

        getCommand("setlore").setExecutor(new SetLoreCommand());
        getCommand("setlore").setTabCompleter(tabCompleter);

        getCommand("sudo").setExecutor(new SudoCommand());
        getCommand("sudo").setTabCompleter(tabCompleter);

        getCommand("ping").setExecutor(new PingCommand());
        getCommand("ping").setTabCompleter(tabCompleter);

        getCommand("god").setExecutor(new GodCommand());

        getCommand("craft").setExecutor(new CraftCommand());
        getCommand("anvil").setExecutor(new AnvilCommand());
    }

    public static MCBasics getInstance() {
        return instance;
    }
}
