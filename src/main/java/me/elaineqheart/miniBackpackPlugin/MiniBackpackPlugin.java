package me.elaineqheart.miniBackpackPlugin;

import me.elaineqheart.miniBackpackPlugin.GUI.GUIListener;
import me.elaineqheart.miniBackpackPlugin.GUI.GUIManager;
import me.elaineqheart.miniBackpackPlugin.GUI.backpackManagers.StorageGUIListener;
import me.elaineqheart.miniBackpackPlugin.GUI.backpackManagers.StorageGUIManager;
import me.elaineqheart.miniBackpackPlugin.GUI.other.EditCraftingBackArrowListener;
import me.elaineqheart.miniBackpackPlugin.GUI.other.input.AnvilGUIManager;
import me.elaineqheart.miniBackpackPlugin.GUI.other.input.ChatInputListener;
import me.elaineqheart.miniBackpackPlugin.commands.EditBackpacksCommand;
import me.elaineqheart.miniBackpackPlugin.commands.GiveBackpackCommand;
import me.elaineqheart.miniBackpackPlugin.commands.ReloadYMLCommand;
import me.elaineqheart.miniBackpackPlugin.items.CraftingListener;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.listener.OpenBackpackListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiniBackpackPlugin extends JavaPlugin {

    private static MiniBackpackPlugin getPlugin;
    private static StorageGUIManager storageGUIManager;
    private static GUIManager guiManager;
    private static AnvilGUIManager searchGUI;
    public static MiniBackpackPlugin getPlugin() {return getPlugin;}
    public static StorageGUIManager getStorageGUIManager() {return storageGUIManager;}
    public static GUIManager getGUIManager() {return guiManager;}
    public static AnvilGUIManager getSearchGUI() {return searchGUI;}

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        getPlugin = this;
        ItemManager.init();
        storageGUIManager = new StorageGUIManager();
        guiManager = new GUIManager();
        searchGUI = new AnvilGUIManager();
        StorageGUIListener storageGUIListener = new StorageGUIListener(storageGUIManager);
        GUIListener guiListener = new GUIListener(guiManager);
        Bukkit.getPluginManager().registerEvents(storageGUIListener,this);
        Bukkit.getPluginManager().registerEvents(guiListener, this);
        Bukkit.getPluginManager().registerEvents(new AnvilGUIManager(), this);

        Bukkit.getPluginManager().registerEvents(new OpenBackpackListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatInputListener(), this);
        Bukkit.getPluginManager().registerEvents(new CraftingListener(), this);
        Bukkit.getPluginManager().registerEvents(new EditCraftingBackArrowListener(), this);
        getCommand("editbackpack").setExecutor(new EditBackpacksCommand());
        getCommand("backpackreload").setExecutor(new ReloadYMLCommand());
        getCommand("backpackitems").setExecutor(new GiveBackpackCommand());

        //Setup config
        reloadConfig(); //reload if there were changes
        getConfig().options().copyDefaults(false);
        saveConfig();
        getLogger().info("MiniBackpacks enabled in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            p.closeInventory();
        }
    }
}
