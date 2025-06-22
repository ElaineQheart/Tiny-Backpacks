package me.elaineqheart.miniBackpackPlugin.items;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class BackpackNote {

    public String name; //"Iron Backpack", "Tiny Backpack"
    public int slots;
    public String texture;
    public boolean isHopperSized;
    public boolean hasUpgradeBackpack; //the backpack this is crafted from, can be null
    public String[] craftingMaterials; //can be null
    // something like [air, leather, air, leather, tiny_backpack, leather, air, leather, air]

    public BackpackNote(String name, int slots, String texture, boolean isHopperSized, boolean hasUpgradeBackpack, String[] craftingMaterials) {
        this.name = name;
        this.slots = slots;
        this.texture = texture;
        this.isHopperSized = isHopperSized;
        this.hasUpgradeBackpack = hasUpgradeBackpack;
        this.craftingMaterials = craftingMaterials;
    }

    public BackpackNote(){}


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

    public void setMaterial(int index, String material) {
        if (craftingMaterials == null) {
            craftingMaterials = new String[]{
                "air", "air", "air",
                "air", "air", "air",
                "air", "air", "air"
            };
        }
        craftingMaterials[index] = material;
        if(Arrays.equals(craftingMaterials, new String[]{
                "air", "air", "air",
                "air", "air", "air",
                "air", "air", "air"
        })) {
            craftingMaterials = null;
        }
    }

}
