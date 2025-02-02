package org.hwabeag.fatiguesystem.database.item;

import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.fatiguesystem.commands.FatigueSettingCommand;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_item;
import org.hwabeag.fatiguesystem.events.ClickEvent;
import org.hwabeag.fatiguesystem.events.InvClickEvent;
import org.hwabeag.fatiguesystem.inventorys.ItemListGUI;

import java.sql.*;

public class SelectItem {
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

    public int ItemSelect(String name) {
        fatiguesystem_item item = new fatiguesystem_item();
        Connection conn = null;
        try {
            conn = this.Open_Connection_Item();
            String sql = "SELECT item_name, item_data, item_point " +
                    "FROM fatiguesystem_item " +
                    "WHERE item_name=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                item.setItemName(rs.getString("item_name"));
                item.setItemData(rs.getString("item_data"));
                item.setItemPoint(rs.getInt("item_point"));
                FatigueSettingCommand.Select_Item_List.put(rs.getString("item_name"), item);
                ClickEvent.Select_Item_List.put(rs.getString("item_name"), item);
                InvClickEvent.Select_Item_List.put(rs.getString("item_name"), item);
                ItemListGUI.Select_Item_List.put(rs.getString("item_name"), item);
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
