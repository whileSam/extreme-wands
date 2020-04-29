package me.trysam.extremewands.util;

import org.bukkit.Location;

public class Interpolation {
    public static Location lerpLocation(Location start, Location end, float f) {
        return start.add(end.toVector().subtract(start.toVector()).multiply(f));
    }
}
