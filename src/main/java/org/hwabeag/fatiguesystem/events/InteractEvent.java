package org.hwabeag.fatiguesystem.events;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.user.UpdateUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class InteractEvent implements Listener {

    SelectUser User_Select = new SelectUser();
    UpdateUser Update_User = new UpdateUser();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (!event.isCancelled()) {
            if (Config.getBoolean("check-event.farm")) {
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getClickedBlock() != null) {
                        Block block = event.getClickedBlock();
                        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
                        if (customBlock != null) {
                            ItemStack CustomCrop = ItemsAdder.getCustomBlock(block);
                            @NotNull String CustomCrop_DisplayName = CustomCrop.getItemMeta().getDisplayName();
                            for (String type : Objects.requireNonNull(Config.getConfigurationSection("Custom-Crop-Interact")).getKeys(false)) {
                                if (CustomCrop_DisplayName.contains(type)) {
                                    @Nullable int CustomCrop_Point = Config.getInt("Custom-Crop-Interact-Point." + type);
                                    if (Objects.equals(Config.getString("database.type"), "mysql")) {
                                        int point = 0;
                                        if (User_Select.UserSelect(player) == 0) {
                                            point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                                        } else {
                                            player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                                            return;
                                        }
                                        if (point < 2000) {
                                            Update_User.UserUpdate(player, point + CustomCrop_Point);
                                            player.sendActionBar(Prefix + " 피로도 " + CustomCrop_Point + "+");
                                        } else {
                                            event.setCancelled(true);
                                            player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                                        }
                                    } else {
                                        if (PlayerConfig.getInt("피로도." + name) < 2000) {
                                            PlayerConfig.set("피로도." + name, PlayerConfig.getInt("피로도." + name) + CustomCrop_Point);
                                            player.sendActionBar(Prefix + " 피로도 " + CustomCrop_Point + "+");
                                            ConfigManager.saveConfigs();
                                        } else {
                                            event.setCancelled(true);
                                            player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
