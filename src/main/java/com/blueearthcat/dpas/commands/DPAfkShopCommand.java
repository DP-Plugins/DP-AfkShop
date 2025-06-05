package com.blueearthcat.dpas.commands;

import com.blueearthcat.dpas.AfkShop;
import com.blueearthcat.dpas.functions.DPASFunction;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.blueearthcat.dpas.AfkShop.getLang;
import static com.blueearthcat.dpas.AfkShop.getPrefix;

public class DPAfkShopCommand {
    private final CommandBuilder builder;

    public DPAfkShopCommand() {
        builder = new CommandBuilder(getPrefix());

        builder.addSubCommand("create", "dpas.create", getLang().get("cmd_create"), true, (p, args) -> {
            if (args.length == 2) DPASFunction.createAfkShop((Player) p, args[1]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_create"));
        });
        builder.addSubCommand("open", "dpas.open", getLang().get("cmd_open"), true, (p, args) -> {
            if (args.length == 2) DPASFunction.openAfkShop((Player) p, args[1]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_open"));
        });
        builder.addSubCommand("delete", "dpas.delete", getLang().get("cmd_delete"), true, (p, args) -> {
            if (args.length == 2) DPASFunction.deleteAfkShop((Player) p, args[1]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_delete"));
        });
        builder.addSubCommand("items", "dpas.items", getLang().get("cmd_items"), true, (p, args) -> {
            if (args.length == 2) DPASFunction.openItemSettingGUI((Player) p, args[1]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_items"));
        });
        builder.addSubCommand("price", "dpas.price", getLang().get("cmd_price"), true, (p, args) -> {
            if (args.length == 2) DPASFunction.openPriceSettingGUI((Player) p, args[1]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_price"));
        });
        builder.addSubCommand("page", "dpas.page", getLang().get("cmd_page"), true, (p, args) -> {
            if (args.length == 3) DPASFunction.setPageAfkShop((Player) p, args[1], args[2]);
            else p.sendMessage(getPrefix() + getLang().get("cmd_page"));
        });
        List<String> commands = Arrays.asList("create", "open", "delete", "items", "price", "page");
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
