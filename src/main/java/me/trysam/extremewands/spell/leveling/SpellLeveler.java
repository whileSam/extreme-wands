package me.trysam.extremewands.spell.leveling;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.config.SimpleConfig;
import me.trysam.extremewands.spell.EnumSpell;
import me.trysam.extremewands.util.InventoryTitles;
import me.trysam.extremewands.wand.WandCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpellLeveler {

    private Player player;
    protected ExtremeWandsPlugin plugin;

    public SpellLeveler(Player player, ExtremeWandsPlugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public SpellSelectionInventory getSpellSelection(WandCategory wandCategory) {
        return new SpellSelectionInventory(player, InventoryTitles.LEVELING_SPELL_SELECTION, wandCategory, this);
    }

    public SpellLevelingInventory getUpgradeSelection(EnumSpell spell) {
        return new SpellLevelingInventory(player, InventoryTitles.LEVELING_UPGRADE_SELECTION, spell, this);
    }

    public int getSpellLevelPoints(EnumSpell spell) {
        FileConfiguration fileConfiguration = plugin.getPluginConfig().getFileConfiguration();
        if(!fileConfiguration.contains("playerSettings."+player.getUniqueId()+".spellLevelPoints."+spell.name())) {
            setSpellLevelPoints(spell, 0);
        }
        return fileConfiguration.getInt("playerSettings."+player.getUniqueId()+".spellLevelPoints."+spell.name());
    }

    public void setSpellLevelPoints(EnumSpell spell, int points) {
        SimpleConfig pluginConfig = plugin.getPluginConfig();
        FileConfiguration fileConfiguration = pluginConfig.getFileConfiguration();
        fileConfiguration.set("playerSettings."+player.getUniqueId()+".spellLevelPoints."+spell.name(), points);
        pluginConfig.save();
    }



}
