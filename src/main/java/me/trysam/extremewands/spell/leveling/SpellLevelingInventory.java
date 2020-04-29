package me.trysam.extremewands.spell.leveling;

import me.trysam.extremewands.spell.*;
import me.trysam.extremewands.util.ItemBuilder;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpellLevelingInventory implements InventoryHolder {

    private Inventory inventory;
    private Player player;
    private EnumSpell spell;
    private SpellLeveler leveler;
    private int currentPage = 0;

    SpellLevelingInventory(Player owner, String title, EnumSpell spell, SpellLeveler leveler) {
        this.player = owner;
        inventory = Bukkit.createInventory(this, 9*4, title);
        this.spell = spell;
        this.leveler = leveler;
    }

    public void prepareInventory() {
        inventory.clear();
        List<SimpleLevelProperty> values = Arrays.asList(spell.getLevelProperties());
        int valuesSize = values.size();
        if(currentPage*9*3 >= valuesSize) {
            setCurrentPage(currentPage);
        }
        int end = currentPage*3*9+3*9;
        if(end > valuesSize) {
            end = valuesSize;
        }

        List<SimpleLevelProperty> subValues = values.subList(currentPage*9*3, end);
        for (int i = 0; i < subValues.size(); i++) {
            SimpleLevelProperty value = subValues.get(i);
            if(value != null && !value.getId().isEmpty() && !value.getInfoID().isEmpty()) {
                int points = leveler.getSpellLevelPoints(spell);

                InfoProvider provider = leveler.plugin.getLevelPropertyInfoProvider();
                InfoEntry infoEntry = provider.provide(value.getInfoID());

                LevelProperty property = new LevelProperty(leveler.plugin, player, value);

                ItemStack stack = new ItemStack(Material.GLOWSTONE_DUST);
                ItemMeta meta = stack.getItemMeta();

                meta.setDisplayName("§6"+infoEntry.getName());

                List<String> lore = new ArrayList<>(infoEntry.getDescription());
                lore.add("");
                lore.add("§aCurrent value§8: §e"+property.getValue());
                String status = "§eUpgrade available";

                meta.getPersistentDataContainer().set(leveler.plugin.getNamespaces().getSpell().getSelectedUpgrade(), PersistentDataType.STRING, value.getId());

                //can-select-upgrade: 0 = OK, 1 = NOT ENOUGH COINS, 2 = MAX LEVEL REACHED
                meta.getPersistentDataContainer().set(leveler.plugin.getNamespaces().getSpell().getCanSelectUpgrade(), PersistentDataType.BYTE, (byte)0);
                if(points <= 0) {
                    stack.setType(Material.GUNPOWDER);
                    meta.getPersistentDataContainer().set(leveler.plugin.getNamespaces().getSpell().getCanSelectUpgrade(), PersistentDataType.BYTE, (byte)1);
                    status = "§7Not enough coins";
                }
                if(property.getStep() > 0) {
                    if(property.getValue()+property.getStep() > property.getMax()) {
                        stack.setType(Material.REDSTONE);
                        meta.getPersistentDataContainer().set(leveler.plugin.getNamespaces().getSpell().getCanSelectUpgrade(), PersistentDataType.BYTE, (byte)2);
                        status = "§cMax level reached";
                        LivingEntity entity;
                    }
                }else if(property.getStep() < 0) {
                    if(property.getValue()+property.getStep() < property.getMin()) {
                        stack.setType(Material.REDSTONE);
                        meta.getPersistentDataContainer().set(leveler.plugin.getNamespaces().getSpell().getCanSelectUpgrade(), PersistentDataType.BYTE, (byte)2);
                        status = "§cMax level reached";
                    }
                }else {
                    continue;
                }

                lore.add(status);

                meta.setLore(lore);
                stack.setItemMeta(meta);
                inventory.setItem(i, stack);
            }
        }
        if((currentPage+1)*9*3 <= values.size()) {
            ItemStack next = new ItemBuilder(Material.ARROW).setName("§eNext page").build();
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.getPersistentDataContainer().set(leveler.plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE, (byte)1);
            next.setItemMeta(nextMeta);
            inventory.setItem(35, next);
        }
        if(currentPage > 0) {
            ItemStack previous = new ItemBuilder(Material.ARROW).setName("§aPrevious page").build();
            ItemMeta previousMeta = previous.getItemMeta();
            previousMeta.getPersistentDataContainer().set(leveler.plugin.getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE, (byte)0);
            previous.setItemMeta(previousMeta);
            inventory.setItem(35-8, previous);
        }
    }

    public void openInventory() {
        player.openInventory(inventory);
    }

    public void setCurrentPage(int currentPage) {
        SimpleLevelProperty[] values = spell.getLevelProperties();
        if(currentPage*9*3 >= values.length) {
            this.currentPage = (values.length/(9*3));
            return;
        }
        this.currentPage = currentPage;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public SpellLeveler getLeveler() {
        return leveler;
    }

    public EnumSpell getSpell() {
        return spell;
    }
}
