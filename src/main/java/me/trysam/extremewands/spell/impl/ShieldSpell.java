package me.trysam.extremewands.spell.impl;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.ParticleRenderer;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.particle.renderer.SphereParticleRenderer;
import me.trysam.extremewands.spell.*;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.util.Quaternion;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import me.trysam.extremewands.wand.WandCategory;
import net.minecraft.server.v1_15_R1.ParticleParamRedstone;
import net.minecraft.server.v1_15_R1.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.Map;

public class ShieldSpell extends Spell implements ParticleHolder {

    private Vec3f rotation_axis = new Vec3f(1, 0, 0);
    private Vec3f rotation_axis2 = new Vec3f(0, 0, 1);
    private SphereParticleRenderer renderer;
    private float spellSize = -1;
    private float radius = 2.2f;
    private Vec3d collisionLocation;

    private Quaternion rotationQuaternion;
    private boolean playSound = false;

    public ShieldSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
        renderer = new SphereParticleRenderer(new ParticleParamRedstone(.5f, .98f, 1.0f, .3f), true,
                Vec3d.fromLocation(getCaster().getLocation().add(0, 1, 0)), new Vec3f(0.03f, 0.03f, 0.03f), 0.5f, 1, 24, radius, new Quaternion(0, 1, 0, 0));
    }

    @Override
    public void enable() {
        super.enable();
        if (handler.getActiveSpells(getCaster()).stream().anyMatch(spell -> spell != this && spell instanceof ShieldSpell)) {
            cancelled = true;
            disable();
            return;
        }
        long expire = (long) Math.floor(getProperties().get("shield.duration").getValue());
        if (expire > Short.MAX_VALUE) {
            expire = Short.MAX_VALUE;
        }
        if (expire < 1) {
            expire = 1;
        }

        playSound = true;
        getCaster().getWorld().playSound(getCaster().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.AMBIENT, 1, 1f);

        autoExpireTimeSeconds = (short) expire;
    }

    @Override
    public void run() {
        if (spellSize < 0) {
            checkForSpells();
        }
        if (spellSize < 0) {
            checkForEntities();
        }


        if (spellSize == 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    spellSize += 0.2;
                    if (spellSize > 1.6) {
                        spellSize = -1;
                        cancel();
                    }

                    renderer.render(Bukkit.getOnlinePlayers(), particleRenderer -> {
                        return collisionLocation != null && particleRenderer != null && particleRenderer.getLocation().distanceTo(collisionLocation) < spellSize;
                    }, 24);

                }
            }.runTaskTimerAsynchronously(plugin, 1, 1);
        }
    }

    private void checkForSpells() {
        Spell foundSpell = handler.getSpells(spell -> spell.getCaster() != getCaster() && spell instanceof Locatable &&
                ((Locatable) spell).getLocation().getWorld() == getCaster().getLocation().getWorld() &&
                ((Locatable) spell).getLocation().distance(getCaster().getLocation().add(0, 1, 0)) < radius)
                .stream()
                .findFirst()
                .orElse(null);
        if (foundSpell != null) {
            spellSize = 0;

            Location spellLocation = ((Locatable) foundSpell).getLocation();

            renderer.getLocation().setX(getCaster().getLocation().getX());
            renderer.getLocation().setY(getCaster().getLocation().getY() + 1);
            renderer.getLocation().setZ(getCaster().getLocation().getZ());

            collisionLocation = Vec3d.fromLocation(spellLocation);

            Vec3d origin = renderer.getLocation();

            double rx = spellLocation.getX() - origin.getX();
            double ry = spellLocation.getY() - origin.getY();
            double rz = spellLocation.getZ() - origin.getZ();

            Vec3d rotationVector = new Vec3d(rx, ry, rz).normalized();
            Vec3d rotationOrigin = new Vec3d(0, 0, 1);

            float pitch_radians = (float) -Math.atan2(rotationVector.getY(), Math.sqrt(rotationVector.getX() * rotationVector.getX() + rotationVector.getZ() * rotationVector.getZ()));

            float yaw_radians = (float) Math.acos((rotationVector.getX() * rotationOrigin.getX() + rotationVector.getZ() * rotationOrigin.getZ()) /
                    Math.sqrt(rotationVector.getX() * rotationVector.getX() + rotationVector.getZ() * rotationVector.getZ()) * Math.sqrt(rotationOrigin.getX() * rotationOrigin.getX() + rotationOrigin.getZ() * rotationOrigin.getZ()));

            if (rotationVector.getX() > 0) {
                yaw_radians = (float) (-yaw_radians + 2 * Math.PI);
            }

            rotationQuaternion = new Quaternion(rotation_axis, 90)
                    .multiplied(new Quaternion(rotation_axis2, Math.toDegrees(yaw_radians)))
                    .multiplied(new Quaternion(rotation_axis, Math.toDegrees(pitch_radians)));
            renderer.setRotation(rotationQuaternion);
            foundSpell.disable();
            playCollisionEffect(collisionLocation);
        }
    }

    private void checkForEntities() {
        new BukkitRunnable() {
            @Override
            public void run() {        getCaster().getNearbyEntities(radius, radius, radius).stream()
                    .filter(entity1 -> (entity1 instanceof Projectile && ((Projectile) entity1).getShooter() != getCaster() && !entity1.isOnGround()) || entity1 instanceof Monster || entity1 instanceof Player).min(Comparator.comparingDouble(value -> value.getLocation().distance(getCaster().getLocation())))
                    .ifPresent(entity1 -> {
                        Vector vector = new Vector();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                spellSize = 0;

                                Location entityLocation = entity1.getLocation().add(0, entity1.getHeight()/2,0);

                                renderer.getLocation().setX(getCaster().getLocation().getX());
                                renderer.getLocation().setY(getCaster().getLocation().getY() + 1);
                                renderer.getLocation().setZ(getCaster().getLocation().getZ());

                                collisionLocation = Vec3d.fromLocation(entityLocation);

                                Vec3d origin = renderer.getLocation();

                                double rx = entityLocation.getX() - origin.getX();
                                double ry = entityLocation.getY() - origin.getY();
                                double rz = entityLocation.getZ() - origin.getZ();
                                vector.setX(rx);
                                vector.setY(ry);
                                vector.setZ(rz);

                                Vec3d rotationVector = new Vec3d(rx, ry, rz).normalized();
                                Vec3d rotationOrigin = new Vec3d(0, 0, 1);

                                float pitch_radians = (float) -Math.atan2(rotationVector.getY(), Math.sqrt(rotationVector.getX() * rotationVector.getX() + rotationVector.getZ() * rotationVector.getZ()));

                                float yaw_radians = (float) Math.acos((rotationVector.getX() * rotationOrigin.getX() + rotationVector.getZ() * rotationOrigin.getZ()) /
                                        Math.sqrt(rotationVector.getX() * rotationVector.getX() + rotationVector.getZ() * rotationVector.getZ()) * Math.sqrt(rotationOrigin.getX() * rotationOrigin.getX() + rotationOrigin.getZ() * rotationOrigin.getZ()));

                                if (rotationVector.getX() > 0) {
                                    yaw_radians = (float) (-yaw_radians + 2 * Math.PI);
                                }

                                rotationQuaternion = new Quaternion(rotation_axis, 90)
                                        .multiplied(new Quaternion(rotation_axis2, Math.toDegrees(yaw_radians)))
                                        .multiplied(new Quaternion(rotation_axis, Math.toDegrees(pitch_radians)));
                                renderer.setRotation(rotationQuaternion);
                                entity1.setVelocity(vector.normalize().multiply(1.5).setY(.8));
                                playCollisionEffect(collisionLocation);
                            }
                        }.runTaskAsynchronously(plugin);
                    });
            }
        }.runTask(plugin);

    }

    private void playCollisionEffect(Vec3d location) {
        SimpleParticleRenderer simpleParticleRenderer = new SimpleParticleRenderer(Particles.END_ROD, true, location, new Vec3f(0.05f, 0.05f, 0.05f), 0.1f, 20);
        simpleParticleRenderer.render(Bukkit.getOnlinePlayers());
        getCaster().getWorld().playSound(getCaster().getLocation(), Sound.ITEM_TRIDENT_RETURN, SoundCategory.AMBIENT, 1,1);
    }

    @Override
    public void disable() {
        super.disable();

        if (playSound) {
            getCaster().getWorld().playSound(getCaster().getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.AMBIENT, 1, 1f);
        }
    }

    /*
    float pitch_radians = (float) -atan2(direction.getY(), Math.sqrt(direction.getX()*direction.getX() + direction.getZ()*direction.getZ()));
                float yaw_radians = (float) acos((direction.getX()*rotationOrigin.getX()+direction.getZ()*rotationOrigin.getZ())/
                        sqrt(direction.getX()*direction.getX()+direction.getZ()*direction.getZ())*sqrt(rotationOrigin.getX()*rotationOrigin.getX()+rotationOrigin.getZ()*rotationOrigin.getZ()));
                if(direction.getX() > 0) {
                    yaw_radians = (float) (-yaw_radians+2*PI);
                }
     */

    @Override
    public ParticleRenderer getRenderer() {
        return renderer;
    }
}
