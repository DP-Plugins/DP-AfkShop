package com.blueearthcat.dpas;

import com.blueearthcat.dpas.commands.DPAfkCommand;
import com.blueearthcat.dpas.commands.DPAfkShopCommand;
import com.blueearthcat.dpas.data.AfkData;
import com.blueearthcat.dpas.events.DPASEvent;
import com.blueearthcat.dpas.functions.DPASFunction;
import com.darksoldier1404.dppc.lang.DLang;
import com.darksoldier1404.dppc.utils.DataContainer;
import com.darksoldier1404.dppc.utils.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkShop extends JavaPlugin {
    public static AfkShop plugin;
    public static DataContainer data;
    public static AfkData afkData;
    public static Map<String, YamlConfiguration> shops = new HashMap<>();
    public static BukkitTask globalTask;
    public static Map<UUID, Integer> afkTime = new HashMap<>();
    public static Map<UUID, Integer> afkTotalTime = new HashMap<>();

    public static AfkShop getInstance() {
        return plugin;
    }

    public static BukkitTask task;

    public static String getPrefix() {
        return AfkShop.data.getPrefix();
    }

    public static DLang getLang() {
        return AfkShop.data.getLang();
    }

    @Override
    public void onLoad() {
        plugin = this;
        DPASFunction.placeholderInit();
        PluginUtil.addPlugin(plugin, 26098);
    }

    @Override
    public void onEnable() {
        data = new DataContainer(plugin, true);
        DPASFunction.init();
        plugin.getServer().getPluginManager().registerEvents(new DPASEvent(), plugin);
        getCommand("dpafk").setExecutor(new DPAfkCommand().getExecutor());
        getCommand("dpafkshop").setExecutor( new DPAfkShopCommand().getExecutor());
    }

    @Override
    public void onDisable() {
        data.save();
        for(Player player: Bukkit.getOnlinePlayers()) {
            data.saveUserData(player.getUniqueId());
        }
        if (task != null) task.cancel();
    }
}
