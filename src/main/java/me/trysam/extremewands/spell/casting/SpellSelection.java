package me.trysam.extremewands.spell.casting;

import me.trysam.extremewands.spell.EnumSpell;

public class SpellSelection {

    private EnumSpell primary;
    private EnumSpell secondary;

    public SpellSelection(EnumSpell primary, EnumSpell secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public EnumSpell getPrimary() {
        return primary;
    }

    public void setPrimary(EnumSpell primary) {
        this.primary = primary;
    }

    public EnumSpell getSecondary() {
        return secondary;
    }

    public void setSecondary(EnumSpell secondary) {
        this.secondary = secondary;
    }
}
