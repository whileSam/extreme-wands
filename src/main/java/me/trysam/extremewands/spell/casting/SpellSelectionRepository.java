package me.trysam.extremewands.spell.casting;

import me.trysam.extremewands.config.SimpleConfig;
import me.trysam.extremewands.spell.EnumSpell;
import me.trysam.extremewands.util.Repository;

import java.util.UUID;

public class SpellSelectionRepository implements Repository<UUID[], SpellSelection> {


    private SimpleConfig config;

    public SpellSelectionRepository(SimpleConfig config) {
        this.config = config;
    }

    @Override
    public void set(UUID[] key, SpellSelection value) {
        config.getFileConfiguration().set("playerSettings." + key[0].toString() + ".spellSelection.wand." + key[1].toString() + ".primary", value.getPrimary().name());
        config.getFileConfiguration().set("playerSettings." + key[0].toString() + ".spellSelection.wand." + key[1].toString() + ".secondary", value.getSecondary().name());

        config.save();
    }

    @Override
    public SpellSelection get(UUID[] key) {
        return new SpellSelection(EnumSpell.valueOf(config.getFileConfiguration().getString("playerSettings." + key[0].toString() + ".spellSelection.wand." + key[1].toString() + ".primary", "NONE")),
                EnumSpell.valueOf(config.getFileConfiguration().getString("playerSettings." + key[0].toString() + ".spellSelection.wand." + key[1].toString() + ".secondary", "NONE")));
    }

}
