package me.trysam.extremewands.spell;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.config.SimpleConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class LevelProperty extends SimpleLevelProperty{

    private ExtremeWandsPlugin plugin;

    private Player player;

    private double value;

    public LevelProperty(ExtremeWandsPlugin plugin, Player player, String id, String infoID, double defaultValue, double step, double min, double max) {
        super(id, infoID, defaultValue, step, min, max);
        this.plugin = plugin;
        this.player = player;
        readValue();
    }

    public LevelProperty(ExtremeWandsPlugin plugin, Player player, SimpleLevelProperty levelProperty) {
        this(plugin, player, levelProperty.id, levelProperty.infoID, levelProperty.defaultValue, levelProperty.step, levelProperty.min, levelProperty.max);
    }


    public void readValue() {
        FileConfiguration cfg = plugin.getPluginConfig().getFileConfiguration();
        if(!cfg.contains("playerSettings."+player.getUniqueId()+".spellProperties."+id)) {
            value = defaultValue;
            saveValue();
            return;
        }
        value = cfg.getDouble("playerSettings."+player.getUniqueId()+".spellProperties."+id);
    }

    public void saveValue() {
        SimpleConfig config = plugin.getPluginConfig();
        FileConfiguration cfg = config.getFileConfiguration();
        cfg.set("playerSettings."+player.getUniqueId()+".spellProperties."+id, value);
        config.save();
    }

    public void increaseValue(int steps) {
        setValue(getValue()+(steps*step));
    }


    public boolean setValue(double value) {
        boolean returnValue = true;
        if(value > max) {
            value = max;
            returnValue = false;
        }
        if(value < min) {
            value = min;
            returnValue = false;
        }
        this.value = value;
        this.value = Math.round(this.value * 100000d) / 100000d;
        return returnValue;
    }

    public double getValue() {
        return value;
    }
}
