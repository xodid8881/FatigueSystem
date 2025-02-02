package org.hwabeag.fatiguesystem.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.item.SelectItem;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.user.UpdateUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_item;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class ClickEvent implements Listener {

    SelectUser User_Select = new SelectUser();
    SelectItem Select_Item = new SelectItem();
    UpdateUser Update_User = new UpdateUser();
    public static HashMap<String, fatiguesystem_item> Select_Item_List = new HashMap<String, fatiguesystem_item>();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    FileConfiguration ItemConfig = ConfigManager.getConfig("item");

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) throws IOException {
        Player player = event.getPlayer();
        String name = player.getName();
        if (player.getItemInHand().getItemMeta() == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            String itemname = player.getItemInHand().getItemMeta().getDisplayName();
            if (player.getItemInHand().getType() == Material.valueOf(Config.getString("item-material"))) {
                if (itemname.equals(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("item-display-name"))))) {
                    if (Objects.equals(Config.getString("database.type"), "mysql")) {
                        int point = 0;
                        if (User_Select.UserSelect(player) == 0) {
                            point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                        } else {
                            player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                            return;
                        }
                        if (point == 0) {
                            player.sendMessage(Prefix + " 피로도가 존재하지 않아 사용되지 않았습니다.");
                            return;
                        }
                        Update_User.UserUpdate(player, 0);
                    } else {
                        PlayerConfig.set("피로도." + name, 0);
                        ConfigManager.saveConfigs();
                    }
                    player.sendMessage(Prefix + " 피로도 회복 아이템을 사용했습니다.");
                    ItemStack item = player.getInventory().getItemInMainHand();
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
            if (!Objects.equals(Config.getString("database.type"), "mysql")) {
                if (ItemConfig.getItemStack("list." + itemname) != null) {
                    @NotNull ItemStack cloneitem = ItemConfig.getItemStack("list." + itemname).clone();
                    if (player.getInventory().containsAtLeast(cloneitem, 1)) {
                        int point = PlayerConfig.getInt("피로도." + name) - ItemConfig.getInt("point." + itemname);
                        if (point > 0) {
                            PlayerConfig.set("피로도." + name, point);
                        } else if (point < 0) {
                            PlayerConfig.set("피로도." + name, 0);
                        } else if (PlayerConfig.getInt("피로도." + name) == 0) {
                            player.sendMessage(Prefix + " 피로도가 존재하지 않아 사용되지 않았습니다.");
                            return;
                        }
                        ConfigManager.saveConfigs();
                        player.sendMessage(Prefix + " 피로도 회복 아이템을 사용했습니다.");
                        cloneitem.setAmount(1);
                        player.getInventory().removeItem(cloneitem);
                        event.setCancelled(true);
                    }
                }
            } else {
                if (Select_Item.ItemSelect(itemname) == 0) {
                    int item_point = Select_Item_List.get(itemname).getItemPoint();
                    String data = Select_Item_List.get(itemname).getItemData();
                    @NotNull ItemStack item = DecodeItem(data).clone();
                    if (player.getInventory().containsAtLeast(item, 1)) {
                        int point = 0;
                        if (User_Select.UserSelect(player) == 0) {
                            point = point + Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                        } else {
                            player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                            return;
                        }
                        if (point > 0) {
                            if (point - item_point < 0) {
                                Update_User.UserUpdate(player, 0);
                            } else {
                                point = point - item_point;
                                Update_User.UserUpdate(player, point);
                            }
                        } else {
                            Update_User.UserUpdate(player, 0);
                            player.sendMessage(Prefix + " 피로도가 존재하지 않아 사용되지 않았습니다.");
                            return;
                        }
                        player.sendMessage(Prefix + " 피로도 회복 아이템을 사용했습니다.");
                        item.setAmount(1);
                        player.getInventory().removeItem(item);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    public static ItemStack DecodeItem(String EncodedItem) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(EncodedItem.replace("%space%", "\n")));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack itemStack = (ItemStack) dataInput.readObject();
            dataInput.close();
            return itemStack;
        } catch (ClassNotFoundException e) {
            throw new IOException("ERR", e);
        }
    }
}
