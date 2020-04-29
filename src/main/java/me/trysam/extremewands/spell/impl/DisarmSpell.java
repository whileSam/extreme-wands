package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.ParticleRenderer;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.spell.*;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.util.Interpolation;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import me.trysam.extremewands.wand.WandCategory;
import net.minecraft.server.v1_15_R1.ParticleParamRedstone;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.Map;

public class DisarmSpell extends Spell implements Locatable, ParticleHolder {

    private Vector direction;
    private Location location;
    private SimpleParticleRenderer renderer;

    public DisarmSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
        renderer = new SimpleParticleRenderer(new ParticleParamRedstone(.929f, .109f, .141f, 1), true, Vec3d.fromLocation(getCaster().getEyeLocation()), new Vec3f(0.03f, 0.03f, 0.03f), 0.5f, 3);
    }

    @Override
    public void enable() {
        super.enable();
        location = getCaster().getEyeLocation();
        direction = getCaster().getLocation().getDirection();
    }

    @Override
    public void run() {
        Location loc1 = location.clone();
        location.add(direction);
        Location loc2 = location.clone();

        for (float f = 0; f < 1.0f; f+=1.0f/4.0f) {
            Location interpolated = Interpolation.lerpLocation(loc1, loc2, f);

            renderer.getLocation().setX(interpolated.getX());
            renderer.getLocation().setY(interpolated.getY());
            renderer.getLocation().setZ(interpolated.getZ());
            renderer.render(Bukkit.getOnlinePlayers());
        }

        if(location.getBlock().getType() != Material.AIR && location.getBlock().getType() != Material.WATER && location.getBlock().getType() != Material.LAVA) {
            playEndEffect();
            disable();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            LivingEntity foundEntity = location.getWorld().getNearbyEntities(location, 0.4, 0.4, 0.4, entity -> {
                return entity instanceof LivingEntity && entity != getCaster();
            }).stream().map(entity -> (LivingEntity)entity).min(Comparator.comparingDouble(value -> value.getLocation().distance(location))).orElse(null);

            if(foundEntity != null) {
                EntityEquipment equipment = foundEntity.getEquipment();
                if(equipment != null) {
                    if(equipment.getItemInMainHand().getType() != Material.AIR) {

                        Item item = getCaster().getWorld().dropItem(foundEntity.getEyeLocation(), equipment.getItemInMainHand());

                        item.setVelocity(new Vector(0, .5, 0));

                        long initMs = System.currentTimeMillis();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(getCaster() == null || item == null || getCaster().getEyeLocation().distance(item.getLocation()) < 1 || System.currentTimeMillis() - initMs > 1000*20) {
                                    cancel();
                                    return;
                                }
                                item.setVelocity(getCaster().getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize());
                            }
                        }.runTaskTimerAsynchronously(plugin, 5, 5);

                        equipment.setItemInMainHand(null);
                    }
                }
                disable();
            }
        });
    }


    private void playEndEffect() {
        renderer.getSize().setX(1.0f);
        renderer.getSize().setY(1.0f);
        renderer.getSize().setZ(1.0f);
        renderer.setSpeed(0.1f);
        renderer.setAmount(50);
        renderer.render(Bukkit.getOnlinePlayers());
        Bukkit.getOnlinePlayers().forEach(o -> o.playSound(location, Sound.BLOCK_LAVA_EXTINGUISH, 5.0f, 1.0f));
    }

    private void playSuccessEffect() {
        renderer.getSize().setX(1.0f);
        renderer.getSize().setY(1.0f);
        renderer.getSize().setZ(1.0f);
        renderer.setSpeed(0.1f);
        renderer.setAmount(50);
        renderer.render(Bukkit.getOnlinePlayers());
        Bukkit.getOnlinePlayers().forEach(o -> o.playSound(location, Sound.ENTITY_ITEM_PICKUP, 5.0f, 1.0f));
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public ParticleRenderer getRenderer() {
        return renderer;
    }
}
