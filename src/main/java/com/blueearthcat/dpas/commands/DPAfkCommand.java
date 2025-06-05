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

import static com.blueearthcat.dpas.AfkShop.getLang;
import static com.blueearthcat.dpas.AfkShop.getPrefix;
import static com.blueearthcat.dpas.functions.DPASFunction.getPlayer;

public class DPAfkCommand {
    private final CommandBuilder builder;

    public DPAfkCommand() {
        builder = new CommandBuilder(getPrefix());

        builder.addSubCommand("setting", "dpas.setting", getLang().get("cmd_setting"), false, (p, args) -> {
            if (args.length == 3) AfkShop.afkData.setPointAndTime(p, args[1], args[2]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_setting"));
        });
        builder.addSubCommand("wgadd", "dpas.wgadd", getLang().get("cmd_wgadd"), true, (p, args) -> {
            if (args.length == 2) AfkShop.afkData.addAfkLocation((Player) p, args[1]);
            else if (args.length == 3) AfkShop.afkData.addAfkLocation((Player) p, args[1], args[2]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_wgadd"));
        });
        builder.addSubCommand("wglist", "dpas.wgadd", getLang().get("cmd_wglist"), true, (p, args) -> {
            if (args.length == 1) AfkShop.afkData.getAfkLocationList((Player) p);
            else p.sendMessage(getPrefix() + getLang().get("cmd_wglist"));
        });
        builder.addSubCommand("wgremove", "dpas.wgremove", getLang().get("cmd_wgremove"), true, (p, args) -> {
            if (args.length == 2) AfkShop.afkData.removeAfkLocation((Player) p, args[1]);
            else if (args.length == 3) AfkShop.afkData.removeAfkLocation((Player) p, args[1], args[2]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_wgremove"));
        });
        builder.addSubCommand("point", "dpas.point", getLang().get("cmd_point"), false, (p, args) -> {
            if (args.length == 2) {
                if (p instanceof Player) DPASFunction.pointSetting(p, args[1], null, (Player) p);
                else p.sendMessage(getPrefix() + getLang().get("player_only"));
            } else if (args.length == 3) {
                if (p instanceof Player) DPASFunction.pointSetting(p, args[1], args[2], (Player) p);
                else p.sendMessage(getPrefix() + getLang().get("player_only"));
            } else if (args.length == 4) DPASFunction.pointSetting(p, args[1], args[2], getPlayer(args[3]));
            else p.sendMessage(getPrefix() + getLang().get("cmd_point"));
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
