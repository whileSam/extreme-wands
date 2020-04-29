package me.trysam.extremewands.particle.renderer;

import me.trysam.extremewands.util.Quaternion;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import net.minecraft.server.v1_15_R1.ParticleParam;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

import static java.lang.Math.*;

public class SphereParticleRenderer extends ParticleRenderer {

    private int elements;
    private double radius;
    private SimpleParticleRenderer renderer;
    private Quaternion rotation;

    public SphereParticleRenderer(ParticleParam particleParam, boolean far, Vec3d location, Vec3f size, float speed, int amount, int elements, double radius, Quaternion rotation) {
        super(particleParam, far, location, size, speed, amount);
        this.radius = radius;
        this.elements = elements;
        this.rotation = rotation;
        renderer = new SimpleParticleRenderer(particleParam, far, location, size, speed, amount);
    }

    @Override
    public void render(Collection<? extends Player> players) {
        render(players, null);
    }

    public void render(Collection<? extends Player> players, Predicate<ParticleRenderer> predicate) {
        render(players, predicate, (double) elements / 2.0);
    }

    public void render(Collection<? extends Player> players, Predicate<ParticleRenderer> predicate, double rings) {
        final double centerX = this.getLocation().getX();
        final double centerY = this.getLocation().getY();
        final double centerZ = this.getLocation().getZ();
        double radius = getRadius();
        for (double phi = 0; phi <= PI; phi += PI / rings) {
            for (double theta = 0; theta < elements; theta += 2 * PI / elements) {
                double x = radius * cos(theta) * sin(phi);
                double y = radius * cos(phi);
                double z = radius * sin(theta) * sin(phi);
                Quaternion point = new Quaternion(0, (float) x, (float) y, (float) z);
                Quaternion point_rotated = rotation.multiplied(point).multiplied(rotation.getInverse());
                x = point_rotated.getX();
                y = point_rotated.getY();
                z = point_rotated.getZ();
                renderer.getLocation().setX(centerX + x);
                renderer.getLocation().setY(centerY + y);
                renderer.getLocation().setZ(centerZ + z);
                if(predicate == null) {
                    renderer.render(players);
                }else {
                    if(predicate.test(renderer)) {
                        renderer.render(players);
                    }
                }
                renderer.getLocation().setX(centerX);
                renderer.getLocation().setY(centerY);
                renderer.getLocation().setZ(centerZ);
            }
        }
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }
}
