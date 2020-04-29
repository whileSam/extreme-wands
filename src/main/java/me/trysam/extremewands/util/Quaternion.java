package me.trysam.extremewands.util;

import static java.lang.Math.*;

public class Quaternion {

    private final Vec3f n;
    private float w;
    private float x;
    private float y;
    private float z;

    public Quaternion(Vec3f n, double theta_degrees) {
        Vec3f n2 = n.normalized();
        float theta = (float) toRadians(theta_degrees);
        Vec3f n_multiplied = n2.multiplied((float) sin(theta / 2));
        w = (float) cos(theta / 2);
        x = n_multiplied.getX();
        y = n_multiplied.getY();
        z = n_multiplied.getZ();
        this.n = new Vec3f(x, y, z);
    }

    public Quaternion(float w, float x, float y, float z) {
        n = new Vec3f(x,y,z);
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion getInverse() {
        Vec3f inverse = n.multiplied(-1);
        return new Quaternion(w, inverse.getX(), inverse.getY(), inverse.getZ());
    }

    public Quaternion multiplied(Quaternion quat) {
        float newW = w * quat.w - n.dot(quat.n);
        Vec3f newN = n.multiplied(quat.w).added(quat.n.multiplied(w)).added(n.cross(quat.n));
        return new Quaternion(newW, newN.getX(), newN.getY(), newN.getZ());
    }


    public Vec3f getN() {
        return n;
    }

    public float getW() {
        return w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
