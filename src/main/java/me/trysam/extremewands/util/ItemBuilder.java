package me.trysam.extremewands.util;

import com.google.common.collect.Maps;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemBuilder {

    private Material material;
    private int amount = 1;
    private String name;
    private List<String> lore = new ArrayList<>();
    private int damage;
    private boolean unbreakable;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();

    public ItemBuilder(Material material) {
        this.material = material;
    }

    public ItemBuilder(ItemStack stack) {
        material = stack.getType();
        amount = stack.getAmount();
        ItemMeta meta = stack.getItemMeta();
        name = meta.getDisplayName();
        if (meta.getLore() != null) {
            lore = meta.getLore();
        }
        if(meta instanceof Damageable) {
            damage = ((Damageable) meta).getDamage();
        }
        enchantments = stack.getEnchantments();
        unbreakable = meta.isUnbreakable();
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setDamage(short damage) {
        this.damage = damage;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        lore.add(line);
        return this;
    }

    public ItemBuilder addLoreLine(int index, String line) {
        lore.add(index, line);
        return this;
    }

    public ItemBuilder setLoreLine(int index, String line) {
        lore.set(index, line);
        return this;
    }

    public ItemBuilder addLoreLines(String... line) {
        lore.addAll(Arrays.asList(line));
        return this;
    }

    public ItemBuilder addLoreLines(int index, String... line) {
        lore.addAll(index, Arrays.asList(line));
        return this;
    }

    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
        if(enchantments == null) {
            this.enchantments = new HashMap<>();
        }
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(material);
        stack.setAmount(amount);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(damage);
        }
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        stack.addUnsafeEnchantments(enchantments);
        return stack;
    }
}
