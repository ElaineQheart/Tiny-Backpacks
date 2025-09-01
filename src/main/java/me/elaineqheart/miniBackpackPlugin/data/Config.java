package me.elaineqheart.miniBackpackPlugin.data;

import com.google.common.base.Charsets;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {

    private File file;
    private FileConfiguration customFile;

    //Finds or generates the custom config file
    public void setup(String fileName, boolean copyDefaults){
        file = new File(MiniBackpackPlugin.getPlugin().getDataFolder(), fileName + ".yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //uwu
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);

        if(!copyDefaults) return;
        //load the yml file from the jar file and update missing keys with defaults
        final InputStream defConfigStream = MiniBackpackPlugin.getPlugin().getResource(fileName + ".yml");
        if (defConfigStream == null) {
            return;
        }
        customFile.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
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
