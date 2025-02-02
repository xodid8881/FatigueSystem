package org.hwabeag.fatiguesystem.database.user;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.fatiguesystem.config.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

public class UpdateUser {
    private Connection connection;
    Statement statement = null;
    FileConfiguration Config = ConfigManager.getConfig("setting");

    public Connection Open_Connection_Player() {
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

    public void UserUpdate(Player player, int point) {
        UUID player_UUID = player.getUniqueId();
        try (Connection conn = this.Open_Connection_Player()) {
            String sql = "UPDATE fatiguesystem_user SET player_point=" + point + " WHERE player_uuid='" + player_UUID + "'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}