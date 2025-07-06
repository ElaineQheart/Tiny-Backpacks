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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemStackConverter {

    public static String encode(ItemStack[] items) throws IOException {
        List<String> encodedItems = new ArrayList<>();
        for (ItemStack item : items) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
                dataOutput.writeObject(item);
                dataOutput.close();
                encodedItems.add(Base64Coder.encodeLines(outputStream.toByteArray()));
            } catch (Exception e) {
                throw new IOException("Unable to save item stack.", e);
            }
        }
        // Join all encoded items with a delimiter
        return String.join(";", encodedItems);
    }

    public static ItemStack[] decode(String data) throws IOException {
        String[] encodedItems = data.split(";");
        List<ItemStack> items = new ArrayList<>();
        for (String encoded : encodedItems) {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(encoded));
                BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
                ItemStack item = (ItemStack) dataInput.readObject();
                dataInput.close();
                items.add(item);
            } catch (Exception e) {
                return oldDecode(data); // Fallback to old decode method if there's an error
            }
        }
        return items.toArray(new ItemStack[0]);
    }

    public static ItemStack[] oldDecode(String data) throws IOException {
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
                removeID(container);
            } else {
                if(data.length() < 65535) { //check if the data isn't too large to be stored as nbt
                    container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, data);
                    removeID(container);

                } else {
                    int newID = 1;
                    if(!container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "id"))) {
                        if (!StorageConfig.get().getKeys(false).isEmpty()) { //if the config is empty, don't do this, else it throws an error
                            Set<Integer> ids = StorageConfig.get().getKeys(false).stream()
                                    .map(Integer::parseInt)
                                    .collect(Collectors.toSet());
                            newID = Collections.max(ids) + 1; //get the max id from the config and add 1
                        }
                        container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "id"), PersistentDataType.INTEGER, newID);
                    } else {
                        newID = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "id"), PersistentDataType.INTEGER);
                    }
                    //item is too large, save it in the config file
                    container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, "data too big to be stored here");
                    StorageConfig.get().set(String.valueOf(newID), itemArray);
                    StorageConfig.save();
                }
            }
            backpack.setItemMeta(meta);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save items in backpack", e);
        }
    }

    private static void removeID(PersistentDataContainer container){
        if(container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "id"))) {
            StorageConfig.get().set(String.valueOf(container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "id"), PersistentDataType.INTEGER)), null);
            StorageConfig.save();
            container.remove(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "id"));
        }
    }

}
