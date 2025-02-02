package org.hwabeag.fatiguesystem.database.item;

import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.fatiguesystem.config.ConfigManager;

import java.sql.*;

public class InsertItem {
    private Connection connection;
    Statement statement = null;

    FileConfiguration Config = ConfigManager.getConfig("setting");

    public Connection Open_Connection_Item() {
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
                String createStr = "CREATE TABLE IF NOT EXISTS fatiguesystem_item(item_name varchar(100) not null, item_data varchar(5000), item_point varchar(100) not null)";
                statement.executeUpdate(createStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return connection;
    }

    public void ItemInsert(String name, String item, int point) {
        Connection conn = null;
        try {
            conn = this.Open_Connection_Item();
            String sql = "INSERT INTO fatiguesystem_item (item_name, item_data, item_point) VALUES (?, ?, ?)";
            PreparedStatement prepared_statement = conn.prepareStatement(sql);
            prepared_statement.setString(1, name);
            prepared_statement.setString(2, item);
            prepared_statement.setInt(3, point);
            int rows = prepared_statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
