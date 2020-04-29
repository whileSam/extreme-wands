package me.trysam.extremewands.spell.data;

import me.trysam.extremewands.config.SimpleConfig;
import me.trysam.extremewands.spell.EnumSpell;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class PersistentSpellData {

    private SimpleConfig simpleConfig;
    private String sectionPath;

    public PersistentSpellData(SimpleConfig simpleConfig, String sectionPath) {
        this.simpleConfig = simpleConfig;
        this.sectionPath = sectionPath;
    }

    public ConfigurationSection getConfigSection() {
        FileConfiguration cfg = simpleConfig.getFileConfiguration();
        ConfigurationSection section = cfg.getConfigurationSection(sectionPath);
        if(section == null) {
            cfg.createSection(sectionPath);
        }
        return section;
    }


    public static class Builder {
        private SimpleConfig configuration;
        private EnumSpell spell;

        public Builder(EnumSpell spell) {
            this.spell = spell;
        }

        public Builder setConfiguration(SimpleConfig configuration) {
            this.configuration = configuration;
            return this;
        }

        public PersistentSpellData build() {
            return new PersistentSpellData(configuration, "spellPersistentData."+spell.name());
        }
    }
}
