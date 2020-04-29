package me.trysam.extremewands.listener;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.spell.EnumSpell;
import me.trysam.extremewands.spell.LevelProperty;
import me.trysam.extremewands.spell.casting.FireModeSelectionInventory;
import me.trysam.extremewands.spell.casting.FiringMode;
import me.trysam.extremewands.spell.casting.SpellSelection;
import me.trysam.extremewands.spell.leveling.SpellLevelingInventory;
import me.trysam.extremewands.spell.leveling.SpellSelectionInventory;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class InventoryClick implements Listener {

    private ExtremeWandsPlugin plugin;

    @Inject
    private InventoryClick(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEvent(InventoryClickEvent event) {
        if(event.getInventory().getHolder() == null) {
            return;
        }
        if (event.getInventory().getHolder() instanceof SpellSelectionInventory) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getSlot() != event.getRawSlot()) {
                return;
            }
            if (event.getCurrentItem().hasItemMeta()) {
                ItemStack itemStack = event.getCurrentItem();
                Inventory inventory = event.getInventory();
                Player player = (Player) event.getWhoClicked();
                SpellSelectionInventory spellSelectionInventory = (SpellSelectionInventory) inventory.getHolder();
                if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE)) {
                    if (itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE) == 1) {
                        spellSelectionInventory.setCurrentPage(spellSelectionInventory.getCurrentPage() + 1);
                    } else {
                        spellSelectionInventory.setCurrentPage(spellSelectionInventory.getCurrentPage() - 1);
                    }
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                    spellSelectionInventory.prepareInventory();
                    player.updateInventory();
                } else if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpell().getSelectedSpell(), PersistentDataType.STRING)) {
                    EnumSpell spell = EnumSpell.valueOf(itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpell().getSelectedSpell(), PersistentDataType.STRING));
                    SpellLevelingInventory spellLevelingInventory = spellSelectionInventory.getLeveler().getUpgradeSelection(spell);
                    spellLevelingInventory.prepareInventory();
                    spellLevelingInventory.openInventory();
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                }
            }
        }
        if (event.getInventory().getHolder() instanceof SpellLevelingInventory) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getSlot() != event.getRawSlot()) {
                return;
            }
            if (event.getCurrentItem().hasItemMeta()) {
                ItemStack itemStack = event.getCurrentItem();
                Inventory inventory = event.getInventory();
                Player player = (Player) event.getWhoClicked();
                SpellLevelingInventory spellLevelingInventory = (SpellLevelingInventory) inventory.getHolder();
                if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE)) {
                    if (itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE) == 0) {
                        spellLevelingInventory.setCurrentPage(spellLevelingInventory.getCurrentPage() + 1);
                    } else {
                        spellLevelingInventory.setCurrentPage(spellLevelingInventory.getCurrentPage() - 1);
                    }
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                    spellLevelingInventory.prepareInventory();
                    player.updateInventory();
                } else if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpell().getCanSelectUpgrade(), PersistentDataType.BYTE)) {
                    if (itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpell().getCanSelectUpgrade(), PersistentDataType.BYTE) == 0) {
                        if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpell().getSelectedUpgrade(), PersistentDataType.STRING)) {
                            String selectedUpgrade = itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpell().getSelectedUpgrade(), PersistentDataType.STRING);
                            EnumSpell spell = spellLevelingInventory.getSpell();
                            LevelProperty property = new LevelProperty(plugin, player, spell.getLevelPropertyByID(selectedUpgrade));
                            property.increaseValue(1);
                            property.saveValue();
                            spellLevelingInventory.getLeveler().setSpellLevelPoints(spell, spellLevelingInventory.getLeveler().getSpellLevelPoints(spell) - 1);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                            spellLevelingInventory.prepareInventory();
                            player.updateInventory();
                        }
                    }
                }
            }
        }
        if (event.getInventory().getHolder() instanceof FireModeSelectionInventory) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getSlot() != event.getRawSlot()) {
                return;
            }
            if (event.getCurrentItem().hasItemMeta()) {
                ItemStack itemStack = event.getCurrentItem();
                Inventory inventory = event.getInventory();
                Player player = (Player) event.getWhoClicked();
                FireModeSelectionInventory fireModeSelectionInventory = (FireModeSelectionInventory) inventory.getHolder();
                if(itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpecial().getFiringModeSelector(), PersistentDataType.BYTE)) {
                    for (FiringMode value : FiringMode.values()) {
                        if(value.ordinal() == itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpecial().getFiringModeSelector(), PersistentDataType.BYTE)) {
                            me.trysam.extremewands.spell.casting.SpellSelectionInventory spellSelectionInventory =
                                    fireModeSelectionInventory.getSpellSelector().getSpellSelectionInventory(value,
                                            fireModeSelectionInventory.getWandCategory(), fireModeSelectionInventory.getWandUUID());
                            spellSelectionInventory.prepareInventory();
                            spellSelectionInventory.openInventory();
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                        }
                    }
                }
            }
        }
        if (event.getInventory().getHolder() instanceof me.trysam.extremewands.spell.casting.SpellSelectionInventory) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            if (event.getSlot() != event.getRawSlot()) {
                return;
            }
            if (event.getCurrentItem().hasItemMeta()) {
                ItemStack itemStack = event.getCurrentItem();
                Inventory inventory = event.getInventory();
                Player player = (Player) event.getWhoClicked();
                me.trysam.extremewands.spell.casting.SpellSelectionInventory spellSelectionInventory = (me.trysam.extremewands.spell.casting.SpellSelectionInventory) inventory.getHolder();
                FiringMode firingMode = spellSelectionInventory.getFiringMode();
                if(itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE)) {
                    if (itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE) == 0) {
                        spellSelectionInventory.setCurrentPage(spellSelectionInventory.getCurrentPage() + 1);
                    } else {
                        spellSelectionInventory.setCurrentPage(spellSelectionInventory.getCurrentPage() - 1);
                    }
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                    spellSelectionInventory.prepareInventory();
                    player.updateInventory();
                }
                if(itemStack.getItemMeta().getPersistentDataContainer().has(plugin.getNamespaces().getSpell().getSelectedSpell(), PersistentDataType.STRING)) {
                    for (EnumSpell value : EnumSpell.values()) {
                        if(value.name() == itemStack.getItemMeta().getPersistentDataContainer().get(plugin.getNamespaces().getSpell().getSelectedSpell(), PersistentDataType.STRING)) {
                            SpellSelection selection = plugin.getSpellSelectionRepository().get(new UUID[]{player.getUniqueId(), spellSelectionInventory.getWandUUID()});
                            switch (firingMode) {
                                case PRIMARY:
                                    selection.setPrimary(value);
                                    break;
                                case SECONDARY:
                                    selection.setSecondary(value);
                                    break;
                                default:
                                    return;
                            }
                            plugin.getSpellSelectionRepository().set(new UUID[]{player.getUniqueId(), spellSelectionInventory.getWandUUID()}, selection);
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                            spellSelectionInventory.prepareInventory();
                            player.updateInventory();
                        }
                    }
                }
            }
        }
    }

}
