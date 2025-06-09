package me.elaineqheart.miniBackpackPlugin.GUI.backpackManagers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class StorageGUIListener implements Listener {

    private final StorageGUIManager guiManager;

    public StorageGUIListener(StorageGUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        this.guiManager.handleClick(event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        this.guiManager.handleDrag(event);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        this.guiManager.handleOpen(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.guiManager.handleClose(event);
    }
}