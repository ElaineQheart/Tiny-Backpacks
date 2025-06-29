package me.elaineqheart.miniBackpackPlugin.items.listener;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CraftingListener implements Listener {

    //ItemStack item = event.getInventory().getItem(5); //item in the middle of the crafting grid
    //this doesn't work as a shaped recipe can be anywhere in the crafting grid

    @EventHandler
    public static void onCraftPreserveItems(PrepareItemCraftEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return;
                String itemName = meta.getItemName();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"))) {
                    ItemStack result = event.getInventory().getResult(); //the result
                    if (result == null || result.getItemMeta() == null) return;
                    if(!ItemManager.craftingUpgrades.containsKey(ItemManager.toDataCase(itemName))) {
                        event.getInventory().setResult(null); //cancel the crafting
                        continue;
                    }
                    for (String upgradeItemName : ItemManager.craftingUpgrades.get(ItemManager.toDataCase(itemName))) { //the expected result item name
                        //check which possible upgrade is the result of the crafting grid
                        if (upgradeItemName != null && upgradeItemName.equals(ItemManager.toDataCase(result.getItemMeta().getItemName()))) {

                            String data = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING); //copy the items
                            ItemMeta resultMeta = result.getItemMeta(); //this way you can upgrade backpacks and the items will be transferred to the new backpack
                            assert data != null;
                            resultMeta.getPersistentDataContainer().set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, data);
                            result.setItemMeta(resultMeta);
                            event.getInventory().setResult(result);
                            return;
                        }
                    }

                }
                event.getInventory().setResult(null); //set the result null if it's not a backpack and contains a player head
            }
        }
    }

}
