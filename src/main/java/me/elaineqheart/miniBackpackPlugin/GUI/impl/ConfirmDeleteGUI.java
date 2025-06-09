package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.InventoryGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.other.Sounds;
import me.elaineqheart.miniBackpackPlugin.items.Backpack;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfirmDeleteGUI extends InventoryGUI {

    Backpack data;

    public ConfirmDeleteGUI(Backpack data) {
        super();
        this.data = data;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,3*9,"Confirm Delete");
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # . # . # . # #",
                "# # # # # # # # #"
        },fillerItem());
        this.addButton(11, confirm());
        this.addButton(13, backpack(data.toItemStack()));
        this.addButton(15, cancel());
        super.decorate(player);
    }

    private void fillOutPlaces(String[] places, InventoryButton fillerItem){
        for(int i = 0; i < places.length; i++){
            for(int j = 0; j < places[i].length(); j+=2){
                if(places[i].charAt(j)=='#') {
                    this.addButton(i*9+j/2, fillerItem);
                }
            }
        }
    }

    private InventoryButton fillerItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }
    private InventoryButton backpack(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(Sounds::click);
    }
    private InventoryButton confirm(){
        return new InventoryButton()
                .creator(player -> ItemManager.confirm)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    p.closeInventory();
                    try {
                        ItemManager.deleteBackpack(data);
                    } catch (Exception e) {
                        p.sendMessage(ChatColor.RED + "An error occurred while deleting the backpack.");
                        return;
                    }
                    Sounds.experience(event);
                    p.sendMessage(ChatColor.YELLOW + "Backpack deleted successfully.");
                });
    }
    private InventoryButton cancel(){
        return new InventoryButton()
                .creator(player -> ItemManager.cancel)
                    .consumer(event -> {
                        Sounds.click(event);
                        Player p = (Player) event.getWhoClicked();
                        p.closeInventory();
                    });
    }

}
