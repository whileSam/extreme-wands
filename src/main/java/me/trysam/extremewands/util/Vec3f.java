package me.trysam.extremewands.util;

public class Vec3f {

    private float x;
    private float y;
    private float z;

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3f multiplied(float number) {
        return new Vec3f(x * number, y * number, z * number);
    }

    public Vec3f normalized() {
        float length = getLength();
        return new Vec3f(x / length, y / length, z / length);
    }

    public float dot(Vec3f vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }

    public Vec3f cross(Vec3f vec) {
        float newX = y*vec.z - z*vec.y;
        float newY = z*vec.x - x*vec.z;
        float newZ = x*vec.y - y*vec.x;
        return new Vec3f(newX, newY, newZ);
    }

    public Vec3f added(Vec3f vec) {
        return  new Vec3f(x+vec.x, y+vec.y, z+vec.z);
    }

    public float getLength() {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
