package com.blueearthcat.dpas;

import com.blueearthcat.dpas.commands.DPAfkCommand;
import com.blueearthcat.dpas.commands.DPAfkShopCommand;
import com.blueearthcat.dpas.data.AfkData;
import com.blueearthcat.dpas.events.DPASEvent;
import com.blueearthcat.dpas.functions.DPASFunction;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AfkShop extends DPlugin {
    public static AfkShop plugin;
    public static AfkData afkData;
    public static DataContainer<String, YamlConfiguration> shops;
    public static DataContainer<String, YamlConfiguration> udata;
    public static BukkitTask globalTask;
    public static Map<UUID, Integer> afkTime = new HashMap<>();
    public static Map<UUID, Integer> afkTotalTime = new HashMap<>();

    public static AfkShop getInstance() {
        return plugin;
    }

    public static BukkitTask task;

    public AfkShop() {
        super(true);
        plugin = this;
        init();
    }

    @Override
    public void onLoad() {

        DPASFunction.placeholderInit();
        shops = loadDataContainer(new DataContainer<>(this, DataType.YAML, "shops"));
        udata = loadDataContainer(new DataContainer<>(this, DataType.YAML, "udata"));
        PluginUtil.addPlugin(plugin, 26098);
    }

    @Override
    public void onEnable() {
        DPASFunction.init();
        plugin.getServer().getPluginManager().registerEvents(new DPASEvent(), plugin);
        getCommand("dpafk").setExecutor(new DPAfkCommand().getExecutor());
        getCommand("dpafkshop").setExecutor(new DPAfkShopCommand().getExecutor());
    }

    @Override
    public void onDisable() {
        saveDataContainer();
        if (task != null) task.cancel();
    }
}
