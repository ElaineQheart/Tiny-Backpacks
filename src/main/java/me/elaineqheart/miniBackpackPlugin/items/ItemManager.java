package me.elaineqheart.miniBackpackPlugin.items;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.UUID;

public class ItemManager {

    public static ItemStack tinyBackpack;
    public static ItemStack smallBackpack;
    public static ItemStack barrier;

    private static final String TINY_BACKPACK_TEXTURE = "http://textures.minecraft.net/texture/9165ee13a606e1b44695af46c39b52ce66657a4c4a623d0b282a7b8ce0509404";
    private static final String SMALL_BACKPACK_TEXTURE = "http://textures.minecraft.net/texture/2308bf5cc3e9decaf0770c3fdad1e042121cf39cc2505bbb866e18c6d23ccd0c";


    public static void init() {
        createTinyBackpack();
        createSmallBackpack();
        createBarrier();
    }

    private static void createTinyBackpack() {
        ItemStack item = makeSkull(TINY_BACKPACK_TEXTURE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName("Tiny Backpack");
        meta.setMaxStackSize(1);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "tinyBackpackItems"), PersistentDataType.STRING, "");

        item.setItemMeta(meta);
        tinyBackpack = item;
    }
    private static void createSmallBackpack() {
        ItemStack item = makeSkull(SMALL_BACKPACK_TEXTURE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName("Small Backpack");
        meta.setMaxStackSize(1);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "SmallBackpackItems"), PersistentDataType.STRING, "");

        item.setItemMeta(meta);
        smallBackpack = item;
    }
    private static void createBarrier() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Locked Slot");

        item.setItemMeta(meta);
        barrier = item;
    }

    private static ItemStack makeSkull(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        PlayerProfile skullProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = skullProfile.getTextures();
        try {
            textures.setSkin(URI.create(url).toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        skullProfile.setTextures(textures);
        assert skullMeta != null;
        skullMeta.setOwnerProfile(skullProfile);
        skull.setItemMeta(skullMeta);
        return skull;
    }

}
