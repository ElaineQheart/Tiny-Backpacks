package me.elaineqheart.miniBackpackPlugin.GUI.other.input;

import me.elaineqheart.miniBackpackPlugin.GUI.impl.EditBackpackGUI;
import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import me.elaineqheart.miniBackpackPlugin.items.BackpackNote;
import me.elaineqheart.miniBackpackPlugin.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

public class ChatInputListener implements Listener {

    private static final Map<Player, BackpackNote> activePlayerTextureInput = new HashMap();
    public static void addActivePlayerTextureInput(Player player, BackpackNote data) {
        player.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
        player.sendMessage(ChatColor.YELLOW + "Please enter the texture URL for the backpack in chat:");
        player.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
        activePlayerTextureInput.put(player,data);
    }

    @EventHandler
    public static void onChatInput(AsyncPlayerChatEvent event) {
        if(activePlayerTextureInput.containsKey(event.getPlayer())) {
            event.setCancelled(true);
            String input = event.getMessage();
            if(input.equalsIgnoreCase("cancel")) {
                event.getPlayer().sendMessage("Cancelled the texture input. You can now talk in chat again.");
                activePlayerTextureInput.remove(event.getPlayer());
                return;
            }
            try {
                PlayerProfile skullProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = skullProfile.getTextures();
                textures.setSkin(URI.create(input).toURL());
            } catch (MalformedURLException | IllegalArgumentException e) {
                event.getPlayer().sendMessage(ChatColor.RED + "You must enter a valid texture URL.");
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Try again. To leave the texture input, type 'cancel'.");
                return;
            }
            BackpackNote data = activePlayerTextureInput.get(event.getPlayer());
            data.texture = input;
            activePlayerTextureInput.remove(event.getPlayer());
            Bukkit.getScheduler().runTaskLater(MiniBackpackPlugin.getPlugin(), () -> {
                ItemManager.safeBackpackData(data,false);
                MiniBackpackPlugin.getGUIManager().openGUI(new EditBackpackGUI(data), event.getPlayer());
            }, 1);
        }
    }

}
