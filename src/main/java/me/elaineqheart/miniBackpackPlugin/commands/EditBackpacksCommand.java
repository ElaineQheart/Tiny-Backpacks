package me.elaineqheart.miniBackpackPlugin.commands;

import me.elaineqheart.miniBackpackPlugin.GUI.impl.MainMenuGUI;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EditBackpacksCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p) {
            MiniBackpackPlugin.getGUIManager().openGUI(new MainMenuGUI(),p);
        }

        return true;
    }
}
