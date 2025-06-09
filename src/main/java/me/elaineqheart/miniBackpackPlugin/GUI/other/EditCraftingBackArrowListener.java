package me.elaineqheart.miniBackpackPlugin.GUI.other;

import me.elaineqheart.miniBackpackPlugin.GUI.impl.CraftingTableGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.impl.EditBackpackGUI;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.BackpackNote;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EditCraftingBackArrowListener implements Listener {

    @EventHandler
    public void onBackArrowClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (!ItemManager.backArrow.equals(item)) return;
        event.setCancelled(true); // cancel the event
        Player player = (Player) event.getWhoClicked();
        BackpackNote data = CraftingTableGUI.backpackData.get(player);
        if(data == null) return;
        player.closeInventory();
        MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), player);
    }

}
