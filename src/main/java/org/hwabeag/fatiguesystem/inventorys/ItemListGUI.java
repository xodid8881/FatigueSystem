package org.hwabeag.fatiguesystem.inventorys;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.item.AllSelectItem;
import org.hwabeag.fatiguesystem.database.item.SelectItem;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_item;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class ItemListGUI implements Listener {
    private final Inventory inv;
    SelectItem Select_Item = new SelectItem();
    AllSelectItem AllSelect_Item = new AllSelectItem();
    public static HashMap<String, fatiguesystem_item> Select_Item_List = new HashMap<String, fatiguesystem_item>();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    FileConfiguration ItemConfig = ConfigManager.getConfig("item");

    private void initItemSetting() throws IOException {
        if (Objects.equals(Config.getString("database.type"), "mysql")) {
            String[] index = AllSelect_Item.ItemAllSelect();
            int length = index.length;
            for (int i = 0; i < length; ) {
                if (index[i] != null) {
                    @NotNull ItemStack item = null;
                    if (Select_Item.ItemSelect(index[i]) == 0) {
                        String data = Select_Item_List.get(index[i]).getItemData();
                        item = DecodeItem(data).clone();
                    }
                    inv.setItem(i, item);
                }
                i++;
            }
        } else {
            int i = 0;
            for (String key : Objects.requireNonNull(ItemConfig.getConfigurationSection("list")).getKeys(false)) {
                @Nullable ItemStack item = ItemConfig.getItemStack("list." + key);
                inv.setItem(i, item);
                i++;
            }
        }
    }

    public ItemListGUI() throws IOException {
        inv = Bukkit.createInventory(null, 54, "피로도 치료아이템");
        initItemSetting();
    }

    public void open(Player player) {
        player.openInventory(inv);
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