package me.elaineqheart.miniBackpackPlugin.items.listener;

import me.elaineqheart.miniBackpackPlugin.GUI.impl.Backpack;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import me.elaineqheart.miniBackpackPlugin.items.ItemStackConverter;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;

public class OpenBackpackListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        ItemStack item = event.getItem();
        if(item==null) return;
        ItemMeta meta = item.getItemMeta();
        if(meta==null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if(container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items")) && container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"))) {
            event.setCancelled(true);
            int slots = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER);
            if(slots == 0) { //if the slots are 0, it means that the backpack should be deleted
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK,1,1);
                String data = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING);
                ItemStack[] items;
                try {
                    items = ItemStackConverter.decode(data);
                } catch (IOException e) {
                    item.setAmount(0);
                    return;
                }
                for(ItemStack i : items) {
                    event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), i);
                }
                item.setAmount(0); // Remove the backpack item from the player's inventory
                return;
            }
            if(slots > 54) throw new RuntimeException("Slots of the backpack cannot be more than 54");
            // Test if backpack exists
            if(!ItemManager.checkIfBackpackExists(meta.getItemName(), slots, item, event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + "This backpack has been modified or removed by an admin.");
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Right click the backpack again to drop the items inside it.");
                container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER, 0);
                item.setItemMeta(meta);
                return;
            }
            //check if the slots are more than the config slots
            String dataName = ItemManager.toDataCase(meta.getItemName());
            int configSlots = MiniBackpackPlugin.getPlugin().getConfig().getInt(dataName + ".slots");
            if(configSlots > slots) {
                event.getPlayer().sendMessage(ChatColor.AQUA + "The amount of slots of this backpack has been changed by an admin.");
                container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER, configSlots);
                item.setItemMeta(meta);
                slots = configSlots;
            }
            //get the display name of the item
            String name;
            if(meta.hasDisplayName()) {
                name = meta.getDisplayName();
            } else {
                name = meta.getItemName();
            }
            MiniBackpackPlugin.getStorageGUIManager().openGUI(event.getPlayer().getInventory().getHeldItemSlot(), item, new Backpack(item,name,slots), event.getPlayer());
        }

    }

}
