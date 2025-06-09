package me.elaineqheart.miniBackpackPlugin.GUI.impl;


import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.InventoryGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.other.Sounds;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.BackpackNote;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class UpgradeGUI extends InventoryGUI {

    BackpackNote data;

    public UpgradeGUI(BackpackNote data) {
        super();
        this.data = data;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 54, "Click on a Backpack to select it");
    }

    @Override
    public void decorate(Player player) {
        fillOutItems();
        this.addButton(53, backArrow());
        super.decorate(player);
    }

    private void fillOutItems(){
        int size = ItemManager.backpacks.size();
        int j = 0; //counter for skipped backpacks
        for(int i = 0; i < 54; i++){
            if(size-1<i) {
                this.addButton(i-j,deselectUpgrade());
                break;
            };
            ItemStack item = ItemManager.backpacks.get(i).clone();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER) > data.slots //filter out bigger backpacks
                    || data.name.equals(meta.getItemName())) { // skip the current backpack
                j++;
                continue; //skip the backpacks with a higher slot count
            }
            meta.setLore(List.of(ChatColor.GRAY + "Slots: " + ItemManager.getSlotsFromItem(item)));
            item.setItemMeta(meta);
            this.addButton(i-j,backpackItem(item));
        }
    }

    private InventoryButton backArrow(){
        return new InventoryButton()
                .creator(player -> ItemManager.backArrow)
                .consumer(event -> {
                    Sounds.click(event);
                    MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), (Player) event.getWhoClicked());
                });
    }

    private InventoryButton backpackItem(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    ItemMeta meta = item.getItemMeta();
                    assert meta != null;
                    data.hasUpgradeBackpack = true;
                    data.setMaterial(4, ItemManager.toDataCase(meta.getItemName()));
                    ItemManager.safeBackpackData(data,true);
                    MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), (Player) event.getWhoClicked());
                });
    }
    private InventoryButton deselectUpgrade(){
        return new InventoryButton()
                .creator(player -> ItemManager.deselectUpgrade)
                .consumer(event -> {
                    Sounds.click(event);
                    if(data.hasUpgradeBackpack) {
                        data.setMaterial(4,"air");
                        data.hasUpgradeBackpack = false;
                    }
                    ItemManager.safeBackpackData(data,true);
                    MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), (Player) event.getWhoClicked());
                });
    }

}

