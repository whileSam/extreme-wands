package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.ParticleRenderer;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.spell.*;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import me.trysam.extremewands.wand.WandCategory;
import net.minecraft.server.v1_15_R1.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.stream.Collectors;

public class EmpowerSpell extends Spell implements ParticleHolder {

    SimpleParticleRenderer renderer;

    public EmpowerSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
        renderer = new SimpleParticleRenderer(Particles.FLAME, true,
                Vec3d.fromLocation(getCaster().getLocation().add(0, 1f, 0)), new Vec3f(0.3f, 0.5f, 0.3f), 0.03f, 5);
    }

    @Override
    public void enable() {
        super.enable();
        Player player = getCaster();
        if(handler.getActiveSpells(player).stream().anyMatch(spell -> spell != this && spell instanceof EmpowerSpell)) {
            cancelled = true;
            disable();
            return;
        }
        long expire = (long) Math.floor(getProperties().get("empower.duration").getValue()*getEffectMultiplier());
        if(expire > Short.MAX_VALUE) {
            expire = Short.MAX_VALUE;
        }

        if(expire < 1) {
            expire = 1;
        }

        autoExpireTimeSeconds = (short) expire;


        int amplifier = (int) Math.ceil(getProperties().get("empower.strength").getValue());
        if(amplifier < 1) {
            amplifier = 1;
        }
        if(amplifier > 3) {
            amplifier = 3;
        }

        PotionEffect protection = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) expire, amplifier,
                false, false, false);
        PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (int) expire, amplifier,
                false, false, false);

        double health = 20 + Math.floor(5*getEffectMultiplier());


        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        player.setHealth(health);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, SoundCategory.AMBIENT, 5f, 1);
    }

    @Override
    public void disable() {
        Player player = getCaster();
        player.setHealth(20.0);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        super.disable();
    }

    @Override
    public void run() {
        Player player = getCaster();
        renderer.getLocation().setX(getCaster().getLocation().getX());
        renderer.getLocation().setY(getCaster().getLocation().getY()+1);
        renderer.getLocation().setZ(getCaster().getLocation().getZ());
        renderer.render(Bukkit.getOnlinePlayers());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.AMBIENT, 0.3f, 0.8f);
    }

    @Override
    public ParticleRenderer getRenderer() {
        return renderer;
    }
}
