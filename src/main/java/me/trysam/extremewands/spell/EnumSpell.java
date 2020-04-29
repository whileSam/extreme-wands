package me.trysam.extremewands.spell;

import me.trysam.extremewands.spell.impl.*;

public enum EnumSpell {

//    FIRE(FireSpell.class, 1, false, SpellCategory.UTIL, new String[]{"fire.manaCost","fire.duration"}, new String[]{"manaCost","fire.duration"}, new Double[]{0.05, 2.0}, new Double[]{-0.001, 1.0}, new Double[]{0.0, 0.0}, new Double[]{0.05, 15.0}, new Float[]{1f, 1f}, new Float[]{1f, 1f}),
//    REPAIR(RepairSpell.class, 0, false, SpellCategory.UTIL, new String[]{"repair.manaCost", "repair.damage"}, new String[]{"manaCost","repair.damage"}, new Double[]{0.1, 50.0}, new Double[]{-0.01, 10.0}, new Double[]{0.0, 0.0}, new Double[]{0.1, 500.0}, new Float[]{1f, 1f}, new Float[]{1f, 1f});

    NONE(null, 0, false, null, new SimpleLevelProperty[0]),


    FIRE(FireSpell.class, 0, false, SpellCategory.UTIL, new SimpleLevelProperty[]{
            new SimpleLevelProperty("fire.manaCost", "manaCost", 12, -1, 2, 12),
            new SimpleLevelProperty("fire.duration", "fire.duration", 2.0, 1.0, 2.0, 15.0)}),

    SHIELD(ShieldSpell.class, 0, false, SpellCategory.DEFENSE, new SimpleLevelProperty[]{
            new SimpleLevelProperty("shield.manaCost", "manaCost", 15, -1, 6, 15),
            new SimpleLevelProperty("shield.duration", "shield.duration", 2.0, 1.0, 2.0, 8.0)}),

    REPAIR(RepairSpell.class, 1, false, SpellCategory.UTIL, new SimpleLevelProperty[]{
            new SimpleLevelProperty("repair.manaCost", "manaCost", 15, -1, 3, 15),
            new SimpleLevelProperty("repair.damage", "repair.damage", 50.0, 10.0, 50.0, 500.0)}),

    DISARM(DisarmSpell.class, 1, false, SpellCategory.COMBAT, new SimpleLevelProperty[]{
            new SimpleLevelProperty("disarm.manaCost", "manaCost", 15, -1, 3, 15)}),


    REVEAL(RevealSpell.class, 0, false, SpellCategory.UTIL, new SimpleLevelProperty[]{
            new SimpleLevelProperty("reveal.manaCost", "manaCost", 12, -1, 3, 2),
            new SimpleLevelProperty("reveal.duration", "reveal.duration", 1, 1, 1, 5),
            new SimpleLevelProperty("reveal.distance", "reveal.distance", 3, 1, 3, 8)}),


    MAGIC_ARROW(MagicArrow.class, 1, true, SpellCategory.COMBAT, new SimpleLevelProperty[]{
            new SimpleLevelProperty("magicarrow.manaCost", "manaCost", 16, -1, 5, 16),
            new SimpleLevelProperty("magicarrow.maxJumps", "magicarrow.maxJumps", 0, 1, 0, 10),
            new SimpleLevelProperty("magicarrow.damage", "magicarrow.damage", 4, 1, 4, 10)}),


    HEAL(HealSpell.class, 1, false, SpellCategory.DEFENSE, new SimpleLevelProperty[]{
            new SimpleLevelProperty("heal.manaCost", "manaCost", 16, -1, 3, 16),
            new SimpleLevelProperty("heal.amount", "heal.amount", 1.0, 1.0, 1.0, 20.0)}),

    EMPOWER(EmpowerSpell.class, 1, false, SpellCategory.COMBAT, new SimpleLevelProperty[]{
            new SimpleLevelProperty("empower.manaCost", "manaCost", 20, -1, 8, 20),
            new SimpleLevelProperty("empower.duration", "empower.duration", 2.0, 1.0, 2.0, 5.0),
            new SimpleLevelProperty("empower.strength", "empower.strength", 1.0, 1.0, 1.0, 3.0)});






    private Class<? extends Spell> spellClass;
    private int spellLevel;
    private boolean isDarkMagic;
    private SpellCategory spellCategory;
    private SimpleLevelProperty[] levelProperties;

    EnumSpell(Class<? extends Spell> spellClass, int spellLevel, boolean isDarkMagic, SpellCategory spellCategory, SimpleLevelProperty[] levelProperties) {
        this.spellClass = spellClass;
        this.spellLevel = spellLevel;
        this.isDarkMagic = isDarkMagic;
        this.spellCategory = spellCategory;
        this.levelProperties = levelProperties;
    }


    public Class<? extends Spell> getSpellClass() {
        return spellClass;
    }

    public int getSpellLevel() {
        return spellLevel;
    }

    public boolean isDarkMagic() {
        return isDarkMagic;
    }

    public SpellCategory getSpellCategory() {
        return spellCategory;
    }

    public SimpleLevelProperty[] getLevelProperties() {
        return levelProperties;
    }

    public SimpleLevelProperty getLevelPropertyByID(String id) {
        for (SimpleLevelProperty levelProperty : getLevelProperties()) {
            if(levelProperty.getId().equals(id)) {
                return levelProperty;
            }
        }
        return null;
    }
}
