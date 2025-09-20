package com.blueearthcat.dpas.commands;

import com.blueearthcat.dpas.AfkShop;
import com.blueearthcat.dpas.functions.DPASFunction;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DPAfkShopCommand {
    private final CommandBuilder builder;
    private static final AfkShop plugin = AfkShop.getInstance();

    public DPAfkShopCommand() {
        builder = new CommandBuilder(plugin);

        builder.addSubCommand("create", "dpas.create", plugin.getLang().get("cmd_create"), true, (p, args) -> {
            if (args.length == 2) {
                DPASFunction.createAfkShop((Player) p, args[1]);
                return true;
            } else return false;
        });
        builder.addSubCommand("open", "dpas.open", plugin.getLang().get("cmd_open"), true, (p, args) -> {
            if (args.length == 2) {
                DPASFunction.openAfkShop((Player) p, args[1]);
                return true;
            } else return false;
        });
        builder.addSubCommand("delete", "dpas.delete", plugin.getLang().get("cmd_delete"), true, (p, args) -> {
            if (args.length == 2) {
                DPASFunction.deleteAfkShop((Player) p, args[1]);
                return true;
            } else return false;
        });
        builder.addSubCommand("items", "dpas.items", plugin.getLang().get("cmd_items"), true, (p, args) -> {
            if (args.length == 2) {
                DPASFunction.openItemSettingGUI((Player) p, args[1]);
                return true;
            } else return false;
        });
        builder.addSubCommand("price", "dpas.price", plugin.getLang().get("cmd_price"), true, (p, args) -> {
            if (args.length == 2) {
                DPASFunction.openPriceSettingGUI((Player) p, args[1]);
                return true;
            } else return false;
        });
        builder.addSubCommand("page", "dpas.page", plugin.getLang().get("cmd_page"), true, (p, args) -> {
            if (args.length == 3) {
                DPASFunction.setPageAfkShop((Player) p, args[1], args[2]);
                return true;
            } else return false;
        });
        List<String> commands = Arrays.asList("open", "delete", "items", "price", "page");
        for (String c : commands) {
            builder.addTabCompletion(c, args -> {
                if (args.length == 2) return new ArrayList<>(AfkShop.shops.keySet());
                return null;
            });
        }
        //===========================================

    }

    public CommandExecutor getExecutor() {
        return builder;
    }
}
