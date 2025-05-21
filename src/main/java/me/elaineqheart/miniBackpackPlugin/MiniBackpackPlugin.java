package me.elaineqheart.miniBackpackPlugin;

import me.elaineqheart.miniBackpackPlugin.GUI.GUIListener;
import me.elaineqheart.miniBackpackPlugin.GUI.GUIManager;
import me.elaineqheart.miniBackpackPlugin.commands.GiveBackpackCommand;
import me.elaineqheart.miniBackpackPlugin.commands.OpenGUITestCommand;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.listener.OpenBackpackListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniBackpackPlugin extends JavaPlugin {

    private static MiniBackpackPlugin getPlugin;
    private static GUIManager guiManager;
    public static MiniBackpackPlugin getPlugin() {return getPlugin;}
    public static GUIManager getGuiManager() {return guiManager;}

    @Override
    public void onEnable() {
        getPlugin = this;
        ItemManager.init();
        guiManager = new GUIManager();
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(guiListener, this);

        getCommand("bp").setExecutor(new OpenGUITestCommand());
        Bukkit.getPluginManager().registerEvents(new OpenBackpackListener(), this);
        getCommand("backpackitems").setExecutor(new GiveBackpackCommand());
    }
}
