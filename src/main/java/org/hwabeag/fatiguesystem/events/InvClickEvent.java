package org.hwabeag.fatiguesystem.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.item.DeleteItem;
import org.hwabeag.fatiguesystem.database.item.SelectItem;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_item;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;

import java.util.HashMap;
import java.util.Objects;

public class InvClickEvent implements Listener {

    DeleteItem Delete_Item = new DeleteItem();
    SelectItem Select_Item = new SelectItem();
    public static HashMap<String, fatiguesystem_item> Select_Item_List = new HashMap<String, fatiguesystem_item>();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));
    FileConfiguration ItemConfig = ConfigManager.getConfig("item");

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null)
            return;
        if (e.getCurrentItem() != null) {
            Player player = (Player) e.getWhoClicked();
            if (e.getView().getTitle().equals("피로도 치료아이템")) {
                e.setCancelled(true);
                String item_name = e.getCurrentItem().getItemMeta().getDisplayName();
                if (Objects.equals(Config.getString("database.type"), "mysql")) {
                    if (Select_Item.ItemSelect(item_name) == 0) {
                        Delete_Item.DeleteItemData(item_name);
                        player.sendMessage(Prefix + " 아이템을 제거했습니다.");
                    } else {
                        player.sendMessage(Prefix + " 아이템을 제거하는 중 문제가 발생했습니다.");
                        player.sendMessage(Prefix + " 이미 삭제된 데이터 혹 알맞지 않는 데이터 값입니다.");
                    }
                } else {
                    if (ItemConfig.getItemStack("list." + item_name) != null) {
                        ItemConfig.set("list." + item_name, null);
                        ConfigManager.saveConfigs();
                        player.sendMessage(Prefix + " 해당 아이템을 제거했습니다.");
                    } else {
                        player.sendMessage(Prefix + " 해당 아이템은 존재하지 않아 제거할 수 없습니다.");
                    }
                }
                e.getInventory().clear();
                player.closeInventory();
            }
        }
    }
}
