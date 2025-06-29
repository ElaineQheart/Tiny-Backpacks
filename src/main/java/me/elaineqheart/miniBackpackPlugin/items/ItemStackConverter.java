package me.elaineqheart.miniBackpackPlugin.items;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemStackConverter {

    public static String encode(ItemStack[] items) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IOException("Unable to save item stacks.", e);
        }
    }

    public static ItemStack[] decode(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized data
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }


    public static void updateItem(ItemStack backpack, ItemStack[] inputItems) {
        ItemMeta meta = backpack.getItemMeta();
        assert meta != null;
        List<ItemStack> itemList = new ArrayList<>();
        for(ItemStack item : inputItems) {
            if(item!=null && !item.equals(ItemManager.barrier)) {
                itemList.add(item);
            }
        }
        try {
            ItemStack[] itemArray = new ItemStack[itemList.size()];
            String data = encode(itemList.toArray(itemArray));
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (itemList.isEmpty()) {
                container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, "");
            } else {
                container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, data);
            }
            backpack.setItemMeta(meta);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save items in backpack", e);
        }
    }

}
