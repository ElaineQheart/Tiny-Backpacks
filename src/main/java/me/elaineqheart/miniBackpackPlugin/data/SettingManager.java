package me.elaineqheart.miniBackpackPlugin.data;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingManager {

    public static Sound open;
    public static Sound close;

    static {
        loadData();
    }

    private static void loadData() {
        FileConfiguration c = ConfigManager.SettingsConfig.get();
        open = Sound.valueOf(c.getString("sound_open", "ENTITY_HORSE_ARMOR"));
        close = Sound.valueOf(c.getString("sound_close", "ENTITY_BAT_TAKEOFF"));
    }

}
