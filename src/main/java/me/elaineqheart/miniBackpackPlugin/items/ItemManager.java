package me.elaineqheart.miniBackpackPlugin.items;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class ItemManager implements Listener {

    public static List<ItemStack> backpacks = new ArrayList<>();
    public static ItemStack barrier;

    //private static final String TINY_BACKPACK_TEXTURE = "http://textures.minecraft.net/texture/9165ee13a606e1b44695af46c39b52ce66657a4c4a623d0b282a7b8ce0509404";
    //private static final String SMALL_BACKPACK_TEXTURE = "http://textures.minecraft.net/texture/2308bf5cc3e9decaf0770c3fdad1e042121cf39cc2505bbb866e18c6d23ccd0c";


    public static void init() {
        Bukkit.getPluginManager().registerEvents(new ItemManager(), MiniBackpackPlugin.getPlugin());
        createBackpacks();
//        addCraftingRecipes();
        createBarrier();
    }
    private static void createBackpacks() {
        if (MiniBackpackPlugin.getPlugin().getConfig().getConfigurationSection("") == null) return;
        Set<Integer> recordedSlots = new HashSet<>();
        for(String itemName : MiniBackpackPlugin.getPlugin().getConfig().getConfigurationSection("").getKeys(false)) {
            String texture = MiniBackpackPlugin.getPlugin().getConfig().getString(itemName + ".texture");
            ItemStack item = makeSkull(texture);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setItemName(toTitleCase(itemName));
            meta.setMaxStackSize(1);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, "");
            int slots = MiniBackpackPlugin.getPlugin().getConfig().getInt(itemName + ".slots");
            //there shouldn't be two backpacks with the same slot count
            if(recordedSlots.contains(slots)) throw new RuntimeException("Duplicate slot count found: " + slots + " for item: " + itemName);
            container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER, slots);
            recordedSlots.add(slots);

            item.setItemMeta(meta);
            backpacks.add(item);
        }
    }
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;

        return Arrays.stream(input.split("_"))
                .filter(word -> !word.isEmpty())
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private static void createBarrier() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Locked Slot");

        item.setItemMeta(meta);
        barrier = item;
    }

    private static ItemStack makeSkull(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        PlayerProfile skullProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = skullProfile.getTextures();
        try {
            textures.setSkin(URI.create(url).toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        skullProfile.setTextures(textures);
        assert skullMeta != null;
        skullMeta.setOwnerProfile(skullProfile);
        skull.setItemMeta(skullMeta);
        return skull;
    }

//    private static void addCraftingRecipes() {
//        ShapedRecipe tinyBackpackRecipe = new ShapedRecipe(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "tiny_backpack"), tinyBackpack);
//        tinyBackpackRecipe.shape(" S ", "L L", " L ");
//        tinyBackpackRecipe.setIngredient('S', Material.STRING);
//        tinyBackpackRecipe.setIngredient('L', Material.LEATHER);
//        Bukkit.addRecipe(tinyBackpackRecipe);
//        ShapedRecipe smallBackpackRecipe = new ShapedRecipe(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "small_backpack"), smallBackpack);
//        smallBackpackRecipe.shape(" L ", "LBL", " L ");
//        smallBackpackRecipe.setIngredient('L', Material.LEATHER);
//        smallBackpackRecipe.setIngredient('B', Material.PLAYER_HEAD);
//        Bukkit.addRecipe(smallBackpackRecipe);
//    }

//    @EventHandler
//    public static void onCraft(PrepareItemCraftEvent event) {
//        ItemStack item = event.getInventory().getItem(5); //item in the middle of the crafting grid
//        if(item == null) return;
//        if(item.getType() == Material.PLAYER_HEAD) {
//            ItemMeta meta = item.getItemMeta();
//            if(meta == null) return;
//            PersistentDataContainer container = meta.getPersistentDataContainer();
//            if(container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"))) {
//                if(Objects.equals(container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER), 3)) {
//                    ItemStack result = event.getInventory().getResult();
//                    if(result == null) return;
//                    assert result.getItemMeta() != null;
//                    String data = container.get(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING);
//                    ItemMeta resultMeta = result.getItemMeta();
//                    assert data != null;
//                    resultMeta.getPersistentDataContainer().set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING, data);
//                    result.setItemMeta(resultMeta);
//                    event.getInventory().setResult(result);
//                    return;
//                }
//            }
//            event.getInventory().setResult(null);
//        }
//    }

    public static boolean isBackpack(ItemStack item) {
        if(item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING);
    }

}
