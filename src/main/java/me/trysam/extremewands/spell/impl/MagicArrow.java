package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.ParticleRenderer;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.spell.*;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import me.trysam.extremewands.wand.WandCategory;
import net.minecraft.server.v1_15_R1.ParticleParamRedstone;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MagicArrow extends Spell implements Locatable, ParticleHolder {

    private Location location;
    private Vector direction;
    private LivingEntity targetedEntity;
    private SimpleParticleRenderer renderer;
    private Arrow arrow;

    private Location lastLocation;

    private int jumps = 0;
    private int maxJumps;
    private double damage;

    private List<LivingEntity> damaged = new LinkedList<>();

    public MagicArrow(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
        location = getCaster().getEyeLocation();
        direction = getCaster().getLocation().getDirection();
        renderer = new SimpleParticleRenderer(new ParticleParamRedstone(.863f, .078f, .235f, .5f), true, Vec3d.fromLocation(location), new Vec3f(.02f, .02f, .02f), .05f, 1);
    }

    @Override
    public void enable() {
        super.enable();
        maxJumps = (int) Math.floor(getProperties().get("magicarrow.maxJumps").getValue());
        if(maxJumps < 0) {
            maxJumps = 0;
        }
        damage = getProperties().get("magicarrow.damage").getValue()*getEffectMultiplier();
        arrow = getCaster().getWorld().spawnArrow(location, direction, 1, 0);
        arrow.setGravity(false);
        arrow.setVelocity(direction);
        arrow.setShooter(getCaster());
        arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
    }

    @Override
    public void run() {
        if(arrow == null) {
            disable();
            return;
        }
        if(arrow.isOnGround()) {
            disable();
            return;
        }
        if(getCaster() == null) {
            disable();
            return;
        }
        if(targetedEntity != null) {
            direction = targetedEntity.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize();
        }
        arrow.getLocation().setDirection(direction);
        arrow.setVelocity(direction);
        renderer.setLocation(Vec3d.fromLocation(arrow.getLocation()));
        renderer.render(Bukkit.getOnlinePlayers());
        new BukkitRunnable() {
            @Override
            public void run() {
                LivingEntity entity = arrow.getNearbyEntities(.6, .6, .6).stream()
                        .filter(entity1 -> entity1 instanceof LivingEntity && entity1 != getCaster())
                        .map(entity1 -> (LivingEntity)entity1)
                        .min(Comparator.comparingDouble(value -> value.getLocation().distance(arrow.getLocation())))
                        .orElse(null);
                if(entity != null) {
                    entity.damage(damage, getCaster());
                    damaged.add(entity);
                    System.out.println("damaged: "+entity.getName());
                    if(jumps >= maxJumps) {
                        disable();
                        return;
                    }
                    LivingEntity nextTarget = arrow.getNearbyEntities(50, 50, 50).stream().filter(entity1 -> {
                        if(entity1 == getCaster()) {
                            return false;
                        }
                        if(entity1 instanceof Monster || entity1 instanceof Player) {
                            if(damaged.contains(entity1)) {
                                return false;
                            }
                            RayTraceResult result = ((LivingEntity) entity1).rayTraceBlocks(51, FluidCollisionMode.ALWAYS);
                            if(result == null) {
                                return true;
                            }
                            if(result.getHitBlock() == null) {
                                return true;
                            }
                            return !result.getHitBlock().getType().isSolid();
                        }
                        return false;
                    }).map(entity1 -> (LivingEntity)entity1)
                            .min(Comparator.comparingDouble(value -> value.getLocation().distance(arrow.getLocation())))
                            .orElse(null);
                    if(nextTarget == null) {
                        disable();
                        return;
                    }
                    System.out.println(nextTarget.getName());
                    direction = nextTarget.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize();
                    targetedEntity = nextTarget;
                    jumps++;
                }
            }
        }.runTask(plugin);
    }

    @Override
    public void disable() {
        if(arrow != null) {
            arrow.setVelocity(new Vector(0, 0, 0));
            arrow.setGravity(true);
        }
        super.disable();
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
