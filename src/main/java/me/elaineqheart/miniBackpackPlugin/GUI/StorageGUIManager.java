package me.elaineqheart.miniBackpackPlugin.GUI;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.ItemStackConverter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        //prhibited items are the backpack itself, battier blocks and shulker boxes
        if(ItemManager.isProhibitedItem(event.getCurrentItem())) {
            //prevent the player from taking the item
            handler.onClick(event);
        }
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

}