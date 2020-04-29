package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.spell.LevelProperty;
import me.trysam.extremewands.spell.Spell;
import me.trysam.extremewands.spell.SpellCategory;
import me.trysam.extremewands.spell.SpellHandler;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.wand.WandCategory;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RevealSpell extends Spell {

    private List<Entity> revealed = new ArrayList<>();

    public RevealSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
    }

    @Override
    public void enable() {
        super.enable();
        long expire = (long) Math.floor(getProperties().get("reveal.duration").getValue());
        float distance = (long) Math.ceil(getProperties().get("reveal.distance").getValue()*getEffectMultiplier());
        if (expire > Short.MAX_VALUE) {
            expire = Short.MAX_VALUE;
        }
        if (expire < 1) {
            expire = 1;
        }
        if(distance < 1) {
            distance = 1;
        }
        autoExpireTimeSeconds = (short) expire;
        revealed.addAll(getCaster().getNearbyEntities(distance, distance, distance));
        final long finalExpire = expire;
        revealed.stream().filter(Objects::nonNull).forEach(entity -> {
            entity.setGlowing(true);
            if(entity instanceof Player) {
                ((Player) entity).sendTitle("§6You have been spotted!", "§eSpotted by "+getCaster().getDisplayName(), 0, (int) finalExpire*20, 0);
                ((Player) entity).playSound(entity.getLocation(), Sound.BLOCK_TRIPWIRE_DETACH, SoundCategory.HOSTILE, 1, 1);
                ((Player) entity).playSound(entity.getLocation(), Sound.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.HOSTILE, 1, 1);
            }
        });
        getCaster().getWorld().playSound(getCaster().getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.AMBIENT, 1, 0.7f);
    }

    @Override
    public void disable() {
        super.disable();
        revealed.stream().filter(Objects::nonNull).forEach(entity -> {
            entity.setGlowing(false);
            if(entity instanceof Player) {
                ((Player) entity).resetTitle();
            }
        });
    }

    @Override
    public void run() {

    }

    public List<Entity> getRevealed() {
        return revealed;
    }
}
