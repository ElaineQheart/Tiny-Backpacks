package me.elaineqheart.miniBackpackPlugin.commands;

import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GiveBackpackCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player p) {
            if(strings.length == 0) {
                return false; //send the player the usage message
            }

            if(strings.length == 1) {
                //check if the item exists in the ItemManager
                ItemStack item = ItemManager.getBackpackFromName(strings[0]);
                if(item == null) {
                    p.sendMessage("That backpack does not exist!");
                    return true;
                }
                //give the player the backpack
                p.getInventory().addItem(item);
                return true;
            }
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> params = new ArrayList<>();
        if(strings.length==1) {
            //check for every item if it's half typed out, then add accordingly to the params list
            List<String> assetParams = new ArrayList<>();
            for(ItemStack item : ItemManager.backpacks) {
                assert item.getItemMeta() != null;
                assetParams.add(ItemManager.toDataCase(item.getItemMeta().getItemName()));
            }
            for (String p : assetParams) {
                if (p.indexOf(strings[0]) == 0){
                    params.add(p);
                }
            }

        }
        return params;
    }
}
