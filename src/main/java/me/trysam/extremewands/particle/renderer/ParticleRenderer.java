package me.trysam.extremewands.particle.renderer;

import me.trysam.extremewands.util.Vec3d;
import me.trysam.extremewands.util.Vec3f;
import net.minecraft.server.v1_15_R1.ParticleParam;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class ParticleRenderer {

    protected ParticleParam particleParam;
    protected boolean far;
    protected Vec3d location;
    protected Vec3f size;
    protected float speed;
    protected int amount;

    public ParticleRenderer(ParticleParam particleParam, boolean far, Vec3d location, Vec3f size, float speed, int amount) {
        this.particleParam = particleParam;
        this.far = far;
        this.location = location;
        this.size = size;
        this.speed = speed;
        this.amount = amount;
    }

    public abstract void render(Collection<? extends Player> players);


    public ParticleParam getParticleParam() {
        return particleParam;
    }

    public void setParticleParam(ParticleParam particleParam) {
        this.particleParam = particleParam;
    }

    public boolean isFar() {
        return far;
    }

    public void setFar(boolean far) {
        this.far = far;
    }

    public Vec3d getLocation() {
        return location;
    }

    public void setLocation(Vec3d location) {
        this.location = location;
    }

    public Vec3f getSize() {
        return size;
    }

    public void setSize(Vec3f size) {
        this.size = size;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
