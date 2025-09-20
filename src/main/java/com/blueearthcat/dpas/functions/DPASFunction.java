package com.blueearthcat.dpas.functions;

import com.blueearthcat.dpas.AfkShop;
import com.blueearthcat.dpas.data.AfkData;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.api.placeholder.PlaceholderBuilder;
import com.darksoldier1404.dppc.api.worldguard.WorldGuardAPI;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.ConfigUtils;
import com.darksoldier1404.dppc.utils.NBT;
import com.darksoldier1404.dppc.utils.Quadruple;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.text.NumberFormat;
import java.util.*;

import static com.blueearthcat.dpas.AfkShop.*;

public class DPASFunction {
    public static final Map<UUID, Quadruple<String, ItemStack, Integer, Integer>> currentEditItem = new HashMap<>();


    public static void init() {
        List<YamlConfiguration> shops = ConfigUtils.loadCustomDataList(plugin, "shops");
        shops.forEach(shop -> {
            AfkShop.shops.put(shop.getString("Shop.Name"), shop);
        });
        afkData = new AfkData();

        afkData.initTaskAfk();
        initGlobalTask();
    }

    public static void placeholderInit() {

        new PlaceholderBuilder.Builder(plugin)
                .identifier("dpas")
                .version("1.0.0.0")
                .onRequest((p, str) -> {
                    if (str.equals("afktime")) {
                        if (afkTotalTime.containsKey(p.getUniqueId())) {
                            return String.valueOf(afkTotalTime.get(p.getUniqueId()));
                        } else {
                            return "0";
                        }
                    }
                    if (str.equals("afkpoint")) {
                        if (udata.get(p.getUniqueId()).getString("AfkPoint") == null) {
                            clearPoint(p);
                            return "";
                        }
                        return NumberFormat.getNumberInstance(Locale.US).format(udata.get(p.getUniqueId()).getInt("AfkPoint"));
                    }
                    if (str.equals("afktime_HMS")) {
                        if (afkTotalTime.containsKey(p.getUniqueId())) {
                            return secondsToHMS(afkTotalTime.get(p.getUniqueId()));
                        } else {
                            return secondsToHMS(0);
                        }
                    }
                    if (str.equals("isInAfkArea")) {
                        if (afkTotalTime.containsKey(p.getUniqueId())) {
                            return "True";
                        } else {
                            return "False";
                        }
                    }
                    return null;
                }).build();
    }

    public static void initGlobalTask() {
        globalTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(p -> {
                for (World world : afkData.getAfkLocation().keySet()) {
                    if (p.getWorld() == world) {
                        for (String guard : afkData.getAfkLocation().get(world)) {
                            if (WorldGuardAPI.isPlayerInRegion(p, guard)) {
                                if (!plugin.afkTime.containsKey(p.getUniqueId())) {
                                    plugin.afkTime.put(p.getUniqueId(), 1);
                                } else {
                                    plugin.afkTime.put(p.getUniqueId(), plugin.afkTime.get(p.getUniqueId()) + 1);
                                }
                                if (plugin.afkTotalTime.containsKey(p.getUniqueId())) {
                                    plugin.afkTotalTime.put(p.getUniqueId(), plugin.afkTotalTime.get(p.getUniqueId()) + 1);
                                }else{
                                    plugin.afkTotalTime.put(p.getUniqueId(), 1);
                                }
                            } else {
                                plugin.afkTime.remove(p.getUniqueId());
                                plugin.afkTotalTime.remove(p.getUniqueId());
                            }
                        }
                    }
                }
            });
        }, 0L, 20L);
    }

    public static void createAfkShop(Player p, String name) {
        if (isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_shop_exists"));
            return;
        }
        YamlConfiguration shop = new YamlConfiguration();
        shop.set("Shop.Name", name);
        shop.set("Shop.MaxPage", 0);
        shops.put(name, shop);
        ConfigUtils.saveCustomData(plugin, shop, name, "shops");
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("func_shop_create", name));
    }

    public static boolean isShopExists(String name) {
        return shops.containsKey(name);
    }

    public static void deleteAfkShop(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_shop_not_exists"));
            return;
        }
        new File(plugin.getDataFolder() + "/shops/" + name + ".yml").delete();
        shops.remove(name);
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("func_shop_delete", name));
    }

    public static void openItemSettingGUI(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_shop_not_exists"));
            return;
        }
        String title = plugin.getLang().getWithArgs("func_shop_item_title", name);
        DInventory inv = new DInventory(title, 54, true, plugin);
        inv.setChannel(0);
        inv.setObj(name);
        YamlConfiguration shop = shops.get(name);
        Map<Integer, ItemStack[]> items = deserialize(shop);
        inv.setPages(shop.getInt("Shop.MaxPage"));
        inv.setPageTools(getPageTools(inv));
        for (int i = 0; i < items.size(); i++) {
            inv.setPageContent(i, items.get(i));
        }
        inv.update();
        updateCurrentPage(inv);
        p.openInventory(inv.getInventory());
    }

    public static void saveShopItems(Player player, String name, DInventory inv) {
        YamlConfiguration shop = serialize(inv.getPageItemsWithoutTools(), shops.get(name));
        shops.put(name, shop);
        ConfigUtils.saveCustomData(plugin, shop, name, "shops");
        player.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("func_shop_item_save", name));
    }

    public static void openAfkShop(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_shop_not_exists"));
            return;
        }
        String title = plugin.getLang().getWithArgs("func_shop_open_title", name);
        DInventory inv = new DInventory(title, 54, true, plugin);
        inv.setChannel(1);
        YamlConfiguration shop = shops.get(name);
        Map<Integer, ItemStack[]> items = deserialize(shop);
        inv.setPages(shop.getInt("Shop.MaxPage"));
        inv.setPageTools(getPageTools(inv));
        for (int page = 0; page < items.size(); page++) {
            ItemStack[] itemList = new ItemStack[45];
            for (int slot = 0; slot < items.get(page).length; slot++) {
                ItemStack item = items.get(page)[slot] == null || items.get(page)[slot].getType().isAir() ? null : items.get(page)[slot].clone();
                if (item != null) setItemLoreWithPrice(item);
                itemList[slot] = item;
            }
            inv.setPageContent(page, itemList);
        }
        inv.update();
        updateCurrentPage(inv);
        p.openInventory(inv.getInventory());
    }

    public static void setPageAfkShop(Player p, String name, String page) {
        if (page == null || !page.matches("[0-9]+")) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_pageFormatException"));
            return;
        }
        YamlConfiguration shop = shops.get(name);
        int maxPage = Integer.parseInt(page);
        shop.set("Shop.MaxPage", maxPage);
        shops.put(name, shop);
        ConfigUtils.saveCustomData(plugin, shop, name, "shops");
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("func_shop_page_set", name));
    }

    public static void setItemLoreWithPrice(ItemStack item) {
        int price = -1;
        ItemMeta im = item.getItemMeta();
        List<String> lore = im.getLore() == null ? new ArrayList<>() : im.getLore();
        lore.add("§f");
        if (NBT.hasTagKey(item, "dpas_price")) {
            price = NBT.getIntegerTag(item, "dpas_price");
        }
        if (price <= 0) {
            lore.add(plugin.getLang().get("func_shop_item_lore1") + plugin.getLang().get("func_shop_price_not_set"));
        } else {
            lore.add(plugin.getLang().get("func_shop_item_lore1") + plugin.getLang().getWithArgs("func_shop_price_unit", NumberFormat.getNumberInstance(Locale.US).format(price)));
        }
        lore.add(plugin.getLang().get("func_shop_item_lore2"));
        lore.add(plugin.getLang().get("func_shop_item_lore3"));
        lore.add(plugin.getLang().get("func_shop_item_lore4"));
        im.setLore(lore);
        item.setItemMeta(im);
    }

    public static void openPriceSettingGUI(Player p, String name) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_shop_not_exists"));
            return;
        }
        String title = plugin.getLang().getWithArgs("func_shop_price_title", name);

        DInventory inv = new DInventory(title, 54, true, plugin);
        inv.setChannel(2);
        inv.setObj(name);
        YamlConfiguration shop = shops.get(name);
        Map<Integer, ItemStack[]> items = deserialize(shop);
        inv.setPages(shop.getInt("Shop.MaxPage"));
        inv.setPageTools(getPageTools(inv));
        for (int page = 0; page < items.size(); page++) {
            ItemStack[] itemList = new ItemStack[45];
            for (int slot = 0; slot < items.get(page).length; slot++) {
                ItemStack item = items.get(page)[slot] == null || items.get(page)[slot].getType().isAir() ? null : items.get(page)[slot].clone();
                if (item != null) setItemLoreWithPrice(item);
                itemList[slot] = item;
            }
            inv.setPageContent(page, itemList);
        }
        inv.update();
        updateCurrentPage(inv);
        p.openInventory(inv.getInventory());
    }

    public static void openPriceSettingGUI(Player p, String name, ItemStack item2, int slot2, int page2) {
        if (!isShopExists(name)) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_shop_not_exists"));
            return;
        }
        String title = plugin.getLang().getWithArgs("func_shop_price_title", name);

        DInventory inv = new DInventory(title, 54, true, plugin);
        inv.setChannel(2);
        inv.setObj(name);
        YamlConfiguration shop = shops.get(name);
        Map<Integer, ItemStack[]> items = deserialize(shop);
        inv.setPages(shop.getInt("Shop.MaxPage"));
        inv.setPageTools(getPageTools(inv));
        for (int page = 0; page < items.size(); page++) {
            ItemStack[] itemList = new ItemStack[45];
            for (int slot = 0; slot < items.get(page).length; slot++) {
                ItemStack item = items.get(page)[slot] == null || items.get(page)[slot].getType().isAir() ? null : items.get(page)[slot].clone();
                if (page2 == page && slot2 == slot) {
                    item = transItem(item2);
                }
                if (item != null) setItemLoreWithPrice(item);
                itemList[slot] = item;
            }
            inv.setPageContent(page, itemList);
        }
        inv.update();
        updateCurrentPage(inv);
        p.openInventory(inv.getInventory());
    }


    public static YamlConfiguration serialize(Map<Integer, ItemStack[]> items, YamlConfiguration data) {
        if (items.isEmpty()) {
            data.set("Shop.Items", null);
            return data;
        }
        for (int page : items.keySet()) {
            for (int i = 0; i < items.get(page).length; i++) {
                data.set("Shop.Items." + page + "." + i, items.get(page)[i]);
            }
        }
        return data;
    }

    public static Map<Integer, ItemStack[]> deserialize(YamlConfiguration shop) {
        Map<Integer, ItemStack[]> itemsMap = new HashMap<>();
        if (shop.getConfigurationSection("Shop.Items") == null) return itemsMap;
        for (String page : shop.getConfigurationSection("Shop.Items").getKeys(false)) {
            ItemStack[] items = new ItemStack[45];
            for (String slot : shop.getConfigurationSection("Shop.Items." + page).getKeys(false)) {
                items[Integer.parseInt(slot)] = shop.getItemStack("Shop.Items." + page + "." + slot);
            }
            itemsMap.put(Integer.parseInt(page), items);
        }
        return itemsMap;
    }

    public static String secondsToHMS(long seconds) {
        if (seconds < 0) {
            return "Invalid input";
        }

        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    private static ItemStack[] getPageTools(DInventory inv) {

        ItemStack prev = NBT.setStringTag(new ItemStack(Material.ARROW), "prev", "true");
        ItemStack next = NBT.setStringTag(new ItemStack(Material.ARROW), "next", "true");
        ItemStack current = NBT.setStringTag(new ItemStack(Material.PAPER), "current", "true");
        ItemStack pane = NBT.setStringTag(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "pane", "true");

        ItemMeta im = prev.getItemMeta();
        im.setDisplayName(plugin.getLang().get("prev_page"));
        prev.setItemMeta(im);

        im = next.getItemMeta();
        im.setDisplayName(plugin.getLang().get("next_page"));
        next.setItemMeta(im);

        im = current.getItemMeta();
        im.setDisplayName(plugin.getLang().get("current_page") + (inv.getCurrentPage() + 1) + "/" + (inv.getPages() + 1));
        current.setItemMeta(im);

        im = pane.getItemMeta();
        im.setDisplayName("§f");
        pane.setItemMeta(im);
        return new ItemStack[]{pane, pane, prev, pane, current, pane, next, pane, pane};
    }

    public static void updateCurrentPage(DInventory inv) {
        ItemStack[] tools = inv.getPageTools();
        ItemStack cpage = tools[4];
        ItemMeta im = cpage.getItemMeta();
        im.setDisplayName(plugin.getLang().get("current_page") + (inv.getCurrentPage() + 1) + "/" + (inv.getPages() + 1));
        cpage.setItemMeta(im);
        inv.setPageTools(tools);
        inv.update();
    }

    public static ItemStack setPrice(Player p, ItemStack b, String price) {
        if (price == null || !price.matches("[0-9]+")) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("func_priceFormatException"));
            return b;
        }
        return NBT.setIntTag(b, "dpas_price", Integer.parseInt(price));
    }

    public static ItemStack transItem(ItemStack item) {
        ItemMeta im = item.getItemMeta();
        if (im == null || !im.hasLore()) return item;
        List<String> lore = new ArrayList<>(im.getLore());
        if (lore.size() < 5) return item;
        lore = lore.subList(0, lore.size() - 5);
        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }

    public static Player getPlayer(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
            return offlinePlayer.getPlayer();
        } else {
            return null;
        }
    }

    public static void pointSetting(CommandSender p, String arg, String po, Player receiver) {
        if (arg.equalsIgnoreCase("clear")) {
            clearPoint(receiver, p);
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("afk_clear_success"));
            return;
        }
        if (po == null || !po.matches("[0-9]+")) {
            p.sendMessage(plugin.getPrefix() + plugin.getLang().get("afk_pointFormatException"));
            return;
        }
        int point = Integer.parseInt(po);
        if (arg.equalsIgnoreCase("set")) setPoint(receiver, point, p);
        else if (arg.equalsIgnoreCase("give")) givePoint(receiver, point, p);
        else if (arg.equalsIgnoreCase("take")) removePoint(receiver, point, p);
        else p.sendMessage(plugin.getPrefix() + plugin.getLang().get("cmd_point"));
    }

    public static void givePoint(Player player, int point) {
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        int current = data.getInt("AfkPoint");
        data.set("AfkPoint", current + point);
        player.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_get_point", NumberFormat.getNumberInstance(Locale.US).format(point)));
    }

    public static void givePoint(Player player, int point, CommandSender p) {
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        int current = data.getInt("AfkPoint");
        data.set("AfkPoint", current + point);
        player.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_get_point", NumberFormat.getNumberInstance(Locale.US).format(point)));
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_give_point", NumberFormat.getNumberInstance(Locale.US).format(point)));
    }

    public static void setPoint(Player player, int point, CommandSender p) {
        if (point < 0) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("afk_wrong_set"));
            return;
        }
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        data.set("AfkPoint", point);
        player.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_set_point", NumberFormat.getNumberInstance(Locale.US).format(point)));
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_set_point_player", NumberFormat.getNumberInstance(Locale.US).format(point)));
    }

    public static void removePoint(Player player, int point) {
        if (hasPoint(player, point)) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("afk_no_point"));
            return;
        }
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        int current = data.getInt("AfkPoint");
        data.set("AfkPoint", current - point);
        player.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_take_point", NumberFormat.getNumberInstance(Locale.US).format(point)));
    }

    public static void removePoint(Player player, int point, CommandSender p) {
        if (hasPoint(player, point)) {
            player.sendMessage(plugin.getPrefix() + plugin.getLang().get("afk_no_point"));
            return;
        }
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        int current = data.getInt("AfkPoint");
        data.set("AfkPoint", current - point);
        player.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_take_point", NumberFormat.getNumberInstance(Locale.US).format(point)));
        p.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("player_take_point_player", NumberFormat.getNumberInstance(Locale.US).format(point)));
    }

    public static void clearPoint(Player player) {
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        data.set("AfkPoint", 0);
    }

    public static void clearPoint(Player player, CommandSender c) {
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        data.set("AfkPoint", 0);
        c.sendMessage(plugin.getPrefix() + plugin.getLang().get("player_clear_point"));
    }

    public static boolean hasPoint(Player player, int point) {
        YamlConfiguration data = AfkShop.udata.get(player.getUniqueId());
        int playerPoint = data.getInt("AfkPoint");
        return point > playerPoint;
    }

    public static void savePriceItems(Player player, String name, DInventory inv) {
        Map<Integer, ItemStack[]> items = inv.getPageItemsWithoutTools();
        for (int page = 0; page < items.size(); page++) {
            for (int slot = 0; slot < items.get(page).length; slot++) {
                if (items.get(page)[slot] == null) continue;
                items.get(page)[slot] = transItem(items.get(page)[slot]);
            }
        }
        inv.update();
        YamlConfiguration shop = serialize(inv.getPageItemsWithoutTools(), shops.get(name));
        shops.put(name, shop);
        ConfigUtils.saveCustomData(plugin, shop, name, "shops");
        player.sendMessage(plugin.getPrefix() + plugin.getLang().getWithArgs("func_shop_item_save", name));
    }
}
