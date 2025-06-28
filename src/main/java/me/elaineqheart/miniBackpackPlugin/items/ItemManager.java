package me.elaineqheart.miniBackpackPlugin.items;

import me.elaineqheart.miniBackpackPlugin.MiniBackpackPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
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

public class ItemManager{

    public static List<ItemStack> backpacks = new ArrayList<>();
    public static ItemStack barrier;
    public static ItemStack fillerItem;
    public static ItemStack viewBackpacks;
    public static ItemStack createNewBackpack;
    public static ItemStack backArrow;
    public static ItemStack emptyPaper;
    public static ItemStack craftingTable;
    public static ItemStack deleteButton;
    public static ItemStack confirm;
    public static ItemStack cancel;
    public static ItemStack editName;
    public static ItemStack editSlots;
    public static ItemStack editTexture;
    public static ItemStack chestSized;
    public static ItemStack hopperSized;
    public static ItemStack upgradeBackpack;
    public static ItemStack craftingInfo1;
    public static ItemStack craftingInfo2;
    public static ItemStack craftingInfo3;
    public static ItemStack deselectUpgrade;

    public static final HashMap<String,List<String>> craftingUpgrades = new HashMap<>(); //material backpack name, result backpack name(s) - can be multiple
    private static final HashMap<String,List<String>> craftingMaterials = new HashMap<>(); //backpack name, material list
    private static final Set<ShapedRecipe> recipeMap = new HashSet<>(); //backpack name, recipe
    public static final HashMap<String,Integer> maxSlots = new HashMap<>(); //backpack title name, max slots
    public static final HashMap<String,Integer> minSlots = new HashMap<>(); //backpack title name, min slots
    //private static final String TINY_BACKPACK_TEXTURE = "http://textures.minecraft.net/texture/9165ee13a606e1b44695af46c39b52ce66657a4c4a623d0b282a7b8ce0509404";
    private static final String SMALL_BACKPACK_TEXTURE = "http://textures.minecraft.net/texture/2308bf5cc3e9decaf0770c3fdad1e042121cf39cc2505bbb866e18c6d23ccd0c";
    public static final HashMap<Material, Collection<NamespacedKey>> unlockRecipes = new HashMap<>(); //material, recipes to unlock


    public static void init() {
        reloadBackpacks(false, true);
        createFillerItem();
        createViewBackpacks();
        createCreateNewBackpack();
        createBackArrow();
        createEmptyPaper();
        createCraftingTable();
        createDeleteButton();
        createConfirm();
        createCancel();
        createEditName();
        createEditSlots();
        createEditTexture();
        createChestSized();
        createHopperSized();
        createUpgradeBackpack();
        createAllCraftingInfo();
        createBarrier();
        createDeselectUpgrade();
    }

    public static void reloadBackpacks(boolean reloadRecipes, boolean addRecipes) {
        if(reloadRecipes) Bukkit.resetRecipes(); //reloads all recipes, because with Bukkit.removeRecipe, the recipe is not removed from a Crafter !!! BUG REPORT
        //but I'm too lazy to report this

//        for(ShapedRecipe recipe : recipeMap) {
//            Bukkit.removeRecipe(recipe.getKey());
//        }
        recipeMap.clear();
        craftingMaterials.clear();
        craftingUpgrades.clear();
        backpacks.clear();
        if (MiniBackpackPlugin.getPlugin().getConfig().getConfigurationSection("") == null) return;
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
            container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER, slots);

            item.setItemMeta(meta);
            backpacks.add(item);

            //adding crafting recipe
            ConfigurationSection path = MiniBackpackPlugin.getPlugin().getConfig().getConfigurationSection(itemName + ".recipe");
            if (path == null || path.getKeys(false).isEmpty()) continue; // no recipe for this backpack
            List<String> pattern = (path.getStringList("pattern")); //something like [" S ", "L L", " L "]
            Map<String, String> ingredientMap = new HashMap<>();
            for (String key : Objects.requireNonNull(path.getConfigurationSection("ingredients")).getKeys(false)) {
                String value = path.getString("ingredients." + key);
                ingredientMap.put(key, value);
            }
            NamespacedKey key = new NamespacedKey(MiniBackpackPlugin.getPlugin(), itemName);
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(pattern.toArray(new String[0]));
            boolean hasAnUpgrade = false;
            for (Map.Entry<String, String> entry : ingredientMap.entrySet()) {
                char symbol = entry.getKey().charAt(0);
                String namespacedMaterial = entry.getValue(); // e.g., "minecraft:string"

                // Convert string to Material safely
                Material material = Material.matchMaterial(namespacedMaterial);
                if (material != null) {
                    recipe.setIngredient(symbol, material);
                } else {
                    if(MiniBackpackPlugin.getPlugin().getConfig().getConfigurationSection(namespacedMaterial) != null && !hasAnUpgrade) {
                        hasAnUpgrade = true;
                        recipe.setIngredient(symbol, Material.PLAYER_HEAD);
                        //material backpack name, List.of(result backpack name)
                        if(!craftingUpgrades.containsKey(namespacedMaterial)) {
                            craftingUpgrades.put(namespacedMaterial, new ArrayList<>()); //make a new arrayList, if it doesn't exist yet
                        }
                        craftingUpgrades.get(namespacedMaterial).add(itemName); //store the upgrade backpack name
                        //the material backpack has a maximum of slots
                        //the result backpack has a minimum of slots, so that the upgraded backpack always has equally or more slots
                        minSlots.put(toTitleCase(itemName), MiniBackpackPlugin.getPlugin().getConfig().getInt(namespacedMaterial + ".slots"));
                        maxSlots.put(toTitleCase(namespacedMaterial), MiniBackpackPlugin.getPlugin().getConfig().getInt(itemName + ".slots"));
                    } else {
                        Bukkit.getLogger().warning("Invalid material \"" + namespacedMaterial + "\" for backpack: " + itemName);
                    }
                }
            }
            if(addRecipes) Bukkit.addRecipe(recipe);

            ingredientMap.put(" ", "air");
            List<String> transformedPattern = pattern.stream() //something like [air, leather, air, leather, tiny_backpack, leather, air, leather, air]
                    .flatMap(row -> row.chars()
                            .mapToObj(c -> ingredientMap.get(String.valueOf((char) c))))
                    .toList();
            craftingMaterials.put(itemName, transformedPattern); //store the crafting materials for this backpack

            for (ShapedRecipe check : recipeMap) { //check for double recipes, which will overwrite eachother
                if (check.getIngredientMap().equals(recipe.getIngredientMap())) {
                    MiniBackpackPlugin.getPlugin().getLogger().warning("Duplicate recipe found for backpack: " + itemName + ". The recipe will be overwritten.");
                }
            }
            recipeMap.add(recipe); //store the recipe for this backpack

            if(!hasAnUpgrade) {
                for(ItemStack craftingIngredient : recipe.getIngredientMap().values()) {
                    Material material = craftingIngredient.getType();
                    if(material == Material.AIR) continue; //skip air
                    if(!unlockRecipes.containsKey(material)) {
                        unlockRecipes.put(material, new ArrayList<>()); //make a new arrayList, if it doesn't exist yet
                    }
                    unlockRecipes.get(material).add(recipe.getKey()); //get the list of recipes and add it
                }
            }

        }

    }
    public static void safeBackpackData(BackpackNote data, boolean reloadRecipes) {
        String name = toDataCase(data.name);
        MiniBackpackPlugin.getPlugin().getConfig().set(name + ".slots", data.slots);
        if(data.isHopperSized) {
            MiniBackpackPlugin.getPlugin().getConfig().set(name + ".type", "hopper");
        } else {
            MiniBackpackPlugin.getPlugin().getConfig().set(name + ".type", "chest");
        }
        MiniBackpackPlugin.getPlugin().getConfig().set(name + ".texture", data.texture);
        if(data.craftingMaterials != null) {
            recipeYamlBuilder(Arrays.asList(data.craftingMaterials), name + ".recipe");
        } else {
            MiniBackpackPlugin.getPlugin().getConfig().set(name + ".recipe", null); //remove the recipe if there are no crafting materials
        }
        MiniBackpackPlugin.getPlugin().saveConfig();
        MiniBackpackPlugin.getPlugin().reloadConfig();
        reloadBackpacks(reloadRecipes, reloadRecipes); //reload backpacks after saving
    }

    private static void recipeYamlBuilder(List<String> inputs, String path) {
        // Step 1: Assign symbols (excluding "air")
        Map<Character, String> symbolMap = new LinkedHashMap<>();
        char nextSymbol = 'A';

        for (String item : inputs) {
            if (!item.equals("air") && !symbolMap.containsValue(item)) {
                symbolMap.put(nextSymbol++, item);
            }
        }

        // Step 2: Build pattern using symbols (or space for air)
        List<String> pattern = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < 3; j++) {
                String item = inputs.get(i * 3 + j);
                Character symbol = symbolMap.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(item))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(' '); // fallback if not found (e.g., air)
                row.append(symbol);
            }
            pattern.add(row.toString());
        }

        // Step 3: Print YAML
        // path: "name.recipe"
        MiniBackpackPlugin.getPlugin().getConfig().set(path + ".pattern", pattern);
        MiniBackpackPlugin.getPlugin().getConfig().set(path + ".ingredients", symbolMap);
    }

    public static void deleteBackpack(BackpackNote data) {
        String dataName = toDataCase(data.name);
        if(!MiniBackpackPlugin.getPlugin().getConfig().contains(dataName)) return;
        //if backpack is used as an upgrade backpack, so it cannot be deleted
        if(craftingUpgrades.containsKey(dataName)) throw new IllegalArgumentException(craftingUpgrades.get(dataName).toString());
        MiniBackpackPlugin.getPlugin().getConfig().set(dataName, null);

        //remove it from crafting recipes (not in use, because the process is stopped above if a crafting upgrade is used)
//        for(String key : craftingUpgrades.keySet()) { //material backpack name, result backpack name
//            if(key.equals(dataName)) {
//                String resultBackpackName = craftingUpgrades.get(key);
//                BackpackNote craftingUpgradeData = getBackpackNoteFromItem(getBackpackFromName(resultBackpackName));
//                craftingUpgradeData.setMaterial(4, "air"); //remove the upgrade backpack
//                craftingUpgradeData.hasUpgradeBackpack = false; //set the upgrade backpack to false
//                safeBackpackData(craftingUpgradeData, true);
//            }
//        }

        MiniBackpackPlugin.getPlugin().saveConfig();
        reloadBackpacks(true, true); //reload backpacks after deleting
    }
    public static boolean checkIfBackpackExists(String name, int slots, ItemStack item, Player p) {
        String dataName = toDataCase(name);
        if(!MiniBackpackPlugin.getPlugin().getConfig().contains(dataName)) return false; // it does not exist when the name was changed
        int configSlots = MiniBackpackPlugin.getPlugin().getConfig().getInt(dataName + ".slots");
        if(configSlots < slots) return false; //it does not exist when the slots are smaller than before
        String configTexture = MiniBackpackPlugin.getPlugin().getConfig().getString(dataName + ".texture");
        if(!Objects.equals(configTexture, getSkullTexture(item))) {
            setSkullTexture(item,configTexture);
            p.sendMessage(ChatColor.AQUA + "The texture of this backpack has been changed by an admin.");
        }
        return true;
    }

    public static BackpackNote getBackpackNoteFromItem(ItemStack item) {
        if (!isBackpack(item)) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        int slots = container.getOrDefault(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER, 0);
        String texture = MiniBackpackPlugin.getPlugin().getConfig().getString(toDataCase(meta.getItemName()) + ".texture");
        boolean isHopperSized = Objects.equals(MiniBackpackPlugin.getPlugin().getConfig().getString(toDataCase(meta.getItemName()) + ".type"), "hopper");
        if(slots > 5) isHopperSized = false;
        String upgradeItem = null;
        //search for the value and not the key
        for(String key : craftingUpgrades.keySet()) {
            String dataName = toDataCase(meta.getItemName());
            for (String resultItemName : craftingUpgrades.get(key)) { //this is the result item name
                if(resultItemName.equals(dataName)) {
                    upgradeItem = key; //if the result item name is equal to the backpack name, then this is the upgrade item
                    break;
                }
            }
        }

        List<String> craftingMaterialList = craftingMaterials.get(toDataCase(meta.getItemName())); //is null when there is no entry

        return new BackpackNote(meta.getItemName(), slots, texture, isHopperSized, upgradeItem != null,
                craftingMaterialList == null ? null : craftingMaterialList.toArray(new String[9]));
    }
    public static ItemStack getBackpackFromName(String name) {
        return backpacks.stream()
                .filter(i -> toDataCase(i.getItemMeta().getItemName()).equals(name))
                .findFirst()
                .orElse(null);
    }

    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;

        return Arrays.stream(input.split("[_ ]"))
                .filter(word -> !word.isEmpty()) // needed if there are multiple underlines one after another
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
    public static String toDataCase(String input) {
        if (input == null || input.isEmpty()) return input;

        return Arrays.stream(input.split(" "))
                .filter(word -> !word.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.joining("_"));
    }
    public static int getSlotsFromItem(ItemStack item) {
        if (!isBackpack(item)) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "slots"), PersistentDataType.INTEGER, 0);
    }

    private static void createBarrier() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Locked Slot");

        item.setItemMeta(meta);
        barrier = item;
    }
    private static void createFillerItem(){
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        fillerItem = item;
    }
    private static void createViewBackpacks() {
        ItemStack item = makeSkull(SMALL_BACKPACK_TEXTURE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "View Backpacks");
        meta.setLore(List.of("", ChatColor.YELLOW + "Click to view your backpacks"));
        item.setItemMeta(meta);
        viewBackpacks = item;
    }
    private static void createCreateNewBackpack() {
        ItemStack item = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.YELLOW + "Create a new backpack");
        item.setItemMeta(meta);
        createNewBackpack = item;
    }
    private static void createBackArrow() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.YELLOW + "Back");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "backArrow"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        backArrow = item;
    }
    private static void createEmptyPaper() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GRAY + "");
        item.setItemMeta(meta);
        emptyPaper = item;
    }
    private static void createCraftingTable() {
        ItemStack item = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Edit Crafting Recipe");
        meta.setLore(List.of(ChatColor.GRAY + "You can add or edit a crafting recipe",
                ChatColor.GRAY + "for this backpack. Make sure to have",
                ChatColor.GRAY + "the items in your inventory before.",
                "",
                ChatColor.YELLOW + "Click to open a crafting grid"));
        item.setItemMeta(meta);
        craftingTable = item;
    }
    private static void createDeleteButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Delete Backpack");
        meta.setLore(List.of("", ChatColor.YELLOW + "Click to delete this backpack"));
        item.setItemMeta(meta);
        deleteButton = item;
    }
    private static void createConfirm() {
        ItemStack item = new ItemStack(Material.GREEN_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Confirm");
        item.setItemMeta(meta);
        confirm = item;
    }
    private static void createCancel() {
        ItemStack item = new ItemStack(Material.RED_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Cancel");
        item.setItemMeta(meta);
        cancel = item;
    }
    private static void createEditName() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Edit Name");
        meta.setLore(List.of(ChatColor.GRAY + "If you edit the name, all existing ",
                ChatColor.GRAY + "backpacks of this type will be removed.",
                ChatColor.GRAY + "The items will be dropped on the ground ",
                ChatColor.GRAY + "if you try to open the old backpack.",
                "", ChatColor.YELLOW + "Click to change the name"));
        item.setItemMeta(meta);
        editName = item;
    }
    private static void createEditSlots() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Edit Slots");
        meta.setLore(List.of(ChatColor.GRAY + "If the slots you select are bigger than ",
                ChatColor.GRAY + "they are now, everything is fine.",
                ChatColor.GRAY + "If the slots are smaller than before, ",
                ChatColor.GRAY + "all existing backpacks of this type will be removed.",
                "", ChatColor.YELLOW + "Click to change the number of slots"));
        item.setItemMeta(meta);
        editSlots = item;
    }
    private static void createEditTexture() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Edit Texture");
        meta.setLore(List.of(ChatColor.GRAY + "If you edit the texture, all existing backpacks ",
                ChatColor.GRAY + "of this type will have their texture changed.",
                "", ChatColor.YELLOW + "Click to change the texture"));
        item.setItemMeta(meta);
        editTexture = item;
    }
    private static void createChestSized() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Edit Inventory Size");
        meta.setLore(List.of("", ChatColor.AQUA + "► Chest Sized Backpack",
                ChatColor.GRAY + "Hopper Sized Backpack"));
        item.setItemMeta(meta);
        chestSized = item;
    }
    private static void createHopperSized() {
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Edit Inventory Size");
        meta.setLore(List.of("", ChatColor.GRAY + "Chest Sized Backpack",
                ChatColor.AQUA + "► Hopper Sized Backpack"));
        item.setItemMeta(meta);
        hopperSized = item;
    }
    private static void createUpgradeBackpack() {
        ItemStack item = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Set a Backpack to upgrade");

        meta.setLore(List.of(ChatColor.GRAY + "You can choose a backpack with less",
                ChatColor.GRAY + "slots than the current one to be an",
                ChatColor.GRAY + "upgrade material for this backpack.",
                "",
                ChatColor.YELLOW + "Click to choose a backpack"));
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item.setItemMeta(meta);
        upgradeBackpack = item;
    }
    private static void createAllCraftingInfo() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Info");
        meta.setLore(List.of(ChatColor.GRAY + "You have to first put the items that you",
                ChatColor.GRAY + "want to use in the crafting recipe",
                ChatColor.GRAY + "into your inventory."));
        item.setItemMeta(meta);
        craftingInfo1 = item.clone();
        meta.setLore(List.of(ChatColor.GRAY + "Remove all materials, if you want to",
                ChatColor.GRAY + "remove the crafting recipe.",
                ChatColor.GRAY + "If you just have a backpack in the crafting grid,",
                ChatColor.GRAY + "there will be no crafting recipe created"));
        item.setItemMeta(meta);
        craftingInfo2 = item.clone();
        meta.setLore(List.of(ChatColor.GRAY + "You can overwrite a vanilla crafting recipe,",
                ChatColor.GRAY + "though it's not recommended."));
        item.setItemMeta(meta);
        craftingInfo3 = item.clone();
    }
    private static void createDeselectUpgrade() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Deselect Upgrade Backpack");
        meta.setLore(List.of(ChatColor.YELLOW + "Click to deselect the upgrade backpack"));
        item.setItemMeta(meta);
        deselectUpgrade = item;
    }

    public static ItemStack makeSkull(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        setSkullTexture(skull, url);
        return skull;
    }
    public static String getSkullTexture(ItemStack item) {
        if(item == null || item.getType() != Material.PLAYER_HEAD) return null;
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if(skullMeta == null || skullMeta.getOwnerProfile() == null) return null;
        PlayerTextures textures = skullMeta.getOwnerProfile().getTextures();
        if(textures.getSkin() == null) return null;
        return textures.getSkin().toString();
    }
    public static void setSkullTexture(ItemStack item, String url) {
        if(item == null || item.getType() != Material.PLAYER_HEAD) return;
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        if(skullMeta == null) return;
        PlayerProfile skullProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = skullProfile.getTextures();
        try {
            textures.setSkin(URI.create(url).toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        skullProfile.setTextures(textures);
        skullMeta.setOwnerProfile(skullProfile);
        item.setItemMeta(skullMeta);
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

    public static boolean isBackpack(ItemStack item) {
        if(item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(new NamespacedKey(MiniBackpackPlugin.getPlugin(), "items"), PersistentDataType.STRING);
    }

}
