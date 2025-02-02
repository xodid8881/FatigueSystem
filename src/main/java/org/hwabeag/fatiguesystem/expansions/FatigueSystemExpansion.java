package org.hwabeag.fatiguesystem.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.fatiguesystem.FatigueSystem;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class FatigueSystemExpansion extends PlaceholderExpansion {

    private final FatigueSystem plugin; //

    public FatigueSystemExpansion(FatigueSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "fatigue";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();
    SelectUser User_Select = new SelectUser();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (Objects.equals(Config.getString("database.type"), "mysql")) {
            if (User_Select.UserSelect((Player) player) == 0) {
                return String.valueOf(Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint());
            } else {
                return "0";
            }
        } else {
            return String.valueOf(PlayerConfig.getInt("피로도." + player.getName()));
        }
    }
}