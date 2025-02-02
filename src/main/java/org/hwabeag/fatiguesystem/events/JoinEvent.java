package org.hwabeag.fatiguesystem.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.InsertUser;

import java.util.Objects;

public class JoinEvent implements Listener {

    InsertUser Insert_User = new InsertUser();
    FileConfiguration Config = ConfigManager.getConfig("setting");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (Objects.equals(Config.getString("database.type"), "mysql")) {
            Insert_User.UserInsert(player);
        } else {
            if (PlayerConfig.getString("피로도." + name) == null) {
                PlayerConfig.addDefault("피로도." + name, "");
                PlayerConfig.set("피로도." + name, 0);
                ConfigManager.saveConfigs();
            }
        }
    }
}
