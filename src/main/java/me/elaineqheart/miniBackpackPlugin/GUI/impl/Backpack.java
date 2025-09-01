package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.backpackManagers.StorageInventoryGUI;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.data.ConfigManager;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.ItemStackConverter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.Objects;

public class Backpack extends StorageInventoryGUI {

    private final ItemStack item;
    private final int slots;
    private final int inventorySize;

    public Backpack(ItemStack item, String name, int slots) {
        super(slots, name);

        this.item = item;
        this.slots = slots;
        if(isHopperInventory(name)) {
            this.inventorySize = 5;
        } else {
            this.inventorySize = ((slots - 1) / 9 + 1) * 9;
        }
    }

    @Override
    protected Inventory createInventory(int slots, String name) {
        if(isHopperInventory(name)) {
            return Bukkit.createInventory(null, InventoryType.HOPPER, name);
        } else {
            int size = ((slots-1)/9+1)*9;
            return Bukkit.createInventory(null, size, name);
        }
    }

    private boolean isHopperInventory(String name) {
        return slots <= 5 && Objects.equals(MiniBackpackPlugin.getPlugin().getConfig().get(ItemManager.toDataCase(name) + ".type"), "hopper");
    }

    @Override
    public void decorate(Player player) {
        int sumOfLockedSlots = (inventorySize - slots);
        if(inventorySize==5) {
            for (int i = 1; i <= sumOfLockedSlots; i++) {
                this.addButton(5 - i, lockedSlot());
            }
        } else {
            int row = inventorySize / 9;
            for (int i = 1; i <= sumOfLockedSlots; i++) {
                this.addButton(row * 9 - i, lockedSlot());
            }
        }
        decorateItems();
        super.decorate(player);
    }

    private InventoryButton lockedSlot(){
        return new InventoryButton()
                .creator(player -> ItemManager.barrier)
                .consumer(event -> {});
    }

    private InventoryButton itemButton(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {});
    }

    private void decorateItems() {
        if(item.getItemMeta() == null) return;
        String data = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING);
        if(data == null || data.isEmpty()) return;
        ItemStack[] items = new ItemStack[0];
        if(data.equals("data too big to be stored here")) {
            items = ConfigManager.StorageConfig.get().getObject(String.valueOf(item.getItemMeta().getPersistentDataContainer().get(
                    new NamespacedKey(MiniBackpackPlugin.getPlugin(), "id"), PersistentDataType.INTEGER)), ItemStack[].class);
        } else {
            try {
                items = ItemStackConverter.decode(data);
            } catch (IOException e) {
                throw new RuntimeException("ERROR in BackpackGUI. Unable to decode the itemdata in the backpack.", e);
            }
        }
        int i = 0;
        if(items == null) {return;}
        while (i < items.length) {
            this.addButton(i, itemButton(items[i]));
            i++;
        }
    }

}
