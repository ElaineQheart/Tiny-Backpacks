package me.elaineqheart.miniBackpackPlugin.commands;

import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveBackpackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p) {
            p.getInventory().addItem(ItemManager.smallBackpack);
            p.getInventory().addItem(ItemManager.tinyBackpack);
        }

        return true;
    }
}
