package org.hwabeag.fatiguesystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.hwabeag.fatiguesystem.config.ConfigManager;
import org.hwabeag.fatiguesystem.database.item.DeleteItem;
import org.hwabeag.fatiguesystem.database.item.InsertItem;
import org.hwabeag.fatiguesystem.database.item.SelectItem;
import org.hwabeag.fatiguesystem.database.user.SelectUser;
import org.hwabeag.fatiguesystem.database.user.UpdateUser;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_item;
import org.hwabeag.fatiguesystem.database.utils.fatiguesystem_user;
import org.hwabeag.fatiguesystem.inventorys.ItemListGUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FatigueSettingCommand implements TabCompleter, @Nullable CommandExecutor {

    SelectUser User_Select = new SelectUser();
    UpdateUser Update_User = new UpdateUser();
    DeleteItem Delete_Item = new DeleteItem();
    InsertItem Insert_Item = new InsertItem();
    SelectItem Select_Item = new SelectItem();
    public static HashMap<String, fatiguesystem_item> Select_Item_List = new HashMap<String, fatiguesystem_item>();
    public static HashMap<String, fatiguesystem_user> Select_User_List = new HashMap<String, fatiguesystem_user>();

    FileConfiguration Config = ConfigManager.getConfig("setting");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Config.getString("fatigue-system.prefix")));
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    FileConfiguration ItemConfig = ConfigManager.getConfig("item");

    private ItemStack getFatigueItem() {
        Material material = Material.valueOf(Config.getString("item-material"));
        ItemStack item = new ItemStack(Objects.requireNonNull(material));
        ItemMeta itemMeta = item.getItemMeta();

        @Nullable String display_name = Config.getString("item-display-name");
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(display_name)));
        ArrayList<String> lore = new ArrayList<String>(); // makes the lore
        for (String key : Config.getStringList("item-lore")) {
            lore.add(ChatColor.translateAlternateColorCodes('&', key));
        }
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<String>();
            list.add("지급");
            list.add("설정");
            list.add("회복템추가");
            list.add("회복템제거");
            list.add("회복템목록");
            return list;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("지급")) {
                List<String> list = new ArrayList<String>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
                return list;
            }
            if (args[0].equalsIgnoreCase("설정")) {
                List<String> list = new ArrayList<String>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
                return list;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp()) {
                player.sendMessage(Prefix + " 당신은 권한이 없습니다.");
                return true;
            }
            if (args.length == 0) {
                player.sendMessage(Prefix + " /피로도설정 지급 [닉네임] - 플레이어에게 피로도 아이템을 지급합니다.");
                player.sendMessage(Prefix + " /피로도설정 설정 [닉네임] [정도] - 플레이어 피로도를 설정합니다.");
                player.sendMessage(Prefix + " /피로도설정 회복템추가 [회복정도] - 손에든 아이템을 회복템으로 추가합니다.");
                player.sendMessage(Prefix + " /피로도설정 회복템제거 - 손에든 회복템을 삭제합니다.");
                player.sendMessage(Prefix + " /피로도설정 회복템제거 [이름] - 이름과 동일한 회복템을 삭제합니다.");
                player.sendMessage(Prefix + " /피로도설정 회복템목록 - 회복템 목록을 확인합니다.");
                return true;
            }
            if (args[0].equalsIgnoreCase("지급")) {
                if (args.length == 1) {
                    player.sendMessage(Prefix + " /피로도설정 지급 [닉네임] - 플레이어에게 피로도 아이템을 지급합니다.");
                    player.sendMessage(Prefix + " 유저 닉네임을 적어주세요.");
                    return true;
                }
                for (Player ignored : Bukkit.getOnlinePlayers()) {
                    if (ignored.getName().equals(args[1])) {
                        ignored.getInventory().addItem(getFatigueItem());
                        player.sendMessage(Prefix + " " + args[1] + " 님에게 피로도 아이템을 지급했습니다.");
                        ignored.sendMessage(Prefix + " 운영진에게 피로도 아이템을 지급 받았습니다.");
                        return true;
                    }
                }
                player.sendMessage(Prefix + " " + args[1] + " 닉네임을 가진 유저가 존재하지 않습니다.");
                return true;
            }
            if (args[0].equalsIgnoreCase("설정")) {
                if (args.length == 1) {
                    player.sendMessage(Prefix + " /피로도설정 설정 [닉네임] [정도] - 플레이어 피로도를 설정합니다.");
                    player.sendMessage(Prefix + " 유저 닉네임을 적어주세요.");
                    return true;
                }
                if (args.length == 2) {
                    player.sendMessage(Prefix + " /피로도설정 설정 [닉네임] [정도] - 플레이어 피로도를 설정합니다.");
                    player.sendMessage(Prefix + " 정도를 적어주세요.");
                    return true;
                }
                if (args[2].matches("[+-]?\\d*(\\.\\d+)?")) {
                    for (Player ignored : Bukkit.getOnlinePlayers()) {
                        if (ignored.getName().equals(args[1])) {
                            if (Objects.equals(Config.getString("database.type"), "mysql")) {
                                if (User_Select.UserSelect(player) == 0) {
                                    Update_User.UserUpdate(ignored, Integer.parseInt(args[2]));
                                } else {
                                    player.sendMessage(Prefix + " 당신의 데이터가 존재하지 않습니다.");
                                    return true;
                                }
                            } else {
                                PlayerConfig.set("피로도." + args[1], args[2]);
                                ConfigManager.saveConfigs();
                            }
                            player.sendMessage(Prefix + " " + args[1] + " 님의 피로도를 설정했습니다.");
                            ignored.sendMessage(Prefix + " 운영진이 당신의 피로도를 설정했습니다..");
                            return true;
                        }
                    }
                } else {
                    player.sendMessage(Prefix + " 정도는 숫자만 가능합니다.");
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("회복템추가")) {
                if (args.length == 1) {
                    player.sendMessage(Prefix + " /피로도설정 회복템추가 [회복정도] - 손에든 아이템을 회복템으로 추가합니다.");
                    player.sendMessage(Prefix + " 정도를 적어주세요.");
                    return true;
                }
                if (args[1].matches("[+-]?\\d*(\\.\\d+)?")) {
                    @NotNull ItemStack item = player.getInventory().getItemInHand();
                    Material material = item.getType();
                    if (material != Material.AIR) {
                        if (Objects.equals(Config.getString("database.type"), "mysql")) {
                            String item_name = item.getItemMeta().getDisplayName();
                            String item_data = EncodeItem(item);
                            int item_point = Integer.parseInt(args[1]);
                            if (Select_Item.ItemSelect(item_name) != 0) {
                                Insert_Item.ItemInsert(item_name, item_data, item_point);
                                player.sendMessage(Prefix + " 손에 든 아이템을 추가했습니다.");
                            } else {
                                player.sendMessage(Prefix + " 아이템 추가중 문제가 발생했습니다.");
                                player.sendMessage(Prefix + " 이미 존재하는 아이템 입니다.");
                            }
                            return true;
                        } else {
                            String item_name = item.getItemMeta().getDisplayName();
                            ItemConfig.set("list." + item_name, item);
                            ItemConfig.set("point." + item_name, args[1]);
                            ConfigManager.saveConfigs();
                            player.sendMessage(Prefix + " 아이템을 추가했습니다.");
                            return true;
                        }
                    } else {
                        player.sendMessage(Prefix + " 손에 아이템을 들고 작업해주세요.");
                        return true;
                    }
                } else {
                    player.sendMessage(Prefix + " 정도는 숫자만 가능합니다.");
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("회복템제거")) {
                if (args.length == 1) {
                    @NotNull ItemStack item = player.getInventory().getItemInHand();
                    Material material = item.getType();
                    if (material != Material.AIR) {
                        if (Objects.equals(Config.getString("database.type"), "mysql")) {
                            String item_name = item.getItemMeta().getDisplayName();
                            if (Select_Item.ItemSelect(item_name) == 0) {
                                Delete_Item.DeleteItemData(item_name);
                                player.sendMessage(Prefix + " 아이템을 제거했습니다.");
                            } else {
                                player.sendMessage(Prefix + " 아이템을 제거하는 중 문제가 발생했습니다.");
                                player.sendMessage(Prefix + " 이미 삭제된 데이터 혹 알맞지 않는 데이터 값입니다.");
                            }
                            return true;
                        } else {
                            String item_name = item.getItemMeta().getDisplayName();
                            if (ItemConfig.getItemStack("list." + item_name) != null) {
                                ItemConfig.set("list." + item_name, null);
                                ConfigManager.saveConfigs();
                                player.sendMessage(Prefix + " 해당 아이템을 제거했습니다.");
                            } else {
                                player.sendMessage(Prefix + " 해당 아이템은 존재하지 않아 제거할 수 없습니다.");
                            }
                            return true;
                        }
                    } else {
                        player.sendMessage(Prefix + " 손에 아이템을 들고 작업해주세요.");
                        return true;
                    }
                } else {
                    if (Objects.equals(Config.getString("database.type"), "mysql")) {
                        if (Select_Item.ItemSelect(args[0]) == 0) {
                            Delete_Item.DeleteItemData(args[0]);
                            player.sendMessage(Prefix + " 해당 이름의 아이템을 제거했습니다.");
                        } else {
                            player.sendMessage(Prefix + " 해당 이름의 아이템을 제거하는 중 문제가 발생했습니다.");
                            player.sendMessage(Prefix + " 이미 삭제된 데이터 혹 알맞지 않는 데이터 값입니다.");
                        }
                    } else {
                        if (ItemConfig.getItemStack("list." + args[0]) != null) {
                            ItemConfig.set("list." + args[0], null);
                            ItemConfig.set("point." + args[0], null);
                            ConfigManager.saveConfigs();
                            player.sendMessage(Prefix + " 해당 이름의 아이템을 제거했습니다.");
                        } else {
                            player.sendMessage(Prefix + " 해당 이름의 아이템은 존재하지 않아 제거할 수 없습니다.");
                        }
                    }
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("회복템목록")) {
                ItemListGUI inv = null;
                try {
                    inv = new ItemListGUI();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                inv.open(player);
                return true;
            }
            player.sendMessage(Prefix + " /피로도설정 지급 [닉네임] - 플레이어에게 피로도 아이템을 지급합니다.");
            player.sendMessage(Prefix + " /피로도설정 설정 [닉네임] [정도] - 플레이어 피로도를 설정합니다.");
            player.sendMessage(Prefix + " /피로도설정 회복템추가 [회복정도] - 손에든 아이템을 회복템으로 추가합니다.");
            player.sendMessage(Prefix + " /피로도설정 회복템제거 - 손에든 회복템을 삭제합니다.");
            player.sendMessage(Prefix + " /피로도설정 회복템제거 [이름] - 이름과 동일한 회복템을 삭제합니다.");
            player.sendMessage(Prefix + " /피로도설정 회복템목록 - 회복템 목록을 확인합니다.");
            return true;
        }
        return false;
    }

    public void DeleteItem (Player player, String item_name){
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
    }

    public static String EncodeItem(ItemStack itemStack) throws IllegalStateException {
        String EncodedItem = "";
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutPut = new BukkitObjectOutputStream(byteArrayOutputStream);
            dataOutPut.writeObject(itemStack);
            dataOutPut.close();
            EncodedItem = Base64Coder.encodeLines(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return EncodedItem.replaceAll("\\r\\n|\\r|\\n", "%space%");
    }
}