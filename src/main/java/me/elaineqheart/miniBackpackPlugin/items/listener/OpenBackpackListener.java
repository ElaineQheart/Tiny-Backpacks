package me.elaineqheart.miniBackpackPlugin.items.listener;

import me.elaineqheart.miniBackpackPlugin.GUI.impl.SmallBackpackGUI;
import me.elaineqheart.miniBackpackPlugin.GUI.impl.TinyBackpackGUI;
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

        if(container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "tinyBackpackItems"))) {
            event.setCancelled(true);
            String itemData = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "tinyBackpackItems"), PersistentDataType.STRING);



            // Open the backpack GUI here
            MiniBackpackPlugin.getGuiManager().openGUI(item, new TinyBackpackGUI(), event.getPlayer());
        }

        if(container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "smallBackpackItems"))) {
            event.setCancelled(true);
            String itemData = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "smallBackpackItems"), PersistentDataType.STRING);



            // Open the backpack GUI here
            MiniBackpackPlugin.getGuiManager().openGUI(item, new SmallBackpackGUI(), event.getPlayer());
        }

    }

}
