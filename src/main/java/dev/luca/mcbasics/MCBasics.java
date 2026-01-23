package dev.luca.mcbasics;

import dev.luca.mcbasics.api.Message;
import dev.luca.mcbasics.commands.*;
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

        getLogger().info("MCBasics plugin enabled!");
        getLogger().info("Loaded " + dev.luca.minecraftapi.MessageAPI.getLoadedMessageCount() + " messages");
        registerCommands();
    }

    @Override
    public void onDisable() {
        dev.luca.minecraftapi.MessageAPI.disableAutoReload();
        getLogger().info("MCBasics plugin disabled!");
    }

    private void registerCommands() {
        MainTabCompleter tabCompleter = new MainTabCompleter();

        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("vanish").setTabCompleter(tabCompleter);

        getCommand("gm").setExecutor(new GamemodeCommand());
        getCommand("gm").setTabCompleter(tabCompleter);
        getCommand("gmc").setExecutor(new GamemodeCommand());
        getCommand("gmc").setTabCompleter(tabCompleter);
        getCommand("gms").setExecutor(new GamemodeCommand());
        getCommand("gms").setTabCompleter(tabCompleter);
        getCommand("gma").setExecutor(new GamemodeCommand());
        getCommand("gma").setTabCompleter(tabCompleter);
        getCommand("gmsp").setExecutor(new GamemodeCommand());
        getCommand("gmsp").setTabCompleter(tabCompleter);

        getCommand("invsee").setExecutor(new InvseeCommand());
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
    }

    public static MCBasics getInstance() {
        return instance;
    }
}
