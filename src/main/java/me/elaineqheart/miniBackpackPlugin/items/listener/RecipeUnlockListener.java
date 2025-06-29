package me.elaineqheart.miniBackpackPlugin.items.listener;

import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;

public class RecipeUnlockListener implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player p) {
            Material itemType = event.getItem().getItemStack().getType();
            if(!ItemManager.unlockRecipes.containsKey(itemType)) return;

            p.discoverRecipes(ItemManager.unlockRecipes.get(itemType)); // Unlock the recipe for the player
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Player p = (Player) event.getWhoClicked();
        Material itemType = event.getRecipe().getResult().getType();
        if (ItemManager.unlockRecipes.containsKey(itemType) && itemType!=Material.PLAYER_HEAD) p.discoverRecipes(ItemManager.unlockRecipes.get(itemType));
        //the crafted material could theoretically also be a player head (ex. a backpack)
        if(event.getRecipe().getResult().getItemMeta() == null) return;
        String itemName = ItemManager.toDataCase(event.getRecipe().getResult().getItemMeta().getItemName());
        if (ItemManager.craftingUpgrades.containsKey(itemName)) {
            for(String upgradeBackpackName : ItemManager.craftingUpgrades.get(itemName)) {
                p.discoverRecipes(ItemManager.unlockRecipesByBackpack.get(upgradeBackpackName));
            }
        }

    }

}
