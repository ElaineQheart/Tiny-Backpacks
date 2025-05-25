package me.elaineqheart.miniBackpackPlugin.GUI;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.ItemStackConverter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//https://www.spigotmc.org/threads/a-modern-approach-to-inventory-guis.594005/

//This is a manager class so it will be treated as a singleton. This means we only create one single
//instance of this class and no more.

public class StorageGUIManager {

    private ItemStack item;
    private final Map<Inventory, InventoryHandler> activeInventories = new HashMap<>();

    public void openGUI(ItemStack item, InventoryGUI gui, Player player) {
        this.registerHandledInventory(item, gui.getInventory(), gui);
        player.openInventory(gui.getInventory());
    }

    public void registerHandledInventory(ItemStack item, Inventory inventory, InventoryHandler handler) {
        this.item = item;
        this.activeInventories.put(inventory,handler);
    }

    public void unregisterInventory(Inventory inventory) {
        this.activeInventories.remove(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler == null) return;
        preventClick(event, handler);
    }

    public void handleDrag(InventoryDragEvent event) {
        preventDrag(event);
    }

    public void handleOpen(InventoryOpenEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());

        if (handler != null){
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN,1,1);
            assert item.getItemMeta()!=null;
            item.getItemMeta().getPersistentDataContainer().set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING,"");
            handler.onOpen(event);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHandler handler = this.activeInventories.get(inventory);
        if (handler != null){
            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF,1,1);
            ItemStackConverter.updateItem(item,inventory.getContents());
            handler.onClose(event);
            this.unregisterInventory(inventory);
        }
    }

    private void preventClick(InventoryClickEvent event, InventoryHandler handler) {

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        ItemStack hotbarItem = null;

        if(Objects.equals(current, item) || Objects.equals(current, ItemManager.barrier)) { // completely disable clicks
            handler.onClick(event); //this cancels the event
            return;
        }
        if (event.isShiftClick() && isProhibited(current)) { // shift click
            handler.onClick(event);
            return;
        }
        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) return; //allow moving items in the main inventory

        if (event.getAction() == InventoryAction.HOTBAR_SWAP || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) { // kotkey swap
            int hotbarButton = event.getHotbarButton();
            hotbarItem = event.getWhoClicked().getInventory().getItem(hotbarButton);
        }
        if(event.getRawSlot() < 0) return;// allow dropping backpacks and shulkers
        if (isProhibited(current) || isProhibited(cursor) || isProhibited(hotbarItem)) {
            handler.onClick(event);
        }

    }
    private void preventDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        // Allow dragging within player's own inventory
        for (int slot : event.getRawSlots()) {
            if (slot < topInventory.getSize()) { //check if any of the drag slots are in the top inventory
                for (ItemStack item : event.getNewItems().values()) {
                    if (isProhibited(item)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                break;
            }
        }
    }
    private boolean isProhibited(ItemStack item) {
        if (item == null) return false;
        if (item.getType() == Material.SHULKER_BOX ||
                item.getType().name().endsWith("_SHULKER_BOX")) return true;
        return ItemManager.isBackpack(item);
    }

}