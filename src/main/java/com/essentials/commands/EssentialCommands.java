package com.essentials.commands;

import org.bukkit.plugin.java.JavaPlugin;

public final class EssentialCommands extends JavaPlugin {

    private static EssentialCommands instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("EssentialCommands plugin enabled!");
        registerCommands();
    }

    @Override
    public void onDisable() {
        getLogger().info("EssentialCommands plugin disabled!");
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

    public static EssentialCommands getInstance() {
        return instance;
    }
}
