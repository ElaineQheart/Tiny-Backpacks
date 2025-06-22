package me.elaineqheart.miniBackpackPlugin.GUI.impl;

import me.elaineqheart.miniBackpackPlugin.GUI.InventoryButton;
import me.elaineqheart.miniBackpackPlugin.GUI.InventoryGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.other.Sounds;
import me.elaineqheart.miniBackpackPlugin.GUI.other.input.AnvilGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.other.input.ChatInputListener;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.BackpackNote;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class EditBackpackGUI extends InventoryGUI {

    BackpackNote data;

    public EditBackpackGUI(BackpackNote data) {
        super();
        this.data = data;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 36, "Edit Backpack");
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# . # # . # # . #",
                "# . # . . . # # #",
                "# # # # # # # # ."
        },fillerItem());
        this.addButton(10, craftingTable());
        {
            ItemStack item = data.toItemStack().clone();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setLore(List.of(ChatColor.GRAY + "Slots: " + ItemManager.getSlotsFromItem(item)));
            item.setItemMeta(meta);
            this.addButton(13, backpack(item));
        }
        this.addButton(16, delete());
        {
            ItemStack upgradeItem = ItemManager.upgradeBackpack;
            if (data.hasUpgradeBackpack) {
                upgradeItem = ItemManager.getBackpackFromName(data.craftingMaterials[4]).clone();
                ItemMeta meta = upgradeItem.getItemMeta();
                meta.setItemName(ChatColor.GREEN + meta.getItemName());
                meta.setLore(List.of(ChatColor.GRAY + "This is the current upgrade material.", "",
                        ChatColor.GRAY + "You can choose a backpack with less",
                        ChatColor.GRAY + "slots than the current one to be an",
                        ChatColor.GRAY + "upgrade material for this backpack.",
                        "",
                        ChatColor.YELLOW + "Click to remove or change this"));
                upgradeItem.setItemMeta(meta);
            }
            this.addButton(19, upgradeTemplate(upgradeItem));
        }
        this.addButton(21, editName());
        this.addButton(22, editSlots());
        this.addButton(23, editTexture());
        if(data.slots <= 5) {
            if(data.isHopperSized) {
                this.addButton(25, hopperSized(ItemManager.hopperSized));
            } else {
                this.addButton(25, hopperSized(ItemManager.chestSized));
            }
        }
        this.addButton(35, back());
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
    private InventoryButton craftingTable(){
        return new InventoryButton()
                .creator(player -> ItemManager.craftingTable)
                .consumer(event -> {
                    Sounds.click(event);
                    ItemStack upgradeItem = null;
                    for(ItemStack testBackpack : ItemManager.backpacks) {
                        if(testBackpack.getItemMeta() != null && data.craftingMaterials != null &&
                                ItemManager.toDataCase(testBackpack.getItemMeta().getItemName()).equals(data.craftingMaterials[4])) {
                            upgradeItem = testBackpack; //get the crafting ingredient backpack
                            break;
                        }
                    }
                    Player p = (Player) event.getWhoClicked();
                    MiniBackpackPlugin.getStorageGUIManager().openGUI(4, upgradeItem,new CraftingTableGUI(upgradeItem,data), p);

                });
    }
    private InventoryButton upgradeTemplate(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    MiniBackpackPlugin.getGUIManager().openGUI(new UpgradeGUI(data), (Player) event.getWhoClicked());
                });
    }
    private InventoryButton back(){
        return new InventoryButton()
                .creator(player -> ItemManager.backArrow)
                .consumer(event -> {
                    Sounds.click(event);
                    MiniBackpackPlugin.getGUIManager().openGUI(new ViewAllBackpacksGUI(), (Player) event.getWhoClicked());
                });
    }
    private InventoryButton delete() {
        return new InventoryButton()
                .creator(player -> ItemManager.deleteButton)
                .consumer(event -> {
                    Sounds.click(event);
                    MiniBackpackPlugin.getGUIManager().openGUI(new ConfirmDeleteGUI(data), (Player) event.getWhoClicked());
                });
    }
    private InventoryButton hopperSized(ItemStack item) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    data.isHopperSized = !data.isHopperSized;
                    ItemManager.safeBackpackData(data,false);
                    MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), (Player) event.getWhoClicked());
                });
    }
    private InventoryButton editName() {
        return new InventoryButton()
                .creator(player -> ItemManager.editName)
                .consumer(event -> {
                    Sounds.click(event);
                    AnvilGUI nameInput = (player, input) -> {
                        String formalizedInput = ItemManager.toTitleCase(input);
                        ItemManager.deleteBackpack(data);
                        data.name = formalizedInput;
                        ItemManager.safeBackpackData(data,false);
                        MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), player);
                    };
                    MiniBackpackPlugin.getSearchGUI().open("The Item name", ItemManager.emptyPaper, (Player) event.getWhoClicked(), nameInput);
                });
    }
    private InventoryButton editSlots() {
        return new InventoryButton()
                .creator(player -> ItemManager.editSlots)
                .consumer(event -> {
                    Sounds.click(event);
                    AnvilGUI slotInput = (player, input) -> {
                        //test if it is an integer between 1 and 54
                        if(!input.matches("\\d+") || input.equals("0") || Integer.parseInt(input) > 54){
                            player.sendMessage("Please enter a valid number of slots between 1 and 54.");
                            MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), player);
                            return;
                        }
                        int desiredSlots = Integer.parseInt(input);
                        if(ItemManager.minSlots.get(data.name) != null && desiredSlots < ItemManager.minSlots.get(data.name)) {
                            player.sendMessage("The amount of slots cannot be less than " + ItemManager.minSlots.get(data.name) + ".");
                            player.sendMessage("This is because this backpack is used as an upgrade for another backpack with that many slots.");
                            MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), player);
                            return;
                        }
                        if(ItemManager.maxSlots.get(data.name) != null && desiredSlots > ItemManager.maxSlots.get(data.name)) {
                            player.sendMessage("The amount of slots cannot be more than " + ItemManager.maxSlots.get(data.name) + ".");
                            player.sendMessage("This is because there is a backpack who uses this one as an upgrade with that many slots.");
                            MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), player);
                            return;
                        }
                        data.slots = desiredSlots;
                        if(data.slots > 5) {
                            data.isHopperSized = false;
                        }
                        ItemManager.safeBackpackData(data,false);
                        MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), player);
                        // ask to type in chat
                    };
                    MiniBackpackPlugin.getSearchGUI().open("Backpack Slots [1-54]",ItemManager.emptyPaper, (Player) event.getWhoClicked(), slotInput);
                });
    }
    private InventoryButton editTexture() {
        return new InventoryButton()
                .creator(player -> ItemManager.editTexture)
                .consumer(event -> {
                    Sounds.click(event);
                    Player p = (Player) event.getWhoClicked();
                    ChatInputListener.addActivePlayerTextureInput(p, data);
                    p.closeInventory();
                });
    }


}
