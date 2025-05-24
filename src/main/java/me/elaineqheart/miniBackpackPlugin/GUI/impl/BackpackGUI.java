package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.InventoryGUI;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.ItemStackConverter;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;

public class BackpackGUI extends InventoryGUI {

    private final ItemStack item;
    private final int slots;
    private final int inventorySize;

    public BackpackGUI(ItemStack item, String name, int slots) {
        super(((slots-1)/9+1)*9, name);
        this.item = item;
        this.slots = slots;
        this.inventorySize = ((slots-1)/9+1)*9;
    }

    @Override
    protected Inventory createInventory(int size, String name) {
        return Bukkit.createInventory(null,size,name);
    }

    @Override
    public void decorate(Player player) {
        int row = inventorySize/9;
        int sumOfLockedSlots = (inventorySize-slots);
        for(int i = 1; i <= sumOfLockedSlots; i++){
            this.addButton(row*9-i, lockedSlot());
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
        ItemStack[] items;
        try {
            items = ItemStackConverter.decode(data);
        } catch (IOException e) {
            throw new RuntimeException("ERROR in BackpackGUI. Unable to decode the itemdata in the backpack.", e);
        }
        int i = 0;
        while (i < items.length) {
            this.addButton(i, itemButton(items[i]));
            i++;
        }
    }

}
