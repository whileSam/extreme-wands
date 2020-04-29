package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.spell.LevelProperty;
import me.trysam.extremewands.spell.Spell;
import me.trysam.extremewands.spell.SpellCategory;
import me.trysam.extremewands.spell.SpellHandler;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.wand.WandCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

public class TelekinesisSpell extends Spell {

    private Entity affectedEntity;
    private boolean playSound;

    public TelekinesisSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
    }

    @Override
    public void enable() {
        super.enable();
        Spell other = handler.getActiveSpells(getCaster()).stream().filter(spell -> spell != this && spell instanceof TelekinesisSpell).findAny().orElse(null);
        if (other != null) {
            other.disable();
            cancelled = true;
            disable();
            return;
        }

    }

    @Override
    public void run() {

    }
}
