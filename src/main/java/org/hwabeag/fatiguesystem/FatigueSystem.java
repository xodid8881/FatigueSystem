package org.hwabeag.fatiguesystem;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hwabeag.fatiguesystem.commands.FatigueCommand;
import org.hwabeag.fatiguesystem.commands.FatigueSettingCommand;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.item.DeleteItem;
import org.hwabeag.fatiguesystem.database.user.UpdateUser;
import org.hwabeag.fatiguesystem.events.*;
import org.hwabeag.fatiguesystem.expansions.FatigueSystemExpansion;

import java.util.Objects;

public final class FatigueSystem extends JavaPlugin {

    private static ConfigManager configManager;

    public static FatigueSystem getPlugin() {
        return JavaPlugin.getPlugin(FatigueSystem.class);
    }

    public static void getConfigManager() {
        if (configManager == null)
            configManager = new ConfigManager();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new BreakEvent(), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);
        getServer().getPluginManager().registerEvents(new FellingEvent(), this);
        getServer().getPluginManager().registerEvents(new FishingEvent(), this);
        getServer().getPluginManager().registerEvents(new HuntEvent(), this);
        getServer().getPluginManager().registerEvents(new InteractEvent(), this);
        getServer().getPluginManager().registerEvents(new InvClickEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getServer().getPluginCommand("피로도")).setExecutor(new FatigueCommand());
        Objects.requireNonNull(getServer().getPluginCommand("피로도설정")).setExecutor(new FatigueSettingCommand());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("[FatigueSystem] Enable");
        this.saveDefaultConfig();
        getConfigManager();
        registerCommands();
        registerEvents();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FatigueSystemExpansion(this).register();
        }
        if (Objects.equals(ConfigManager.getConfig("setting").getString("database.type"), "mysql")) {
            new UpdateUser().Open_Connection_Player();
            new DeleteItem().Open_Connection_Item();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("[FatigueSystem] Disable");
        ConfigManager.saveConfigs();
    }
}
