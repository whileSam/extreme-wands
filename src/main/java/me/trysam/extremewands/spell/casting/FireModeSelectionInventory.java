package me.trysam.extremewands.spell.casting;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.trysam.extremewands.util.ItemBuilder;
import me.trysam.extremewands.wand.WandCategory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class FireModeSelectionInventory implements InventoryHolder {

    private Player player;
    private SpellSelector spellSelector;
    private Inventory inventory;
    private String primarySkinValue;
    private String secondarySkinValue;
    private UUID skullUUID = UUID.randomUUID();
    private WandCategory wandCategory;
    private UUID wandUUID;


    FireModeSelectionInventory(Player player, SpellSelector spellSelector, String title, WandCategory wandCategory, String primarySkinValue, String secondarySkinValue, UUID wandUUID) {
        this.player = player;
        this.spellSelector = spellSelector;
        this.inventory = Bukkit.createInventory(this, 9, title);
        this.primarySkinValue = primarySkinValue;
        this.secondarySkinValue = secondarySkinValue;
        this.wandCategory = wandCategory;
        this.wandUUID = wandUUID;
    }

    public void prepareInventory() {
        inventory.clear();
        ItemStack primaryStack = new ItemBuilder(Material.PLAYER_HEAD).setName("§aPrimary Fire").setLore("§7Click to select primary spell").build();
        ItemStack secondaryStack = new ItemBuilder(Material.PLAYER_HEAD).setName("§eSecondary Fire").setLore("§7Click to select secondary spell").build();
        ItemMeta primaryMeta = primaryStack.getItemMeta();
        ItemMeta secondaryMeta = secondaryStack.getItemMeta();

        primaryMeta.getPersistentDataContainer().set(spellSelector.getPlugin().getNamespaces().getSpecial().getFiringModeSelector(), PersistentDataType.BYTE, (byte) FiringMode.PRIMARY.ordinal());
        secondaryMeta.getPersistentDataContainer().set(spellSelector.getPlugin().getNamespaces().getSpecial().getFiringModeSelector(), PersistentDataType.BYTE, (byte) FiringMode.SECONDARY.ordinal());

        insertSkinValueIntoSkullMeta(primaryMeta, primarySkinValue);
        insertSkinValueIntoSkullMeta(secondaryMeta, secondarySkinValue);
        primaryStack.setItemMeta(primaryMeta);
        secondaryStack.setItemMeta(secondaryMeta);
        inventory.setItem(2, primaryStack);
        inventory.setItem(6, secondaryStack);
    }

    public void openInventory() {
        player.openInventory(inventory);
    }

    private void insertSkinValueIntoSkullMeta(ItemMeta meta, String skinValue) {
        if (!(meta instanceof SkullMeta)) {
            return;
        }
        SkullMeta skullMeta = (SkullMeta) meta;
        GameProfile profile = new GameProfile(skullUUID, meta.hasDisplayName() ? meta.getDisplayName() : "skull_profile");
        profile.getProperties().put("textures", new Property("textures", skinValue, null));
        try {
            Method method = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            method.setAccessible(true);
            method.invoke(skullMeta, profile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public SpellSelector getSpellSelector() {
        return spellSelector;
    }

    public WandCategory getWandCategory() {
        return wandCategory;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getWandUUID() {
        return wandUUID;
    }
}
