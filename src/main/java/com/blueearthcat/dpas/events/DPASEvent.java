package com.blueearthcat.dpas.events;

import com.blueearthcat.dpas.AfkShop;
import com.blueearthcat.dpas.functions.DPASFunction;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import static com.blueearthcat.dpas.AfkShop.getLang;
import static com.blueearthcat.dpas.AfkShop.getPrefix;
import static com.blueearthcat.dpas.functions.DPASFunction.*;

public class DPASEvent implements Listener {
    private static final AfkShop plugin = AfkShop.getInstance();

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        AfkShop.data.addUserData(e.getPlayer().getUniqueId(), ConfigUtils.initUserData(plugin, e.getPlayer().getUniqueId().toString(), "udata"));
        ConfigUtils.saveCustomData(plugin, AfkShop.data.getUserData(e.getPlayer().getUniqueId()), e.getPlayer().getUniqueId().toString(), "udata");
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        ConfigUtils.saveCustomData(plugin, AfkShop.data.getUserData(e.getPlayer().getUniqueId()), e.getPlayer().getUniqueId().toString(), "udata");
        AfkShop.data.removeUserData(e.getPlayer().getUniqueId());
        AfkShop.afkTime.remove(e.getPlayer().getUniqueId());
        AfkShop.afkTotalTime.remove(e.getPlayer().getUniqueId());
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!(e.getInventory().getHolder() instanceof DInventory)) return;
        DInventory inv = (DInventory) e.getInventory().getHolder();
        if (!inv.isValidHandler(plugin)) return;
        if (e.getCurrentItem() == null) return;
        ItemStack item = e.getCurrentItem();
        if (NBT.hasTagKey(e.getCurrentItem(), "prev")) {
            e.setCancelled(true);
            inv.applyChanges()
            inv.prevPage();
            DPASFunction.updateCurrentPage(inv);
            return;
        }
        if (NBT.hasTagKey(e.getCurrentItem(), "next")) {
            e.setCancelled(true);
            inv.applyChanges()
            inv.nextPage();
            DPASFunction.updateCurrentPage(inv);
            return;
        }
        if (inv.getChannel() == 0) { //item Setting GUI
            if (e.getCurrentItem() == null) return;
            if (NBT.hasTagKey(e.getCurrentItem(), "current") || NBT.hasTagKey(e.getCurrentItem(), "pane")) {
                e.setCancelled(true);
                return;
            }
            return;
        }
        if (inv.getChannel() == 1) { //Item Open GUI
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;
            if (!NBT.hasTagKey(item, "dpas_price")) return;
            int point = NBT.getIntegerTag(item, "dpas_price");
            item = item.clone();
            if (e.getClick() == ClickType.RIGHT) {
                if (hasPoint(p, point)) {
                    p.sendMessage(getPrefix() + getLang().get("afk_no_point"));
                    return;
                }
                if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), transItem(item))) {
                    p.sendMessage(getPrefix() + getLang().get("player_has_no_space"));
                    return;
                }
                removePoint(p, point);
                p.getInventory().addItem(NBT.removeTag(transItem(item),"dpas_price"));
                p.sendMessage(getPrefix() + getLang().get("event_item_buy"));
            }
            if (e.getClick() == ClickType.SHIFT_RIGHT) {
                point *= item.getMaxStackSize();
                if (hasPoint(p, point)) {
                    p.sendMessage(getPrefix() + getLang().get("afk_no_point"));
                    return;
                }
                item.setAmount(item.getMaxStackSize());
                if (!InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), transItem(item))) {
                    p.sendMessage(getPrefix() + getLang().get("player_has_no_space"));
                    return;
                }
                removePoint(p, point);
                p.getInventory().addItem(NBT.removeTag(transItem(item),"dpas_price"));
                p.sendMessage(getPrefix() + getLang().get("event_item_buy_64"));
            }
        }
        if (inv.getChannel() == 2) {//Item Price Setting
            if (e.getSlot() > 44) return ;
            String name = (String) inv.getObj();
            e.setCancelled(true);
            DPASFunction.currentEditItem.put(p.getUniqueId(), Quadruple.of(name, item, e.getSlot(), inv.getCurrentPage()));
            p.closeInventory();
            p.sendMessage(getPrefix() + getLang().get("event_shop_price_set"));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getInventory().getHolder() instanceof DInventory)) return;
        DInventory inv = (DInventory) e.getInventory().getHolder();
        if (!inv.isValidHandler(plugin)) return;
        if (inv.getChannel() == 0) {// item edit mode save
            inv.applyChanges()
            DPASFunction.saveShopItems((Player) e.getPlayer(), (String) inv.getObj(), inv);
        }
        if (inv.getChannel() == 2){
            inv.applyChanges()
            DPASFunction.savePriceItems((Player) e.getPlayer(), (String) inv.getObj(), inv);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (DPASFunction.currentEditItem.containsKey(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            Quadruple<String, ItemStack, Integer, Integer> t = DPASFunction.currentEditItem.get(e.getPlayer().getUniqueId());
            t.setB(DPASFunction.setPrice(e.getPlayer(), t.getB(), e.getMessage()));
            Bukkit.getScheduler().runTask(plugin, () -> {
                DPASFunction.openPriceSettingGUI(e.getPlayer(), t.getA(), t.getB(), t.getC(), t.getD());
                DPASFunction.currentEditItem.remove(e.getPlayer().getUniqueId());
            });
        }
    }
}
