package me.elaineqheart.miniBackpackPlugin;

import me.elaineqheart.miniBackpackPlugin.GUI.GUIListener;
import me.elaineqheart.miniBackpackPlugin.GUI.StorageGUIManager;
import me.elaineqheart.miniBackpackPlugin.commands.GiveBackpackCommand;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.listener.OpenBackpackListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniBackpackPlugin extends JavaPlugin {

    private static MiniBackpackPlugin getPlugin;
    private static StorageGUIManager guiManager;
    public static MiniBackpackPlugin getPlugin() {return getPlugin;}
    public static StorageGUIManager getGuiManager() {return guiManager;}

    @Override
    public void onEnable() {
        getPlugin = this;
        ItemManager.init();
        guiManager = new StorageGUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);

        Bukkit.getPluginManager().registerEvents(new OpenBackpackListener(), this);
        getCommand("backpackitems").setExecutor(new GiveBackpackCommand());

        //Setup config
        reloadConfig(); //reload if there were changes
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
