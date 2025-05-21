package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.InventoryGUI;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class TinyBackpackGUI extends InventoryGUI {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,9,"Tiny Backpack");
    }

    @Override
    public void decorate(Player player) {
        this.addButton(0, lockedSlot());
        this.addButton(1, lockedSlot());
        this.addButton(2, lockedSlot());
        this.addButton(6, lockedSlot());
        this.addButton(7, lockedSlot());
        this.addButton(8, lockedSlot());
        super.decorate(player);
    }

    private InventoryButton lockedSlot(){
        return new InventoryButton()
                .creator(player -> ItemManager.barrier)
                .consumer(event -> {});
    }

}
