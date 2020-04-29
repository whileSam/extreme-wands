package me.trysam.extremewands.player;

import me.trysam.extremewands.ExtremeWandsPlugin;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ManaBar {

    private ExtremeWandsPlugin plugin;
    private String full;
    private String three_quarters;
    private String half;
    private String one_quarter;

    public ManaBar(ExtremeWandsPlugin plugin, String full, String three_quarters, String half, String one_quarter) {
        this.plugin = plugin;
        this.full = full;
        this.three_quarters = three_quarters;
        this.half = half;
        this.one_quarter = one_quarter;
    }


    public void drawManaBar(Player player, int duration) {
        ManaRepository manaRepository = plugin.getManaRepository();
        int percentage = Math.round(manaRepository.get(player.getUniqueId()) / (float)manaRepository.getMaxMana() * 100f);
        StringBuilder string = new StringBuilder();
        float p = (percentage-1)/4f;
        for (int i = 0; i < 25; i++) {
            if(i <= p) {
                if(i < (int) p) {
                    string.append(full);
                }else {
                    switch (percentage%4) {
                        case 0:
                            string.append(full);
                            break;
                        case 1:
                            string.append(one_quarter);
                            break;
                        case 2:
                            string.append(half);
                            break;
                        case 3:
                            string.append(three_quarters);
                            break;
                    }
                }
            }
        }
        String normal = string.toString();
        String reverse = string.reverse().toString();
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, new ChatComponentText("§a"+reverse+" §8[§6Mana§7: §e"+manaRepository.get(player.getUniqueId())+"§8] §a"+normal), 0, duration, 0);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
    }




}
