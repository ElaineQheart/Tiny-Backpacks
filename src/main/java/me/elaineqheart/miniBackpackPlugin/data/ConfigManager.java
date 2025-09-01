package me.elaineqheart.miniBackpackPlugin.data;

public class ConfigManager {

    public static Config StorageConfig = new Config();
    public static Config SettingsConfig = new Config();


    public static void setup(){
        StorageConfig.setup("persistent_large_storage");
        StorageConfig.get().options().copyDefaults(false);
        StorageConfig.save();
        SettingsConfig.setup("settings");
        SettingsConfig.get().options().copyDefaults(true);
        SettingsConfig.save();
    }

    public static void reload(){
        StorageConfig.reload();
        SettingsConfig.reload();
    }

}
