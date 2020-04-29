package me.trysam.extremewands.particle;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class Particle {

    private PacketPlayOutWorldParticles packet;

    public Particle(ParticleParam particle, boolean far, double x, double y, double z, float rx, float ry, float rz, float speed, int amount) {
        packet = new PacketPlayOutWorldParticles(particle, far, x, y, z, rx, ry, rz, speed, amount);
    }

    private void sendParticle(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }


    public void sendParticle(Collection<? extends Player> players) {
        players.forEach(this::sendParticle);
    }

}
