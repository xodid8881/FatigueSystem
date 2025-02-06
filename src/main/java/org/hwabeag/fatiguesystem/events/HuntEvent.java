package org.hwabeag.fatiguesystem.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.user.UpdateUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class HuntEvent implements Listener {

    SelectUser User_Select = new SelectUser();
    UpdateUser Update_User = new UpdateUser();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!event.isCancelled()) {
            if (Config.getBoolean("check-event.hunt")) {
                String name = damager.getName();
                if (Objects.equals(Config.getString("database.type"), "mysql")) {
                    int point = 0;
                    if (User_Select.UserSelect(damager) == 0) {
                        point = Select_User_List.get(damager.getUniqueId().toString()).getPlayerPoint();
                    } else {
                        damager.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                        return;
                    }
                    if (point > 2000) {
                        event.setCancelled(true);
                        damager.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                    }
                } else {
                    if (PlayerConfig.getInt("피로도." + name) > 2000) {
                        event.setCancelled(true);
                        damager.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (Config.getBoolean("check-event.hunt")) {
            if (event.getEntity().getKiller() != null) {
                Player player = event.getEntity().getKiller();
                String name = player.getName();
                @Nullable int Hunt_Point = Config.getInt("Hunt-Point");
                if (Objects.equals(Config.getString("database.type"), "mysql")) {
                    int point = 0;
                    if (User_Select.UserSelect(player) == 0) {
                        point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                    } else {
                        player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                        return;
                    }
                    if (point < 2000) {
                        Update_User.UserUpdate(player, point + Hunt_Point);
                        player.sendActionBar(Prefix + " 피로도 " + Hunt_Point + "+");
                    } else {
                        event.setCancelled(true);
                        player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                    }
                } else {
                    if (PlayerConfig.getInt("피로도." + name) < 2000) {
                        PlayerConfig.set("피로도." + name, PlayerConfig.getInt("피로도." + name) + Hunt_Point);
                        ConfigManager.saveConfigs();
                        player.sendActionBar(Prefix + " 피로도 " + Hunt_Point + "+");
                    } else {
                        event.setCancelled(true);
                        player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                    }
                }
            }
        }
    }
}
