package com.blueearthcat.dpas.commands;

import com.blueearthcat.dpas.AfkShop;
import com.blueearthcat.dpas.functions.DPASFunction;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.blueearthcat.dpas.functions.DPASFunction.getPlayer;

public class DPAfkCommand {
    private final CommandBuilder builder;
    private static final AfkShop plugin = AfkShop.getInstance();

    public DPAfkCommand() {
        builder = new CommandBuilder(plugin);

        builder.addSubCommand("setting", "dpas.setting", plugin.getLang().get("cmd_setting"), false, (p, args) -> {
            if (args.length == 3) {
                AfkShop.afkData.setPointAndTime(p, args[1], args[2]);
                return true;
            } else return false;
        });
        builder.addSubCommand("wgadd", "dpas.wgadd", plugin.getLang().get("cmd_wgadd"), true, (p, args) -> {
            if (args.length == 2) {
                AfkShop.afkData.addAfkLocation((Player) p, args[1]);
                return true;
            } else if (args.length == 3) {
                AfkShop.afkData.addAfkLocation((Player) p, args[1], args[2]);
                return true;
            } else return false;
        });
        builder.addSubCommand("wglist", "dpas.wgadd", plugin.getLang().get("cmd_wglist"), true, (p, args) -> {
            if (args.length == 1) {
                AfkShop.afkData.getAfkLocationList((Player) p);
                return true;
            } else return false;
        });
        builder.addSubCommand("wgremove", "dpas.wgremove", plugin.getLang().get("cmd_wgremove"), true, (p, args) -> {
            if (args.length == 2) {
                AfkShop.afkData.removeAfkLocation((Player) p, args[1]);
                return true;
            } else if (args.length == 3) {
                AfkShop.afkData.removeAfkLocation((Player) p, args[1], args[2]);
                return true;
            } else return false;
        });
        builder.addSubCommand("point", "dpas.point", plugin.getLang().get("cmd_point"), false, (p, args) -> {
            if (args.length == 2) {
                if (p instanceof Player) {
                    DPASFunction.pointSetting(p, args[1], null, (Player) p);
                    return true;
                } else {
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().get("player_only"));
                    return true;
                }
            } else if (args.length == 3) {
                if (p instanceof Player) {
                    DPASFunction.pointSetting(p, args[1], args[2], (Player) p);
                    return true;
                } else {
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().get("player_only"));
                    return true;
                }
            } else if (args.length == 4) {
                DPASFunction.pointSetting(p, args[1], args[2], getPlayer(args[3]));
                return true;
            } else return false;
        });
        List<String> worldList = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        builder.addTabCompletion("wgadd", args -> {
            if (args.length == 3) return worldList;
            return null;
        });
        builder.addTabCompletion("wgremove", args -> {
            if (args.length == 3) return worldList;
            return null;
        });
        builder.addTabCompletion("setting", args -> {
            if (args.length == 2) return Arrays.asList("30", "10s", "1m", "1h");
            return null;
        });
        builder.addTabCompletion("point", args -> {
            if (args.length == 2) {
                return Arrays.asList("give", "set", "take", "clear");
            }

            if (args.length == 3 && args[2].equalsIgnoreCase("clear")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
            }

            if (args.length == 4 && Arrays.asList("give", "set", "take").contains(args[2].toLowerCase())) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList());
            }

            return null;
        });
    }

    public CommandExecutor getExecutor() {
        return builder;
    }
}
