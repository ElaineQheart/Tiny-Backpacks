package me.elaineqheart.miniBackpackPlugin.items;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class Backpack {

    public String name;
    public int slots;
    public String texture;
    public boolean isHopperSized;
    public String upgradeBackpack; //the backpack this is crafted from, can be null
    public List<String> craftingMaterials; //can be null

    public Backpack (String name, int slots, String texture, boolean isHopperSized, String upgradeBackpack, List<String> craftingMaterials) {
        this.name = name;
        this.slots = slots;
        this.texture = texture;
        this.isHopperSized = isHopperSized;
        this.upgradeBackpack = upgradeBackpack;
        this.craftingMaterials = craftingMaterials;
    }

    public Backpack(){}


    public String toString() {
        return "Backpack{" + "name=" + name + ", slots=" + slots + ", texture=" + texture + "}";
    }

    public ItemStack toItemStack() {
        ItemStack item = ItemManager.makeSkull(texture);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(name);
        meta.setMaxStackSize(1);
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, "");
        //there shouldn't be two backpacks with the same slot count
        container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER, slots);

        item.setItemMeta(meta);
        return item;
    }

}
