package org.hwabeag.fatiguesystem.events;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.user.UpdateUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class BreakEvent implements Listener {

    SelectUser User_Select = new SelectUser();
    UpdateUser Update_User = new UpdateUser();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    @EventHandler
    public void onMineBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        Block block = event.getBlock();
        if (Config.getBoolean("check-event.farm")) {
            if (!event.isCancelled()) {
                if (block.getType() == Material.WHEAT || block.getType() == Material.CARROTS || block.getType() == Material.BEETROOTS || block.getType() == Material.POTATOES || block.getType() == Material.POTATOES) {
                    Ageable age = (Ageable) block.getBlockData();
                    if (age.getMaximumAge() == age.getAge()) {
                        @Nullable int Farm_Point = Config.getInt("Farm-Point");
                        if (Objects.equals(Config.getString("database.type"), "mysql")) {
                            int point = 0;
                            if (User_Select.UserSelect(player) == 0) {
                                point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                            } else {
                                player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                                return;
                            }
                            if (point < 2000) {
                                Update_User.UserUpdate(player, point + Farm_Point);
                                player.sendActionBar(Prefix + " 피로도 " + Farm_Point + "+");
                            } else {
                                event.setCancelled(true);
                                player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                            }
                        } else {
                            if (PlayerConfig.getInt("피로도." + name) < 2000) {
                                PlayerConfig.set("피로도." + name, PlayerConfig.getInt("피로도." + name) + Farm_Point);
                                ConfigManager.saveConfigs();
                                player.sendActionBar(Prefix + " 피로도 " + Farm_Point + "+");
                            } else {
                                event.setCancelled(true);
                                player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                            }
                        }
                    }
                }
                if (block.getType() == Material.MELON || block.getType() == Material.PUMPKIN) {
                    @Nullable int Farm_Point = Config.getInt("Farm-Point");
                    if (Objects.equals(Config.getString("database.type"), "mysql")) {
                        int point = 0;
                        if (User_Select.UserSelect(player) == 0) {
                            point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                        } else {
                            player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                            return;
                        }
                        if (point < 2000) {
                            Update_User.UserUpdate(player, point + Farm_Point);
                            player.sendActionBar(Prefix + "피로도가 쌓였습니다.");
                            player.sendActionBar(Prefix + " 피로도 " + Farm_Point + "+");
                        } else {
                            event.setCancelled(true);
                            player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                        }
                    } else {
                        if (PlayerConfig.getInt("피로도." + name) < 2000) {
                            PlayerConfig.set("피로도." + name, PlayerConfig.getInt("피로도." + name) + Farm_Point);
                            ConfigManager.saveConfigs();
                            player.sendActionBar(Prefix + " 피로도 " + Farm_Point + "+");
                        } else {
                            event.setCancelled(true);
                            player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                        }
                    }
                }
            }
        }

        if (Config.getBoolean("check-event.mine")) {
            if (!event.isCancelled()) {
                if (block.getType() == Material.COAL_ORE || block.getType() == Material.DEEPSLATE_COAL_ORE || block.getType() == Material.IRON_ORE || block.getType() == Material.DEEPSLATE_IRON_ORE || block.getType() == Material.GOLD_ORE || block.getType() == Material.DEEPSLATE_GOLD_ORE || block.getType() == Material.COPPER_ORE || block.getType() == Material.DEEPSLATE_COPPER_ORE || block.getType() == Material.REDSTONE_ORE || block.getType() == Material.DEEPSLATE_REDSTONE_ORE || block.getType() == Material.DIAMOND_ORE || block.getType() == Material.DEEPSLATE_DIAMOND_ORE || block.getType() == Material.EMERALD_ORE || block.getType() == Material.DEEPSLATE_EMERALD_ORE || block.getType() == Material.LAPIS_ORE || block.getType() == Material.DEEPSLATE_LAPIS_ORE || block.getType() == Material.LAPIS_ORE || block.getType() == Material.DEEPSLATE_LAPIS_ORE) {
                    @Nullable int Mining_Point = Config.getInt("Mining-Point");
                    if (Objects.equals(Config.getString("database.type"), "mysql")) {
                        int point = 0;
                        if (User_Select.UserSelect(player) == 0) {
                            point = Select_User_List.get(player.getUniqueId().toString()).getPlayerPoint();
                        } else {
                            player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                            return;
                        }
                        if (point < 2000) {
                            Update_User.UserUpdate(player, point + Mining_Point);
                            player.sendActionBar(Prefix + " 피로도 " + Mining_Point + "+");
                        } else {
                            event.setCancelled(true);
                            player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                        }
                    } else {
                        if (PlayerConfig.getInt("피로도." + name) < 2000) {
                            PlayerConfig.set("피로도." + name, PlayerConfig.getInt("피로도." + name) + Mining_Point);
                            ConfigManager.saveConfigs();
                            player.sendActionBar(Prefix + " 피로도 " + Mining_Point + "+");
                        } else {
                            event.setCancelled(true);
                            player.sendActionBar(Prefix + " 피로도로 인해 작업할 수 없습니다.");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCustomBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        Block block = event.getBlock();
        if (Config.getBoolean("check-event.farm")) {
            if (!event.isCancelled()) {
                boolean isCustomBlock = ItemsAdder.isCustomBlock(block);
                if (isCustomBlock) {
                    ItemStack CustomCrop = ItemsAdder.getCustomBlock(block);
                    @NotNull String CustomCrop_DisplayName = CustomCrop.getItemMeta().getDisplayName();
                    for (String type : Objects.requireNonNull(Config.getConfigurationSection("Custom-Crop-Break")).getKeys(false)) {
                        if (CustomCrop_DisplayName.contains(type)) {
                            @Nullable int CustomCrop_Point = Config.getInt("Custom-Crop-Break-Point." + type);
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
                                    ConfigManager.saveConfigs();
                                    player.sendActionBar(Prefix + " 피로도 " + CustomCrop_Point + "+");
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
