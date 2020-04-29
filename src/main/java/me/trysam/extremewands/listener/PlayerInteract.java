package me.trysam.extremewands.listener;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.spell.EnumSpell;
import me.trysam.extremewands.spell.casting.FireModeSelectionInventory;
import me.trysam.extremewands.spell.casting.FiringMode;
import me.trysam.extremewands.spell.casting.SpellSelector;
import me.trysam.extremewands.spell.leveling.SpellLeveler;
import me.trysam.extremewands.spell.leveling.SpellSelectionInventory;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import me.trysam.extremewands.wand.WandCategory;
import net.minecraft.server.v1_15_R1.Particles;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerInteract implements Listener {


    private final static long COOLDOWN = 333;

    private ExtremeWandsPlugin plugin;
    private AtomicReference<Map<UUID, Long>> primaryCooldown = new AtomicReference<>(new HashMap<>());
    private AtomicReference<Map<UUID, Long>> secondaryCooldown = new AtomicReference<>(new HashMap<>());

    @Inject
    private PlayerInteract(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onEvent(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }


        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            if (event.getItem().hasItemMeta()) {
                if (event.getItem().getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE)) {
                    event.setCancelled(true);
                }
            }
        }


        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.LECTERN) {
                Lectern lectern = (Lectern) event.getClickedBlock().getState();
                ItemStack book = lectern.getInventory().getItem(0);
                if(lectern.getBlock().getLightFromSky() < 15) {
                    event.getPlayer().sendMessage("§cLectern must see the sky");
                    return;
                }
                if (book != null) {
                    if (book.hasItemMeta()) {
                        if (book.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getBook().getIsAncientKnowledgeBook(), PersistentDataType.BYTE)) {
                            ItemStack itemStack = event.getItem();
                            if (itemStack.hasItemMeta()) {
                                if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE)) {
                                    if (itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE) == 0) {
                                        plugin.getWandActivator().activateWand(event.getPlayer(), lectern.getLocation(), itemStack);
                                        return;
                                    }
                                    if (itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE) == 1) {
                                        if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getOwner(), PersistentDataType.STRING)) {
                                            if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getType(), PersistentDataType.INTEGER)) {
                                                String uuid = itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getOwner(), PersistentDataType.STRING);
                                                Integer type = itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getType(), PersistentDataType.INTEGER);
                                                if (uuid != null && type != null) {
                                                    if (uuid.equalsIgnoreCase(event.getPlayer().getUniqueId().toString())) {
                                                        for (WandCategory value : WandCategory.values()) {
                                                            if (value.ordinal() == type) {
                                                                SpellLeveler leveler = new SpellLeveler(event.getPlayer(), plugin);
                                                                SpellSelectionInventory spellSelectionInventory = leveler.getSpellSelection(value);
                                                                spellSelectionInventory.prepareInventory();
                                                                spellSelectionInventory.openInventory();
                                                                event.setCancelled(true);
                                                            }
                                                        }
                                                    } else {
                                                        event.getPlayer().playSound(lectern.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                                                        SimpleParticleRenderer renderer = new SimpleParticleRenderer(
                                                                Particles.SMOKE, true,
                                                                Vec3d.fromLocation(lectern.getLocation().clone().add(0.5, 0.5, 0.5)),
                                                                new Vec3f(0.8f, 1.2f, 0.8f),
                                                                0.3f, 10);
                                                        renderer.render(Collections.singletonList(event.getPlayer()));
                                                    }
                                                }
                                            }
                                        }
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getPlayer().isSneaking()) {
                openSelection(event.getPlayer(), event.getItem());
            }else {
                fire(event.getPlayer(), event.getItem(), FiringMode.SECONDARY);
            }
        } else if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            fire(event.getPlayer(), event.getItem(), FiringMode.PRIMARY);
        }
    }

    private boolean isFiringModeOnCooldown(Player player, FiringMode firingMode) {
        switch (firingMode) {
            case PRIMARY:
                if(primaryCooldown.get().containsKey(player.getUniqueId())) {
                    if(System.currentTimeMillis() - primaryCooldown.get().get(player.getUniqueId()) > COOLDOWN) {
                        primaryCooldown.get().remove(player.getUniqueId());
                        return false;
                    }else {
                        return true;
                    }
                }
            case SECONDARY:
                if(secondaryCooldown.get().containsKey(player.getUniqueId())) {
                    if(System.currentTimeMillis() - secondaryCooldown.get().get(player.getUniqueId()) > COOLDOWN) {
                        secondaryCooldown.get().remove(player.getUniqueId());
                        return false;
                    }else {
                        return true;
                    }
                }
        }
        return false;
    }


    private void fire(Player player, ItemStack stack, FiringMode firingMode) {
        if (!stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE)) {
            return;
        }
        if(isFiringModeOnCooldown(player, firingMode)) {
            System.out.println("HEYHOMINEGUAFTFREUNDE");
            return;
        }
        if (stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE) == 1) {
            if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getOwner(), PersistentDataType.STRING)) {
                if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getType(), PersistentDataType.INTEGER)) {
                    if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getWandUuid(), PersistentDataType.STRING)) {
                        if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getEffectMultiplier(), PersistentDataType.DOUBLE)) {
                            if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getProtectionMultiplier(), PersistentDataType.DOUBLE)) {
                                String owner = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getOwner(), PersistentDataType.STRING);
                                Integer type = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getType(), PersistentDataType.INTEGER);
                                String wandUuid = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getWandUuid(), PersistentDataType.STRING);
                                Double effectMultiplier = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getEffectMultiplier(), PersistentDataType.DOUBLE);
                                Double protectionMultiplier = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getProtectionMultiplier(), PersistentDataType.DOUBLE);
                                if(owner == null) {
                                    return;
                                }
                                if(type == null) {
                                    return;
                                }
                                if(wandUuid == null) {
                                    return;
                                }
                                if(effectMultiplier == null) {
                                    return;
                                }
                                if(protectionMultiplier == null) {
                                    return;
                                }
                                if (owner.equalsIgnoreCase(player.getUniqueId().toString())) {
                                    SpellSelector spellSelector = new SpellSelector(player, plugin);
                                    for (WandCategory value : WandCategory.values()) {
                                        if (value.ordinal() == type) {
                                            for (EnumSpell enumSpell : EnumSpell.values()) {
                                                if(enumSpell == EnumSpell.NONE) {
                                                    continue;
                                                }
                                                if(enumSpell.getSpellLevel() > value.getCanHandleSpellLevel()) {
                                                    continue;
                                                }
                                                switch (firingMode) {
                                                    case PRIMARY:
                                                        if(enumSpell == plugin.getSpellSelectionRepository().get(new UUID[] {player.getUniqueId(), UUID.fromString(wandUuid)}).getPrimary()) {
                                                            plugin.getSpellHandler().launchSpell(enumSpell, player, value, effectMultiplier.floatValue(), protectionMultiplier.floatValue());
                                                        }
                                                        primaryCooldown.get().put(player.getUniqueId(), System.currentTimeMillis());
                                                        break;
                                                    case SECONDARY:
                                                        if(enumSpell == plugin.getSpellSelectionRepository().get(new UUID[] {player.getUniqueId(), UUID.fromString(wandUuid)}).getSecondary()) {
                                                            plugin.getSpellHandler().launchSpell(enumSpell, player, value, effectMultiplier.floatValue(), protectionMultiplier.floatValue());
                                                        }
                                                        secondaryCooldown.get().put(player.getUniqueId(), System.currentTimeMillis());
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void openSelection(Player player, ItemStack stack) {
        if (!stack.hasItemMeta()) {
            return;
        }
        if (!stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE)) {
            return;
        }
        if (stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE) == 1) {
            if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getOwner(), PersistentDataType.STRING)) {
                if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getType(), PersistentDataType.INTEGER)) {
                    if (stack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getWand().getWandUuid(), PersistentDataType.STRING)) {
                        String owner = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getOwner(), PersistentDataType.STRING);
                        Integer type = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getType(), PersistentDataType.INTEGER);
                        String wandUuid = stack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getWand().getWandUuid(), PersistentDataType.STRING);
                        if (type == null) {
                            return;
                        }
                        if (wandUuid == null) {
                            return;
                        }
                        if (owner.equalsIgnoreCase(player.getUniqueId().toString())) {
                            SpellSelector spellSelector = new SpellSelector(player, plugin);
                            for (WandCategory value : WandCategory.values()) {
                                if (value.ordinal() == type) {
                                    FireModeSelectionInventory selectionInventory = spellSelector.getFireModeSelectionInventory(value, UUID.fromString(wandUuid));
                                    selectionInventory.prepareInventory();
                                    selectionInventory.openInventory();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if(primaryCooldown.get().containsKey(event.getPlayer().getUniqueId())) {
            primaryCooldown.get().remove(event.getPlayer());
        }
        if(secondaryCooldown.get().containsKey(event.getPlayer().getUniqueId())) {
            secondaryCooldown.get().remove(event.getPlayer());
        }
    }

}
