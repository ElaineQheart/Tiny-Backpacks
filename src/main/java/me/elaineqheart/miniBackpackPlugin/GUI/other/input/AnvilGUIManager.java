package me.elaineqheart.miniBackpackPlugin.GUI.other.input;

import me.elaineqheart.miniBackpackPlugin.GUI.other.Sounds;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashMap;
import java.util.Map;

public class AnvilGUIManager implements Listener{

    private static final Map<Inventory, AnvilGUI> activeInventories = new HashMap<>();

    public void open(String name, ItemStack item, Player player, AnvilGUI handler) {
        AnvilView view = MenuType.ANVIL.create(player, name);
        view.setMaximumRepairCost(0);
        view.setItem(0, item);
        registerHandledInventory(view.getTopInventory(),handler);
        player.openInventory(view);
    }

    public void registerHandledInventory(Inventory inventory, AnvilGUI handler) {
        activeInventories.put(inventory,handler);
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        AnvilGUI handler = activeInventories.get(event.getView().getTopInventory());
        if (handler == null) return;

        event.setCancelled(true);
        ItemStack paperItem = event.getInventory().getItem(0);
        AnvilView view = (AnvilView) event.getView();
        view.setRepairCost(0);

        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() != 2) return;
        ItemStack resultItem = event.getCurrentItem();
        if (resultItem == null) return;
        ItemMeta meta = resultItem.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            //remove the paper, else it will end up in the players inventory
            player.getOpenInventory().getTopInventory().remove(paperItem);
            String typedText = meta.getDisplayName();
            Sounds.click(event);
            handler.execute(player,ItemManager.toTitleCase(typedText));

        }
    }

    @EventHandler //also set the name formatted
    public void handleTyping(PrepareAnvilEvent event) {
        AnvilGUI handler = activeInventories.get(event.getView().getTopInventory());
        if (handler == null) return;

        ItemStack paperItem = event.getInventory().getItem(2);
        if (paperItem == null) return;
        ItemMeta meta = paperItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ItemManager.toTitleCase(meta.getDisplayName()));
        paperItem.setItemMeta(meta);
        //run task later to make sure the repair cost is set to 0 after the event is done
        Bukkit.getScheduler().runTaskLater(MiniBackpackPlugin.getPlugin(), () -> {
                    event.getView().setRepairCost(0);
                    event.getInventory().setItem(2,paperItem);
                },1);
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        AnvilGUI handler = activeInventories.get(event.getView().getTopInventory());
        if (handler == null) return;

        ItemStack paperItem = event.getInventory().getItem(0);
        Player p = (Player) event.getPlayer();
        //remove the paper, else it will end up in the players inventory
        assert paperItem != null;
        p.getOpenInventory().getTopInventory().remove(paperItem);
    }

}
