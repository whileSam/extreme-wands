package me.trysam.extremewands.particle.renderer;

import me.trysam.extremewands.util.Axis;
import me.trysam.extremewands.util.Quaternion;
import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import net.minecraft.server.v1_15_R1.ParticleParam;
import org.bukkit.entity.Player;

import java.util.Collection;

import static java.lang.Math.*;

public class CircleParticleRenderer extends ParticleRenderer {

    private int elements;
    private double radius;
    private SimpleParticleRenderer renderer;
    private Quaternion rotation;

    public CircleParticleRenderer(ParticleParam particleParam, boolean far, Vec3d location, Vec3f size, float speed, int amount, int elements, double radius, Quaternion rotation) {
        super(particleParam, far, location, size, speed, amount);
        this.rotation = rotation;
        renderer = new SimpleParticleRenderer(particleParam, far, location, size, speed, amount);
        this.elements = elements;
        this.radius = radius;
    }

    @Override
    public void render(Collection<? extends Player> players) {
        double centerX = this.getLocation().getX();
        double centerY = this.getLocation().getY();
        double centerZ = this.getLocation().getZ();
        for (double theta = 0; theta < elements; theta += 2 * PI / elements) {
            double x = getRadius() * cos(theta);
            double y = 0;
            double z = getRadius() * sin(theta);
            Quaternion point = new Quaternion(0, (float)x, (float)y, (float)z);
            Quaternion point_rotated = rotation.multiplied(point).multiplied(rotation.getInverse());
            x = point_rotated.getX();
            y = point_rotated.getY();
            z = point_rotated.getZ();
            renderer.getLocation().setX(centerX + x);
            renderer.getLocation().setY(centerY + y);
            renderer.getLocation().setZ(centerZ + z);
            renderer.render(players);
        }
    }

    public int getElements() {
        return elements;
    }

    public void setElements(int elements) {
        this.elements = elements;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}
