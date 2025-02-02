package org.hwabeag.fatiguesystem.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.fatiguesystem.FatigueSystem;

import java.util.HashMap;

public class ConfigManager {
    private static final FatigueSystem plugin = FatigueSystem.getPlugin();

    private static final HashMap<String, ConfigMaker> configSet = new HashMap<>();

    public ConfigManager() {
        String path = plugin.getDataFolder().getAbsolutePath();
        configSet.put("setting", new ConfigMaker(path, "config.yml"));
        configSet.put("player", new ConfigMaker(path, "player.yml"));
        configSet.put("item", new ConfigMaker(path, "item.yml"));
        loadSettings();
        saveConfigs();
    }

    public static void reloadConfigs() {
        for (String key : configSet.keySet()) {
            plugin.getLogger().info(key);
            configSet.get(key).reloadConfig();
        }
    }

    public static void saveConfigs() {
        for (String key : configSet.keySet())
            configSet.get(key).saveConfig();
    }

    public static FileConfiguration getConfig(String fileName) {
        return configSet.get(fileName).getConfig();
    }

    public static void loadSettings() {
        saveConfigs();
    }
}