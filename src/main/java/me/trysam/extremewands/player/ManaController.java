package me.trysam.extremewands.player;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.particle.renderer.SimpleParticleRenderer;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import net.minecraft.server.v1_15_R1.Particles;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

public class ManaController {


    private ExtremeWandsPlugin plugin;

    @Inject
    private ManaController(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void startManaProvider(int manaPerSecond, int period) {
        int radius = 5;
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(o -> {
                    UUID uuid = o.getUniqueId();
                    ManaRepository manaRepository = plugin.getManaRepository();
                    for (int i = -radius; i < radius + 1; i++) {
                        for (int j = -radius; j < radius + 1; j++) {
                            for (int k = -radius; k < radius + 1; k++) {
                                if(o.getWorld().getBlockAt(o.getLocation().getBlockX()+i, o.getLocation().getBlockY() +j, o.getLocation().getBlockZ()+k).getType() == Material.ENCHANTING_TABLE) {
                                    manaRepository.set(uuid, manaRepository.get(uuid)+Math.round(manaPerSecond*(period/20f)));
                                    playEnchantEffect(o.getEyeLocation(), 25, 0.25f);
                                }
                            }
                        }
                    }
                });
            }
        }.runTaskTimerAsynchronously(plugin, 0, period);
    }

    public void playEnchantEffect(Location location, int amount, float volume) {
        SimpleParticleRenderer renderer = new SimpleParticleRenderer(Particles.ENCHANT, true, Vec3d.fromLocation(location), new Vec3f(0, 0, 0), 1, 0);
        for (int i = 0; i < amount; i++) {
            renderer.getSize().setX((float) (Math.random() * (5f+5f) - 5f));
            renderer.getSize().setY((float) (Math.random() * (5f+1f) - 1f));
            renderer.getSize().setZ((float) (Math.random() * (5f+5f) - 5f));
            renderer.render(Bukkit.getOnlinePlayers());
        }
        location.getWorld().playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.AMBIENT, volume, 0.8f);
    }
}
