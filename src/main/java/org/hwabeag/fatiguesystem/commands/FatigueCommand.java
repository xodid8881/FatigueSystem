package org.hwabeag.fatiguesystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FatigueCommand implements TabCompleter, @Nullable CommandExecutor {

    SelectUser User_Select = new SelectUser();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<String>();
            list.add("내정보");
            return list;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getConsoleSender().sendMessage(Prefix + " 인게임에서 사용할 수 있습니다.");
            return true;
        }
        String name = player.getName();
        if (args.length == 0) {
            player.sendMessage(Prefix + " /피로도 내정보 - 내정보를 확인합니다.");
            return true;
        }
        if (args[0].equalsIgnoreCase("내정보")) {
            if (Objects.equals(Config.getString("database.type"), "mysql")) {
                if (User_Select.UserSelect(player) == 0) {
                    int player_point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                    player.sendMessage(Prefix + " 나의 피로도 :" + player_point + "점");
                } else {
                    player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                    return true;
                }
            } else {
                player.sendMessage(Prefix + " 나의 피로도 :" + PlayerConfig.getInt("피로도." + name) + "점");
            }
            return true;
        }
        player.sendMessage(Prefix + " /피로도 내정보 - 내정보를 확인합니다.");
        return true;
    }
}