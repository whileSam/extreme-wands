package me.trysam.extremewands.listener;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.player.ManaRepository;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.UUID;

public class PlayerDropItem implements Listener {

    private ExtremeWandsPlugin plugin;

    @Inject
    private PlayerDropItem(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(PlayerDropItemEvent event) {
        switch (event.getItemDrop().getItemStack().getType()) {
            case REDSTONE:
                rewardMana(event.getPlayer(), event.getItemDrop(), 5*event.getItemDrop().getItemStack().getAmount());
                break;
            case BLAZE_POWDER:
                rewardMana(event.getPlayer(), event.getItemDrop(), 15*event.getItemDrop().getItemStack().getAmount());
                break;
            case GOLD_INGOT:
                rewardMana(event.getPlayer(), event.getItemDrop(), 40*event.getItemDrop().getItemStack().getAmount());
                break;
            case LAPIS_LAZULI:
                rewardMana(event.getPlayer(), event.getItemDrop(), 60*event.getItemDrop().getItemStack().getAmount());
                break;
            case EMERALD:
                rewardMana(event.getPlayer(), event.getItemDrop(), 80*event.getItemDrop().getItemStack().getAmount());
                break;
            case EXPERIENCE_BOTTLE:
                rewardMana(event.getPlayer(), event.getItemDrop(), 100*event.getItemDrop().getItemStack().getAmount());
                break;
            case DIAMOND:
                rewardMana(event.getPlayer(), event.getItemDrop(), 140*event.getItemDrop().getItemStack().getAmount());
                break;
            case ENCHANTED_GOLDEN_APPLE:
                rewardMana(event.getPlayer(), event.getItemDrop(), 190*event.getItemDrop().getItemStack().getAmount());
                break;
            case NETHER_STAR:
                rewardMana(event.getPlayer(), event.getItemDrop(), 4000*event.getItemDrop().getItemStack().getAmount());
                break;
        }
    }

    private void rewardMana(Player player, Item item, int amount) {
        int radius = 2;
        UUID uuid = player.getUniqueId();
        ManaRepository manaRepository = plugin.getManaRepository();
        for (int i = -radius; i < radius + 1; i++) {
            for (int j = -radius; j < radius + 1; j++) {
                for (int k = -radius; k < radius + 1; k++) {
                    if(player.getWorld().getBlockAt(player.getLocation().getBlockX()+i, player.getLocation().getBlockY() +j, player.getLocation().getBlockZ()+k).getType() == Material.ENCHANTING_TABLE) {
                        manaRepository.set(uuid, manaRepository.get(uuid)+amount);
                        item.remove();
                        plugin.getManaController().playEnchantEffect(player.getEyeLocation(), 60, 0.8f);
                    }
                }
            }
        }
    }
}
