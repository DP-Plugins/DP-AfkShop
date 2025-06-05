package com.blueearthcat.dpas.data;

import com.blueearthcat.dpas.AfkShop;
import com.darksoldier1404.dppc.api.worldguard.WorldGuardAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

import static com.blueearthcat.dpas.AfkShop.*;
import static com.blueearthcat.dpas.functions.DPASFunction.givePoint;

public class AfkData {
    private int timeSchedule; //초단위로 저장
    private int pointPerTime;
    private final Map<World, List<String>> afkLocation; //잠수 월드로 지정될 월드


    public AfkData() {
        YamlConfiguration config = AfkShop.data.getConfig();
        timeSchedule = config.getInt("Settings.timeSchedule");
        pointPerTime = config.getInt("Settings.pointPerTime");
        ConfigurationSection worlds = config.getConfigurationSection("Settings.world-limit");
        afkLocation = new HashMap<>();
        if (worlds == null) return;
        for (String w : worlds.getKeys(false)) {

            World world = Bukkit.getWorld(w);
            if (world == null) continue;

            List<String> worldGuards = config.getStringList("Settings.world-limit." + w);
            afkLocation.put(world, worldGuards);
        }
    }

    public void initTaskAfk() {
        if (task != null) task.cancel();
        if (timeSchedule == 0) return;
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> {
            for (World world : afkLocation.keySet()) {
                if (player.getWorld() == world) {
                    for (String guard : afkLocation.get(world)) {
                        if (WorldGuardAPI.isPlayerInRegion(player, guard)) {
                            if (pointPerTime == -1) return;
                            if (plugin.afkTime.containsKey(player.getUniqueId())) {
                                int sec = plugin.afkTime.get(player.getUniqueId());
                                if (sec > timeSchedule) {
                                    givePoint(player, pointPerTime);
                                    plugin.afkTime.remove(player.getUniqueId());
                                }
                            }
                        }
                    }
                }
            }
        }), 0L, 20L);
    }

    public Map<World, List<String>> getAfkLocation() {
        return afkLocation;
    }

    public void addAfkLocation(Player p, String worldGuard) {
        addAfkLocation(p, worldGuard, p.getWorld());
    }

    public void addAfkLocation(Player p, String worldGuard, String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            p.sendMessage(getPrefix() + getLang().get("afk_world_wrong"));
            return;
        }
        addAfkLocation(p, worldGuard, Bukkit.getWorld(worldName));
    }

    public void addAfkLocation(Player p, String worldGuard, World world) {
        YamlConfiguration config = AfkShop.data.getConfig();
        if (!isExistWorld(world)) afkLocation.put(world, new ArrayList<>());
        if (isExistGuard(world, worldGuard)) {
            p.sendMessage(getPrefix() + getLang().get("afk_location_exists"));
            return;
        }
        List<String> guardList = afkLocation.get(world);
        guardList.add(worldGuard);
        afkLocation.put(world, guardList);
        p.sendMessage(getPrefix() + getLang().getWithArgs("afk_location_add", world.getName(), worldGuard));
        config.set("Settings.world-limit." + world.getName(), guardList);
        AfkShop.data.setConfig(config);
        AfkShop.data.save();
        initTaskAfk();
    }

    public void removeAfkLocation(Player p, String worldGuard) {
        removeAfkLocation(p, worldGuard, p.getWorld());
    }

    public void removeAfkLocation(Player p, String worldGuard, String worldName) {
        if (Bukkit.getWorld(worldName) == null) {
            p.sendMessage(getPrefix() + getLang().get("afk_world_wrong"));
            return;
        }
        removeAfkLocation(p, worldGuard, Bukkit.getWorld(worldName));
    }

    public void removeAfkLocation(Player p, String worldGuard, World world) {
        YamlConfiguration config = AfkShop.data.getConfig();
        if (!isExistWorld(world)) {
            p.sendMessage(getPrefix() + getLang().get("afk_world_exists_not"));
            return;
        }
        if (!isExistGuard(world, worldGuard)) {
            p.sendMessage(getPrefix() + getLang().get("afk_location_exists_not"));
            return;
        }
        List<String> guardList = afkLocation.get(world);
        guardList.remove(worldGuard);
        afkLocation.put(world, guardList);
        p.sendMessage(getPrefix() + getLang().getWithArgs("afk_location_remove", world.getName(), worldGuard));
        config.set("Settings.world-limit." + world.getName(), guardList);
        AfkShop.data.setConfig(config);
        AfkShop.data.save();
        initTaskAfk();
    }

    public void setPointAndTime(CommandSender p, String t, String point) {
        if (t == null || !t.matches("\\d+([smh])?")) {
            p.sendMessage(getPrefix() + getLang().get("afk_timeFormatException"));
            return;
        }
        if (point == null || !point.matches("[0-9]+")) {
            p.sendMessage(getPrefix() + getLang().get("afk_pointFormatException"));
            return;
        }
        int time = Integer.parseInt(t.replaceAll("[^0-9]", ""));
        char unit = 's';
        if (t.matches("\\d+[smh]")) {
            unit = t.charAt(t.length() - 1);
        }
        timeSchedule = time;
        switch (unit) {
            case 'm':
                timeSchedule *= 60;
                break;
            case 'h':
                timeSchedule *= 3600;
                break;
            default:
                break;
        }
        pointPerTime = Integer.parseInt(point);
        YamlConfiguration config = data.getConfig();
        config.set("Settings.timeSchedule", timeSchedule);
        config.set("Settings.pointPerTime", pointPerTime);
        AfkShop.data.setConfig(config);
        AfkShop.data.save();
        p.sendMessage(getPrefix() + getLang().getWithArgs("afk_set_point_time", String.valueOf(timeSchedule), String.valueOf(pointPerTime)));
        initTaskAfk();
    }

    private boolean isExistGuard(World world, String worldGuard) {
        return afkLocation.get(world).contains(worldGuard);
    }

    public boolean isExistWorld(World world) {
        return afkLocation.containsKey(world);
    }

    public void getAfkLocationList(Player p) {
        p.sendMessage(getPrefix() + getLang().get("afk_see_wg_list"));
        for (World world : afkLocation.keySet()) {
            for (String guard : afkLocation.get(world)) {
                p.sendMessage(getPrefix() + world.getName() + ":" + guard);
            }
        }
    }
}
