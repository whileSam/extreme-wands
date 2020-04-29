package me.trysam.extremewands.listener;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.util.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataType;

public class CraftItem implements Listener {



    private ExtremeWandsPlugin plugin;

    @Inject
    private CraftItem(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(CraftItemEvent event) {
        for (ItemStack matrix : event.getInventory().getMatrix()) {
            if(matrix == null) {
                continue;
            }
            if(matrix.hasItemMeta()) {
                if(matrix.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE)) {
                    event.setCancelled(true);
                }
            }
        }
        if (event.getRecipe() instanceof ShapedRecipe) {
            ShapedRecipe recipe = (ShapedRecipe) event.getRecipe();
            NamespacedKey namespacedKey = recipe.getKey();
            if (namespacedKey.equals(plugin.getNamespaces().getRecipes().getWoodenWand()) ||
                    namespacedKey.equals(plugin.getNamespaces().getRecipes().getBoneWand()) ||
                    namespacedKey.equals(plugin.getNamespaces().getRecipes().getBlazeWand())) {
                ItemStack toReturn = new ItemBuilder(recipe.getIngredientMap().get('a')).build();
                if (toReturn != null) {
                    if(event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                        toReturn.setAmount(getLowestAmount(event.getInventory().getMatrix()));
                    }
                    event.getWhoClicked().getInventory().addItem(toReturn);
                }
            }
        }
    }

    public int getLowestAmount(ItemStack[] stacks) {
        int lowest = 64;
        for (ItemStack stack : stacks) {
            if(stack == null) {
                continue;
            }
            if(stack.getAmount() < lowest) {
                lowest = stack.getAmount();
            }
        }
        if(lowest <= 0) {
            lowest = 1;
        }
        return lowest;
    }

}
