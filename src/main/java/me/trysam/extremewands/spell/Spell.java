package me.trysam.extremewands.spell;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.wand.WandCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class Spell implements Runnable {

    protected ExtremeWandsPlugin plugin;
    protected SpellHandler handler;
    private Map<String, LevelProperty> properties;
    private boolean active = false;
    private Player caster;
    private WandCategory castedWith;
    private SpellCategory spellCategory;
    private long activationTime;
    protected short autoExpireTimeSeconds = 30;
    protected boolean cancelled = false;

    private float effectMultiplier;
    private float protectionMultiplier;

    protected PersistentSpellData spellData;

    public Spell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        this.plugin = plugin;
        this.handler = handler;
        this.properties = properties == null ? new HashMap<>() : properties;
        this.caster = caster;
        this.castedWith = castedWith;
        this.spellCategory = spellCategory;
        this.effectMultiplier = effectMultiplier;
        this.protectionMultiplier = protectionMultiplier;
        this.spellData = spellData;
    }

    public void enable() {
        activationTime = System.currentTimeMillis();
        active = true;
    }

    public void disable() {
        active = false;
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - activationTime > 1000*autoExpireTimeSeconds;
    }

    public Map<String, LevelProperty> getProperties() {
        return properties;
    }

    public boolean isActive() {
        return active;
    }

    public Player getCaster() {
        return caster;
    }

    public WandCategory getCastedWith() {
        return castedWith;
    }

    public SpellCategory getSpellCategory() {
        return spellCategory;
    }

    public float getEffectMultiplier() {
        return effectMultiplier;
    }

    public float getProtectionMultiplier() {
        return protectionMultiplier;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
