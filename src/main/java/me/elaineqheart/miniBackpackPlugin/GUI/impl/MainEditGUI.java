package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.InventoryGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.other.input.AnvilGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.other.input.ChatInputListener;
import me.elaineqheart.miniBackpackPlugin.GUI.other.Sounds;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.Backpack;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class MainEditGUI extends InventoryGUI {

    private static final Map<Player, Backpack> activeDataOfCreatedBackpacks = new HashMap<>();


    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 27, "Edit Backpacks");
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # . # # # . # #",
                "# # # # # # # # #"
        },fillerItem());
        this.addButton(11, viewBackpacks());
        this.addButton(15, createNewBackpack());
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
    private InventoryButton viewBackpacks(){
        return new InventoryButton()
                .creator(player -> ItemManager.viewBackpacks)
                .consumer(event -> {
                    Sounds.enderChest(event);
                    MiniBackpackPlugin.getGUIManager().openGUI(new ViewAllBackpacksGUI(), (Player) event.getWhoClicked());
                });
    }
    private InventoryButton createNewBackpack(){
        return new InventoryButton()
                .creator(player -> ItemManager.createNewBackpack)
                .consumer(event -> {
                    Sounds.click(event);
                    AnvilGUI slotInput = (player, input) -> {
                        //test if it is an integer between 1 and 54
                        if(!input.matches("\\d+") || input.equals("0") || Integer.parseInt(input) > 54){
                            player.sendMessage("Please enter a valid number of slots between 1 and 54.");
                            player.closeInventory();
                            activeDataOfCreatedBackpacks.remove(player);
                            return;
                        }
                        Backpack data = activeDataOfCreatedBackpacks.get(player);
                        data.slots = Integer.parseInt(input);
                        activeDataOfCreatedBackpacks.put(player,data);
                        ChatInputListener.addActivePlayerTextureInput(player,data);
                        player.closeInventory();
                        // ask to type in chat
                    };
                    AnvilGUI nameInput = (player, input) -> {
                        String formalizedInput = ItemManager.toTitleCase(input);
                        MiniBackpackPlugin.getSearchGUI().open("Backpack Slots [1-54]",ItemManager.emptyPaper, (Player) event.getWhoClicked(), slotInput);
                        Backpack data = new Backpack();
                        data.name = formalizedInput;
                        activeDataOfCreatedBackpacks.put(player, data);
                    };

                    MiniBackpackPlugin.getSearchGUI().open("The Item name", ItemManager.emptyPaper, (Player) event.getWhoClicked(), nameInput);
                });
    }

}
