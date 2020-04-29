package me.trysam.extremewands.spell.casting;

import me.trysam.extremewands.spell.EnumSpell;
import me.trysam.extremewands.spell.InfoEntry;
import me.trysam.extremewands.spell.InfoProvider;
import me.trysam.extremewands.util.ItemBuilder;
import me.trysam.extremewands.wand.WandCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpellSelectionInventory implements InventoryHolder {

    private Player player;
    private SpellSelector spellSelector;
    private FiringMode firingMode;
    private int currentPage = 0;
    private Inventory inventory;
    private WandCategory wandCategory;
    private UUID wandUUID;

    SpellSelectionInventory(Player player, SpellSelector spellSelector, FiringMode firingMode, String title, WandCategory wandCategory, UUID wandUUID) {
        this.player = player;
        this.spellSelector = spellSelector;
        this.firingMode = firingMode;
        inventory = Bukkit.createInventory(this, 9 * 4, title);
        this.wandCategory = wandCategory;
        this.wandUUID = wandUUID;
    }

    public void prepareInventory() {
        inventory.clear();
        List<EnumSpell> values = getAvailableSpells();
        values.sort((o1, o2) -> Boolean.compare(o1.isDarkMagic(), o2.isDarkMagic()));
        int valuesLength = values.size();
        if (currentPage * 9 * 3 >= valuesLength) {
            setCurrentPage(currentPage);
        }
        int end = currentPage * 3 * 9 + 3 * 9;
        if (end > valuesLength) {
            end = valuesLength;
        }
        List<EnumSpell> subValues = values.subList(currentPage * 9 * 3, end);
        for (int i = 0; i < subValues.size(); i++) {
            EnumSpell value = subValues.get(i);
            if (value != null) {
                ChatColor color = ChatColor.GREEN;
                switch (value.getSpellLevel()) {
                    case 1:
                        color = ChatColor.GOLD;
                        break;
                    case 2:
                        color = ChatColor.RED;
                        break;
                    case 3:
                        color = ChatColor.DARK_PURPLE;
                        break;
                    default:
                        break;
                }
                InfoProvider provider = spellSelector.getPlugin().getSpellInfoProvider();
                InfoEntry infoEntry = provider.provide(value.name());
                String name = infoEntry.getName();
                List<String> description = infoEntry.getDescription();

                ItemStack stack = new ItemBuilder(value.isDarkMagic() ? Material.ENDER_EYE : Material.BLAZE_POWDER).setName(color + name).build();

                ItemMeta meta = stack.getItemMeta();
                if(value == EnumSpell.NONE) {
                    stack.setType(Material.BARRIER);
                    meta.setDisplayName("§4"+name);
                }

                List<String> lore = new ArrayList<>(description);
                lore.add("");
                lore.add("§7Click to select");



                switch (firingMode) {
                    case PRIMARY:
                        if (spellSelector.getPlugin().getSpellSelectionRepository().get(new UUID[]{player.getUniqueId(), wandUUID}).getPrimary() == value) {
                            meta.addEnchant(Enchantment.DURABILITY, 10, true);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            lore.remove("§7Click to select");
                            lore.add("§aSelected");
                        }
                        break;
                    case SECONDARY:
                        if (spellSelector.getPlugin().getSpellSelectionRepository().get(new UUID[]{player.getUniqueId(), wandUUID}).getSecondary() == value) {
                            meta.addEnchant(Enchantment.DURABILITY, 10, true);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            lore.remove("§7Click to select");
                            lore.add("§aSelected");
                        }
                        break;
                }

                meta.setLore(lore);


                meta.getPersistentDataContainer().set(spellSelector.getPlugin().getNamespaces().getSpell().getSelectedSpell(), PersistentDataType.STRING, value.name());


                stack.setItemMeta(meta);
                inventory.setItem(i, stack);
            }
        }

        if ((currentPage + 1) * 9 * 3 <= values.size()) {
            ItemStack next = new ItemBuilder(Material.ARROW).setName("§eNext page").build();
            ItemMeta nextMeta = next.getItemMeta();
            nextMeta.getPersistentDataContainer().set(spellSelector.getPlugin().getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE, (byte) 1);
            next.setItemMeta(nextMeta);
            inventory.setItem(35, next);
        }
        if (currentPage > 0) {
            ItemStack previous = new ItemBuilder(Material.IRON_DOOR).setName("§aPrevious page").build();
            ItemMeta previousMeta = previous.getItemMeta();
            previousMeta.getPersistentDataContainer().set(spellSelector.getPlugin().getNamespaces().getSpecial().getPageSelector(), PersistentDataType.BYTE, (byte) 0);
            previous.setItemMeta(previousMeta);
            inventory.setItem(35 - 8, previous);
        }
    }

    public void openInventory() {
        player.openInventory(inventory);
    }


    public List<EnumSpell> getAvailableSpells() {
        return Arrays.stream(EnumSpell.values()).filter(spell -> spell.getSpellLevel() <= wandCategory.getCanHandleSpellLevel()).collect(Collectors.toList());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        List<EnumSpell> availableSpells = getAvailableSpells();
        if (currentPage * 9 * 3 >= availableSpells.size()) {
            this.currentPage = (availableSpells.size() / (9 * 3));
            return;
        }
        this.currentPage = currentPage;
    }

    public WandCategory getWandCategory() {
        return wandCategory;
    }

    public SpellSelector getSpellSelector() {
        return spellSelector;
    }

    public FiringMode getFiringMode() {
        return firingMode;
    }

    public UUID getWandUUID() {
        return wandUUID;
    }
}
