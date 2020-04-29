package me.trysam.extremewands.util;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RecipeLoader {

    private ExtremeWandsPlugin plugin;

    @Inject
    private RecipeLoader(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadWoodenWandRecipe() {
        ItemStack result = new ItemBuilder(Material.STICK).setName("§a§lWooden Wand").setLore("§7Right-click on lectern to activate", "§7Lectern must hold §6\"Ancient Knowledge\"").build();
        ItemMeta meta = result.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE, (byte)0);
        result.setItemMeta(meta);
        NamespacedKey key = plugin.getNamespaces().getRecipes().getWoodenWand();
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("A", "B", "C");

        RecipeChoice planksChoice = new RecipeChoice.MaterialChoice(Material.ACACIA_PLANKS, Material.BIRCH_PLANKS, Material.DARK_OAK_PLANKS, Material.JUNGLE_PLANKS, Material.OAK_PLANKS, Material.SPRUCE_PLANKS);

        recipe.setIngredient('A', Material.RED_MUSHROOM);
        recipe.setIngredient('B', planksChoice);
        recipe.setIngredient('C', Material.GOLD_INGOT);

        Bukkit.addRecipe(recipe);
    }

    public void loadBoneWandRecipe() {
        ItemStack result = new ItemBuilder(Material.BONE).setName("§7§lBone Wand").setLore("§7Right-click on lectern to activate", "§7Lectern must hold §6\"Ancient Knowledge\"").build();

        ItemMeta meta = result.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE, (byte)0);
        result.setItemMeta(meta);

        NamespacedKey key = plugin.getNamespaces().getRecipes().getBoneWand();
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("A", "B", "C");

        recipe.setIngredient('A', Material.GUNPOWDER);
        recipe.setIngredient('B', Material.BONE);
        recipe.setIngredient('C', Material.GOLD_INGOT);

        Bukkit.addRecipe(recipe);
    }

    public void loadBlazeWandRecipe() {
        ItemStack result = new ItemBuilder(Material.BLAZE_ROD).setName("§6§lBlaze Wand").setLore("§7Right-click on lectern to activate", "§7Lectern must hold §6\"Ancient Knowledge\"").build();

        ItemMeta meta = result.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE, (byte)0);
        result.setItemMeta(meta);

        NamespacedKey key = plugin.getNamespaces().getRecipes().getBlazeWand();
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("A", "B", "C");

        recipe.setIngredient('A', Material.ENDER_PEARL);
        recipe.setIngredient('B', Material.BLAZE_ROD);
        recipe.setIngredient('C', Material.GOLD_INGOT);

        Bukkit.addRecipe(recipe);
    }

    public void loadEnderWandRecipe() {
        ItemStack result = new ItemBuilder(Material.END_ROD).setName("§5§lEnder Wand").setLore("§7Right-click on lectern to activate", "§7Lectern must hold §6\"Ancient Knowledge\"").build();

        ItemMeta meta = result.getItemMeta();
        meta.getPersistentDataContainer().set(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE, (byte)0);
        result.setItemMeta(meta);

        NamespacedKey key = plugin.getNamespaces().getRecipes().getEnderWand();
        ShapedRecipe recipe = new ShapedRecipe(key, result);

        recipe.shape("A", "B", "C");

        recipe.setIngredient('A', Material.NETHER_STAR);
        recipe.setIngredient('B', Material.END_ROD);
        recipe.setIngredient('C', Material.DRAGON_EGG);

        Bukkit.addRecipe(recipe);
    }

}
