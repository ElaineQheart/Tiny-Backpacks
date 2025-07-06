package me.elaineqheart.miniBackpackPlugin.items;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class StorageConfig {

    private static File file;
    private static FileConfiguration customFile;

    //Finds or generates the custom config file
    public static void setup(){
        file = new File(MiniBackpackPlugin.getPlugin().getDataFolder(), "persistent_large_storage.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //uwu
            }

        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try {
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}
