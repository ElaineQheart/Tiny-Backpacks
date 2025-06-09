package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.backpackManagers.StorageInventoryGUI;
import me.elaineqheart.miniBackpackPlugin.items.BackpackNote;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CraftingTableGUI extends StorageInventoryGUI{

    private static final HashMap<Player, ItemStack[]> hotbar = new HashMap<>();
    public static final HashMap<Player, BackpackNote> backpackData = new HashMap<>();
    private final ItemStack item;
    private final BackpackNote data;

    public CraftingTableGUI(ItemStack item, BackpackNote data) {
        super(0, null);
        this.item = item;
        this.data = data;
    }

    @Override
    protected Inventory createInventory(int size, String name) {
        return Bukkit.createInventory(null, InventoryType.CRAFTER, "Edit Crafting Recipe");
    }

    @Override
    public void decorate(Player player) {
        ItemStack[] hotbarItems = new ItemStack[9];
        for(int i = 0; i < 9; i++) {
            hotbarItems[i] = player.getInventory().getItem(i);
        }
        for(int i = 3; i < 8; i++) {
            player.getInventory().setItem(i, ItemManager.fillerItem);
        }
        hotbar.put(player, hotbarItems); //save the items in the hotbar
        backpackData.put(player, data); //save the backpack data

        player.getInventory().setItem(8, ItemManager.backArrow);
        player.getInventory().setItem(0, ItemManager.craftingInfo1);
        player.getInventory().setItem(1, ItemManager.craftingInfo2);
        player.getInventory().setItem(2, ItemManager.craftingInfo3);

        decorateCraftingMaterials();

        super.decorate(player);
    }

    private void decorateCraftingMaterials() {
        if(data == null || data.craftingMaterials == null) return;
        for(int i = 0; i < 9; i++) {
            if(i==4 && item != null) {
                this.addButton(i, itemButton(item));
            } else {
                String namespacedMaterial = data.craftingMaterials[i];
                Material material = Material.matchMaterial(namespacedMaterial);
                if(material==null) continue;
                this.addButton(i,itemButton(new ItemStack(material)));
            }
        }
    }

    private InventoryButton itemButton(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {});
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        backpackData.remove(player);
        ItemStack[] hotbarItems = hotbar.get(player);
        List<String> craftingMaterials = new ArrayList<>();
        boolean hasRecipe = false;
        for(int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, hotbarItems[i]);
            ItemStack item = event.getInventory().getItem(i);
            if (i == 4 && this.item != null && this.item.getItemMeta() != null) {
                craftingMaterials.add(ItemManager.toDataCase(this.item.getItemMeta().getItemName()));
                hasRecipe = true;
                // center slot
            } else if (item == null) {
                craftingMaterials.add("air");
            } else {
                craftingMaterials.add(item.getType().toString().toLowerCase());
                hasRecipe = true;
            }
        }
        this.data.craftingMaterials = hasRecipe ? craftingMaterials.toArray(new String[9]) : null;

        ItemManager.safeBackpackData(data,true);

    }

}
