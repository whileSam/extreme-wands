package me.trysam.extremewands.wand;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import net.minecraft.server.v1_15_R1.Particles;
import net.minecraft.server.v1_15_R1.RayTrace;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class WandActivator {

    private ExtremeWandsPlugin plugin;

    @Inject
    private WandActivator(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void activateWand(Player player, Location location, ItemStack itemStack) {
        if (Objects.isNull(itemStack)) {
            throw new IllegalArgumentException("ItemStack MUST NOT be null!");
        }
        WandCategory category = null;
        switch (itemStack.getType()) {
            case STICK:
                category = WandCategory.WOOD;
                break;
            case BONE:
                category = WandCategory.BONE;
                break;
            case BLAZE_ROD:
                category = WandCategory.BLAZE;
                break;
            case END_ROD:
                category = WandCategory.ENDER;
                break;
            default:
                break;
        }
        if (category != null) {

            ItemMeta meta = itemStack.getItemMeta();

            PersistentDataContainer container = meta.getPersistentDataContainer();

            NamespacedKey wandType = plugin.getNamespaces().getWand().getType();
            NamespacedKey wandOwner = plugin.getNamespaces().getWand().getOwner();
            NamespacedKey keyEffectMultiplier = plugin.getNamespaces().getWand().getEffectMultiplier();
            NamespacedKey keyProtectionMultiplier = plugin.getNamespaces().getWand().getProtectionMultiplier();
            NamespacedKey wandUuid = plugin.getNamespaces().getWand().getWandUuid();

            Random random = new Random();
            double effectVariation = category.getEffect_multiplier_variation();
            double effectMultiplier = category.getBase_effect_multiplier() + (random.nextDouble() * (effectVariation + effectVariation)) - effectVariation;
            double protectionVariation = category.getProtection_multiplier_variation();
            double protectionMultiplier = category.getBase_protection_multiplier() + (random.nextDouble() * (protectionVariation + protectionVariation)) - protectionVariation;

            container.set(wandType, PersistentDataType.INTEGER, category.ordinal());
            container.set(wandOwner, PersistentDataType.STRING, player.getUniqueId().toString());
            container.set(keyEffectMultiplier, PersistentDataType.DOUBLE, effectMultiplier);
            container.set(keyProtectionMultiplier, PersistentDataType.DOUBLE, protectionMultiplier);
            container.set(plugin.getNamespaces().getWand().getIsActivated(), PersistentDataType.BYTE, (byte) 1);
            if(!container.has(wandUuid, PersistentDataType.STRING)) {
                container.set(wandUuid, PersistentDataType.STRING, UUID.randomUUID().toString());
            }

            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.HALF_UP);


            meta.setLore(Arrays.asList("§5§kwand §e§lactivated §5§kwand", "§7Effect§8: §ex" + df.format(effectMultiplier), "§7Protection§8: §ex" + df.format(protectionMultiplier)));

            meta.addEnchant(Enchantment.DURABILITY, 10, true);
            itemStack.setItemMeta(meta);


            if (category == WandCategory.ENDER) {
                playEnderEffect(location, player, random);
            } else {
                location.getWorld().playSound(location, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.AMBIENT, 2f, 1.0f);
                defaultEffect(location, 0);
            }
        }
    }

    private void playEnderEffect(Location location, Player player, Random random) {
        int oldClear = ((CraftWorld) player.getWorld()).getHandle().getWorldData().z();
        ((CraftWorld) player.getWorld()).getHandle().getWorldData().g(0);
        player.getWorld().setWeatherDuration(20 * 15);
        player.getWorld().setThunderDuration(20 * 15);
        player.getWorld().setStorm(true);
        player.getWorld().setThundering(true);
        for (int i = 0; i < 20; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (random.nextBoolean()) {
                    player.getWorld().setTime(6000);
                    double varX = random.nextDouble() * (3 + 3) - 3;
                    double varZ = random.nextDouble() * (3 + 3) - 3;
                    location.getWorld().strikeLightningEffect(location.add(new Vector(varX, 0, varZ)));
                } else {
                    player.getWorld().setTime(18000);
                    location.getWorld().strikeLightningEffect(player.getEyeLocation());
                }
            }, random.nextInt(10) * i);
        }
        location.getWorld().playSound(location, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.AMBIENT, 5.0f, 1.0f);
        new BukkitRunnable() {
            @Override
            public void run() {
                ((CraftWorld) player.getWorld()).getHandle().getWorldData().g(oldClear);
                player.getWorld().setWeatherDuration(0);
                player.getWorld().setThunderDuration(0);
                player.getWorld().setStorm(false);
                player.getWorld().setThundering(false);
                player.getWorld().setTime(18000);
                for (int i = 0; i < 10; i++) {
                    Phantom phantom = player.getWorld().spawn(location.clone().add(random.nextDouble() * (15+15) - 15, random.nextDouble()*(20-12)+12, random.nextDouble() * (15+15) - 15), Phantom.class);
                    phantom.setSize(random.nextInt(25-5)+5);
                    phantom.setTarget(player);
                }
                spawnRandomMonster(location.getWorld(), location.getX(), location.getZ(), 25, 30, random, player,
                        Zombie.class, ZombieVillager.class, Skeleton.class, Creeper.class, Spider.class, CaveSpider.class,
                        Witch.class, Vex.class, Vindicator.class, Enderman.class);
            }
        }.runTaskLater(plugin, 20 * 10);
        Vec3d loc = Vec3d.fromLocation(location.clone().add(0.5, 0.5, 0.5));
        Vec3f size = new Vec3f(35f, 35f, 35f);
        SimpleParticleRenderer simpleParticleRenderer = new SimpleParticleRenderer(Particles.END_ROD, true, loc, size, 0.5f, 450);
        new BukkitRunnable() {
            @Override
            public void run() {
                simpleParticleRenderer.render(Bukkit.getOnlinePlayers());
                makeMonstersAngry(location.clone().add(0.5, 0.5, 0.5), 35, player);
            }
        }.runTaskAsynchronously(plugin);
    }

    @SafeVarargs
    private final void spawnRandomMonster(World world, double x, double z, double radius, int amount, Random random, LivingEntity target, Class<? extends Monster>... monsters) {
        for (int i = 0; i < amount; i++) {
            Location spawnLocation = new Location(world, x + random.nextDouble() * (radius+radius)-radius, world.getHighestBlockYAt((int)x, (int)z)+0.5, z + random.nextDouble() * (radius+radius)-radius);
            Monster monster = world.spawn(spawnLocation, monsters[random.nextInt(monsters.length)]);
            monster.setTarget(target);
        }
    }

    public void defaultEffect(Location location, int delay) {
        Vec3d loc = Vec3d.fromLocation(location.clone().add(0.5, 0.5, 0.5));
        Vec3f size = new Vec3f(20f, 20f, 20f);
        SimpleParticleRenderer simpleParticleRenderer = new SimpleParticleRenderer(Particles.ENCHANT, true, loc, size, 0.5f, 450);
        new BukkitRunnable() {
            @Override
            public void run() {
                simpleParticleRenderer.render(Bukkit.getOnlinePlayers());
                pushEntities(location.clone().add(0.5, 0.5, 0.5), 20);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void pushEntities(Location location, double radius) {
        new BukkitRunnable() {
            @Override
            public void run() {
                location.getWorld().getNearbyEntities(location, radius + 2, radius + 2, radius + 2).forEach(entity -> {
                    double rx = entity.getLocation().getX() - location.getX();
                    double ry = entity.getLocation().getY() - location.getY();
                    double rz = entity.getLocation().getZ() - location.getZ();
                    Vector vector = new Vector(rx, ry, rz).normalize().multiply(2).setY(1.5);
                    entity.setVelocity(vector);
                });

            }
        }.runTask(plugin);
    }

    public void makeMonstersAngry(Location location, double radius, LivingEntity target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                location.getWorld().getNearbyEntities(location, radius + 8, radius + 8, radius + 8, entity -> entity instanceof Monster).forEach(entity -> {
                    ((Monster) entity).setTarget(target);
                });

            }
        }.runTask(plugin);
    }


}
