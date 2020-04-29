package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.spell.LevelProperty;
import me.trysam.extremewands.spell.Spell;
import me.trysam.extremewands.spell.SpellCategory;
import me.trysam.extremewands.spell.SpellHandler;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import me.trysam.extremewands.wand.WandCategory;
import net.minecraft.server.v1_15_R1.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Map;

public class HealSpell extends Spell {

    public HealSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
    }


    @Override
    public void enable() {
        super.enable();
        Player player = getCaster();
        double health = player.getHealth() + getProperties().get("heal.amount").getValue()*2*getEffectMultiplier();
        if(health > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()) {
            health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        }
        player.setHealth(health);
        SimpleParticleRenderer renderer = new SimpleParticleRenderer(Particles.HEART, true, Vec3d.fromLocation(player.getLocation().add(0, 0.75, 0)), new Vec3f(1, 1.5f, 1), 0.1f, 10);
        renderer.render(Bukkit.getOnlinePlayers());
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        disable();
    }

    @Override
    public void run() {

    }
}
