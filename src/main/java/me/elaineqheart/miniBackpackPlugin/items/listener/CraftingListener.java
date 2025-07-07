package me.elaineqheart.miniBackpackPlugin.items.listener;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.BackpackNote;
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

import java.util.Objects;

public class CraftingListener implements Listener {

    //ItemStack item = event.getInventory().getItem(5); //item in the middle of the crafting grid
    //this doesn't work as a shaped recipe can be anywhere in the crafting grid

    @EventHandler
    public static void onCraftPreserveItems(PrepareItemCraftEvent event) {
        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD) {
                if (ItemManager.isBackpack(item)) {
                    String itemName = Objects.requireNonNull(item.getItemMeta()).getItemName();
                    if(!ItemManager.craftingUpgrades.containsKey(ItemManager.toDataCase(itemName))) {
                        event.getInventory().setResult(null); //cancel the crafting if there is no upgrade for the item
                        return;
                    }
                    for (String upgradeItemName : ItemManager.craftingUpgrades.get(ItemManager.toDataCase(itemName))) { //the expected result item name
                        if(upgradeItemName == null) return;
                        ItemStack result = ItemManager.getBackpackFromName(ItemManager.toDataCase(upgradeItemName));
                        BackpackNote note = ItemManager.getBackpackNoteFromItem(result);
                        if(note == null) return;
                        //check which possible upgrade is the result of the crafting grid
                        if (isValidRecipe(note,event)) {
                            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                            String data = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING); //copy the items
                            ItemMeta resultMeta = result.getItemMeta(); //this way you can upgrade backpacks and the items will be transferred to the new backpack
                            assert resultMeta != null;
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

    private static boolean isValidRecipe(BackpackNote note, PrepareItemCraftEvent event) {
        String backpackName = ItemManager.toTitleCase(note.craftingMaterials[4]); //name of the backpack in the middle of the crafting grid
        int index = -1;
        for(int i = 0; i < 9; i++) { // 0 slot is the result slot, 1-9 are the crafting slots
            ItemStack test = event.getInventory().getItem(i+1);
            if(test == null || test.getItemMeta() == null) continue;
            if(ItemManager.isBackpack(test) && test.getItemMeta().getItemName().equals(backpackName)) index = i;
        }
        if(index == -1) return false; //backpack not found
        int indexRow = index / 3;
        int indexColumn = index % 3;
        indexRow--;
        indexColumn--;
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int currentIndex = (indexRow + i) * 3 + (indexColumn + j);
                ItemStack item = event.getInventory().getItem(currentIndex+1); //+1 because the first slot is the result slot
                if (indexRow + i < 0 || indexRow + i >= 3 || indexColumn + j < 0 || indexColumn + j >= 3) {
                    if (item != null) return false; //recipe is invalid if the item isn't air in an unspecified slot
                    if(!note.craftingMaterials[i*3 + j].equals("air")) return false;
                    int row = indexRow + i; //check the other side of the crafting grid in case it's a small recipe not in the center of the crafting grid
                    if(indexRow + i < 0) {
                        row = 2;
                    } else if (indexRow + i >= 3) {
                        row = 0;
                    }
                    int column = indexColumn + j;
                    if(indexColumn + j >= 3) {
                        column = 0;
                    } else if (indexColumn + j < 0) {
                        column = 2;
                    }
                    if(event.getInventory().getItem(row*3 + column + 1) != null) return false;
                    continue;
                }
                if (index == currentIndex) continue; //skip the backpack item itself
                if (item == null){
                    if(!note.craftingMaterials[i*3 + j].equals("air")) return false;
                } else if (!item.getType().equals(Material.matchMaterial(note.craftingMaterials[i*3 + j]))) {
                    return false; //recipe is invalid if the item isn't the expected material
                }
            }
        }
        return true;
    }

}
