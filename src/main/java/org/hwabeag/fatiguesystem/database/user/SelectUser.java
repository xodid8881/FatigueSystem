package org.hwabeag.fatiguesystem.database.user;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.fatiguesystem.commands.FatigueCommand;
import org.hwabeag.fatiguesystem.commands.FatigueSettingCommand;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.hwabeag.fatiguesystem.events.*;
import org.hwabeag.fatiguesystem.expansions.FatigueSystemExpansion;

import java.sql.*;
import java.util.UUID;

public class SelectUser {
    private Connection connection;
    Statement statement = null;

    FileConfiguration Config = ConfigManager.getConfig("setting");

    public Connection Open_Connection_User() {
        try {
            if (connection != null && !connection.isClosed()) {
                return null;
            }
            synchronized (this) {
                if (connection != null && !connection.isClosed()) {
                    return null;
                }
                Class.forName("com.mysql.jdbc.Driver");
                String host = Config.getString("database.mysql.host");
                int port = Config.getInt("database.mysql.port");
                String database = Config.getString("database.mysql.database");
                String username = Config.getString("database.mysql.user");
                String password = Config.getString("database.mysql.password");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
                statement = connection.createStatement();
                String createStr = "CREATE TABLE IF NOT EXISTS fatiguesystem_user(player_uuid varchar(50) not null, player_point varchar(50) not null)";
                statement.executeUpdate(createStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return connection;
    }

    public int UserSelect(Player player) {
        UUID player_UUID = player.getUniqueId();
        fatiguesystem_user user = new fatiguesystem_user();
        Connection conn = null;
        try {
            conn = this.Open_Connection_User();
            String sql = "SELECT player_uuid, player_point " +
                    "FROM fatiguesystem_user " +
                    "WHERE player_uuid=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, player_UUID.toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user.setPlayerUuid(rs.getString("player_uuid"));
                user.setPlayerPoint(rs.getInt("player_point"));
                ClickEvent.Select_User_List.put(rs.getString("player_uuid"), user);
                BreakEvent.Select_User_List.put(rs.getString("player_uuid"), user);
                FellingEvent.Select_User_List.put(rs.getString("player_uuid"), user);
                FishingEvent.Select_User_List.put(rs.getString("player_uuid"), user);
                HuntEvent.Select_User_List.put(rs.getString("player_uuid"), user);
                InteractEvent.Select_User_List.put(rs.getString("player_uuid"), user);
                FatigueCommand.Select_User_List.put(rs.getString("player_uuid"), user);
                FatigueSettingCommand.Select_User_List.put(rs.getString("player_uuid"), user);
                FatigueSystemExpansion.Select_User_List.put(rs.getString("player_uuid"), user);
                rs.close();
                pstmt.close();
                return 0;
            } else {
                rs.close();
                pstmt.close();
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 2;
    }
}
