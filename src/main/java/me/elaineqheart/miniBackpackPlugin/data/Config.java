package me.elaineqheart.miniBackpackPlugin.data;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private File file;
    private FileConfiguration customFile;

    //Finds or generates the custom config file
    public void setup(String fileName){
        file = new File(MiniBackpackPlugin.getPlugin().getDataFolder(), fileName + ".yml"); //"persistent_large_storage.yml"

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //uwu
            }

        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration get(){
        return customFile;
    }

    public void save(){
        try {
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    public void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}
