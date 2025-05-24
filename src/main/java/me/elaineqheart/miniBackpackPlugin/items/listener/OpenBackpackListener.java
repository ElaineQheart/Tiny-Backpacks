package me.elaineqheart.miniBackpackPlugin.items.listener;

import me.elaineqheart.miniBackpackPlugin.GUI.impl.BackpackGUI;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class OpenBackpackListener implements Listener {

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        ItemStack item = event.getItem();
        if(item==null) return;
        ItemMeta meta = item.getItemMeta();
        if(meta==null) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if(container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items")) && container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"))) {
            event.setCancelled(true);
            int slots = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER);
            if(slots > 54) throw new RuntimeException("Slots of the backpack cannot be more than 54");

            // Open the backpack GUI here
            String name;
            if(meta.hasDisplayName()) {
                name = meta.getDisplayName();
            } else {
                name = meta.getItemName();
            }
            MiniBackpackPlugin.getGuiManager().openGUI(item, new BackpackGUI(item,name,slots), event.getPlayer());
        }

    }

}
