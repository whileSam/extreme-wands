package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.ParticleRenderer;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.spell.*;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.wand.WandCategory;
import me.trysam.extremewands.util.Interpolation;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import net.minecraft.server.v1_15_R1.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;

public class FireSpell extends Spell implements Locatable, ParticleHolder {

    private Location location;

    private ParticleRenderer particleRenderer;

    public FireSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
        location = caster.getEyeLocation();
        Vec3d loc = new Vec3d(location.getX(), location.getY(), location.getZ());
        Vec3f size = new Vec3f(0, 0, 0);
        particleRenderer = new SimpleParticleRenderer(Particles.FLAME, true, loc, size, 0, 10);
    }

    @Override
    public void run() {
        Location loc1 = location.clone();
        location.add(getCaster().getLocation().getDirection());
        Location loc2 = location.clone();

        for (float f = 0; f < 1.0f; f+=1.0f/4.0f) {
            Location interpolated = Interpolation.lerpLocation(loc1, loc2, f);

            particleRenderer.getLocation().setX(interpolated.getX());
            particleRenderer.getLocation().setY(interpolated.getY());
            particleRenderer.getLocation().setZ(interpolated.getZ());
            particleRenderer.render(Bukkit.getOnlinePlayers());
        }

        if(location.getBlock().getType() != Material.AIR && location.getBlock().getType() != Material.WATER && location.getBlock().getType() != Material.LAVA) {
            Block block = location.getWorld().getBlockAt(location.getBlockX(), location.getBlockY()+1, location.getBlockZ());
            if(block.getType() == Material.AIR) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        block.setType(Material.FIRE);
                    }
                }.runTask(plugin);
            }else {
                playEndEffect();
            }
            disable();
        }else if (location.getBlock().getType() == Material.WATER || location.getBlock().getType() == Material.LAVA) {
            playEndEffect();
            disable();
        }

        Bukkit.getScheduler().runTask(plugin, () -> {

            Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5, entity -> entity != getCaster());
            if(!nearbyEntities.isEmpty()) {
                nearbyEntities.forEach(entity -> entity.setFireTicks((int)Math.round(getProperties().get("fire.duration").getValue()*getEffectMultiplier()*20)));
                disable();
            }
        });

    }

    private void playEndEffect() {
        particleRenderer.getSize().setX(1.0f);
        particleRenderer.getSize().setY(1.0f);
        particleRenderer.getSize().setZ(1.0f);
        particleRenderer.setSpeed(0.1f);
        particleRenderer.setAmount(50);
        particleRenderer.render(Bukkit.getOnlinePlayers());
        Bukkit.getOnlinePlayers().forEach(o -> o.playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, 5.0f, 1.0f));
    }


    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public ParticleRenderer getRenderer() {
        return particleRenderer;
    }
}
