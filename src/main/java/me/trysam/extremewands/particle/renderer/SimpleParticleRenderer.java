package me.trysam.extremewands.particle.renderer;

import me.trysam.extremewands.particle.Particle;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import net.minecraft.server.v1_15_R1.ParticleParam;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SimpleParticleRenderer extends ParticleRenderer {

    public SimpleParticleRenderer(ParticleParam particleParam, boolean far, Vec3d location, Vec3f size, float speed, int amount) {
        super(particleParam, far, location, size, speed, amount);
    }

    public void render(Collection<? extends Player> players) {
        Particle particle = new Particle(particleParam, far, location.getX(), location.getY(), location.getZ(), size.getX(), size.getY(), size.getZ(), speed, amount);
        particle.sendParticle(players);
    }

}
