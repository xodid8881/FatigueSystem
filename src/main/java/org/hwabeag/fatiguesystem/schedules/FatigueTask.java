package org.hwabeag.fatiguesystem.schedules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.ResetUser;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class FatigueTask implements Runnable {

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));

    ResetUser User_Reset = new ResetUser();

    @Override
    public void run() {
        // 현재 시간
        LocalDateTime currentDateTime = LocalDateTime.now();
        // 오늘의 00시를 구함
        if (Config.get("ToDay_Midnight.Time") == null) {
            Config.set("ToDay_Midnight.Time", currentDateTime.toLocalDate().atStartOfDay().toString());
            ConfigManager.saveConfigs();
        }
        LocalDateTime midnight = LocalDateTime.parse(Objects.requireNonNull(Config.getString("ToDay_Midnight.Time")));
        // 하루가 지났는지 확인
        boolean isOneDayPassed = hasOneDayPassed(midnight, currentDateTime);
        if (isOneDayPassed) {
            User_Reset.UserReset();
            Bukkit.broadcastMessage(Prefix + " 하루가 지나 모든 피로도가 회복되었습니다.");
            Config.set("ToDay_Midnight.Time", currentDateTime.toLocalDate().atStartOfDay().toString());
            ConfigManager.saveConfigs();
        }
    }

    public static boolean hasOneDayPassed(LocalDateTime midnight, LocalDateTime currentDateTime) {
        long daysBetween = ChronoUnit.DAYS.between(midnight, currentDateTime);
        return daysBetween >= 1;
    }
}
