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
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;

import java.util.Map;

public class RepairSpell extends Spell {

    public RepairSpell(ExtremeWandsPlugin plugin, SpellHandler handler, Map<String, LevelProperty> properties, Player caster, WandCategory castedWith, SpellCategory spellCategory, float effectMultiplier, float protectionMultiplier, PersistentSpellData spellData) {
        super(plugin, handler, properties, caster, castedWith, spellCategory, effectMultiplier, protectionMultiplier, spellData);
        autoExpireTimeSeconds = 10;
    }

    @Override
    public void enable() {
        super.enable();
        healItem();
        disable();
    }

    private void healItem() {
        Player player = getCaster();
        World world = player.getWorld();
        RayTraceResult result = world.rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), 25, FluidCollisionMode.NEVER, true, 0.2, entity -> entity instanceof Item);
        if(result != null) {
            if(result.getHitEntity() != null) {
                Item item = (Item) result.getHitEntity();
                ItemStack stack = item.getItemStack();
                ItemMeta meta = stack.getItemMeta();
                if(meta instanceof Damageable) {
                    int damage = (int) (((Damageable) meta).getDamage()-Math.ceil(getProperties().get("repair.damage").getValue()*getEffectMultiplier()));
                    if(damage < 0) {
                        damage = 0;
                    }
                    ((Damageable) meta).setDamage(damage);
                    SimpleParticleRenderer renderer = new SimpleParticleRenderer(Particles.HAPPY_VILLAGER, true,
                            Vec3d.fromBukkitVector(result.getHitPosition()), new Vec3f(0.75f, 0.75f, 0.75f),
                            0.15f, 15);
                    renderer.render(Bukkit.getOnlinePlayers());
                    world.playSound(new Location(world, result.getHitPosition().getX(), result.getHitPosition().getY(), result.getHitPosition().getZ()),
                            Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.AMBIENT, 1, 1);
                    stack.setItemMeta(meta);
                }
            }else {
                SimpleParticleRenderer renderer = new SimpleParticleRenderer(Particles.SMOKE, true,
                        Vec3d.fromBukkitVector(result.getHitPosition()), new Vec3f(0.75f, 0.75f, 0.75f),
                        0.15f, 15);
                renderer.render(Bukkit.getOnlinePlayers());
                world.playSound(new Location(world, result.getHitPosition().getX(), result.getHitPosition().getY(), result.getHitPosition().getZ()),
                        Sound.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.AMBIENT, 1, 1);
            }
        }
    }

    @Override
    public void run() {

    }
}
