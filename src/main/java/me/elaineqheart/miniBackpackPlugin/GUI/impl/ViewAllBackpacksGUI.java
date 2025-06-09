package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.InventoryGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.other.Sounds;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.Backpack;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ViewAllBackpacksGUI extends InventoryGUI {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Click on a Backpack to edit it");
    }

    @Override
    public void decorate(Player player) {
        fillOutItems();
        this.addButton(53, backArrow());
        super.decorate(player);
    }

    private void fillOutItems(){
        int size = ItemManager.backpacks.size();
        for(int i = 0; i < 54; i++){
            if(size-1<i)break;
            ItemStack item = ItemManager.backpacks.get(i).clone();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setLore(List.of(ChatColor.GRAY + "Slots: " + ItemManager.getSlotsFromItem(item)));
            item.setItemMeta(meta);
            this.addButton(i,backpackItem(item));
        }
    }

    private InventoryButton backArrow(){
        return new InventoryButton()
                .creator(player -> ItemManager.backArrow)
                .consumer(event -> {
                    Sounds.closeEnderChest(event);
                    MiniBackpackPlugin.getGUIManager().openGUI(new MainEditGUI(), (Player) event.getWhoClicked());
                });
    }

    private InventoryButton backpackItem(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    Backpack data = ItemManager.getBackpackFromItem(event.getCurrentItem());
                    MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), (Player) event.getWhoClicked());
                });
    }

}
