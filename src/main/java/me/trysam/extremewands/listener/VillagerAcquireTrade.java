package me.trysam.extremewands.listener;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import net.minecraft.server.v1_15_R1.ItemStack;
import net.minecraft.server.v1_15_R1.Items;
import net.minecraft.server.v1_15_R1.MerchantRecipe;
import net.minecraft.server.v1_15_R1.MerchantRecipeList;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class VillagerAcquireTrade implements Listener {


    private Random random = new Random();
    private ExtremeWandsPlugin plugin;
    private org.bukkit.inventory.ItemStack reference;

    @Inject
    private VillagerAcquireTrade(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
        this.reference = plugin.getAncientKnowledgeBookWriter().generateBook();
    }


    @EventHandler
    public void onEvent(VillagerAcquireTradeEvent event) {
        if(event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();
            if (villager.getProfession() == Villager.Profession.CLERIC && villager.getVillagerLevel() == 4) {
                CraftVillager craftVillager = (CraftVillager) villager;
                if (!hasOffer(craftVillager.getHandle().getOffers(), reference)) {
                    craftVillager.getHandle().getOffers().add(new net.minecraft.server.v1_15_R1.MerchantRecipe(new ItemStack(Items.EMERALD, 13), CraftItemStack.asNMSCopy(plugin.getAncientKnowledgeBookWriter().generateBook()), 3, 25, 13));
                }
            }
        }
    }

    private boolean hasOffer(MerchantRecipeList list, org.bukkit.inventory.ItemStack result) {
        for (MerchantRecipe recipe : list) {
            if (recipe.getSellingItem().getItem() == Items.WRITTEN_BOOK) {
                org.bukkit.inventory.ItemStack stack = CraftItemStack.asBukkitCopy(recipe.getSellingItem());
                if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getBook().getIsAncientKnowledgeBook(), PersistentDataType.BYTE)) {
                    return true;
                }
            }
        }
        return false;
    }



}
