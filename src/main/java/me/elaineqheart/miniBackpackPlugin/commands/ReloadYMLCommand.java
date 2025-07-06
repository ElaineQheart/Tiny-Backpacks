package me.elaineqheart.miniBackpackPlugin.commands;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadYMLCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        MiniBackpackPlugin.getPlugin().reloadConfig();
        ItemManager.reloadBackpacks(true, true);
        if (commandSender instanceof Player p) {
            p.sendMessage(ChatColor.GREEN + "MiniBackpack config.yml reloaded successfully!");
        }

        return true;
    }
}
