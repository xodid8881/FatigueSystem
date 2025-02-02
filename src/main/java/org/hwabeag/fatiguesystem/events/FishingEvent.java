package org.hwabeag.fatiguesystem.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.user.UpdateUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class FishingEvent implements Listener {

    SelectUser User_Select = new SelectUser();
    UpdateUser Update_User = new UpdateUser();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        String state = event.getState().name();
        if (Config.getBoolean("check-event.fishing")) {
            if (state.equals("CAUGHT_FISH")) {
                @Nullable int Fishing_Point = Config.getInt("Fishing-Point");
                if (Objects.equals(Config.getString("database.type"), "mysql")) {
                    int point = 0;
                    if (User_Select.UserSelect(player) == 0) {
                        point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                    } else {
                        player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                        return;
                    }
                    if (point < 2000) {
                        Update_User.UserUpdate(player, point + Fishing_Point);
                        player.sendActionBar(Prefix + " 피로도 " + Fishing_Point + "+");
                    } else {
                        event.setCancelled(true);
                        player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                    }
                } else {
                    if (PlayerConfig.getInt("피로도." + name) < 2000) {
                        PlayerConfig.set("피로도." + name, PlayerConfig.getInt("피로도." + name) + Fishing_Point);
                        ConfigManager.saveConfigs();
                        player.sendActionBar(Prefix + " 피로도 " + Fishing_Point + "+");
                    } else {
                        event.setCancelled(true);
                        player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                    }
                }
            }
        }
    }
}
