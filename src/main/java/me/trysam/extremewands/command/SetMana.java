package me.trysam.extremewands.command;

import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class SetMana implements CommandExecutor {


    private ExtremeWandsPlugin plugin;

    @Inject
    private SetMana(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("§cMust be player!");
                return false;
            }
            Player player = (Player) commandSender;
            int toSet;
            try {
                toSet = Integer.parseInt(args[0]);
            } catch (NumberFormatException ex) {
                commandSender.sendMessage("§cInvalid usage!");
                return false;
            }
            plugin.getManaRepository().set(player.getUniqueId(), toSet);
            commandSender.sendMessage("§aSuccess!");

            if(player.getInventory().getItemInMainHand().getItemMeta() instanceof Damageable) {
                ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();
                System.out.println(((Damageable)meta).getDamage());
                ((Damageable) meta).setDamage(toSet);
                player.getInventory().getItemInMainHand().setItemMeta(meta);
            }
            return true;
        } else if (args.length == 2) {
            Player target = Bukkit.getPlayerExact(args[0]);
            if(target == null) {
                commandSender.sendMessage("§cTarget player is not online!");
                return false;
            }
            int toSet;
            try {
                toSet = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                commandSender.sendMessage("§cInvalid usage!");
                return false;
            }
            plugin.getManaRepository().set(target.getUniqueId(), toSet);
            commandSender.sendMessage("§aSuccess!");
            return true;
        }else {
            commandSender.sendMessage("§cInvalid usage!");
        }


        return false;
    }

//    private void circle(Player player, Axis axis, float angle) {
//        Vec3f rotation_axis = new Vec3f(1, 0, 0);
//        Vec3f rotation_axis2 = new Vec3f(0, 0, 1);
//        Quaternion rotationQuaternion = new Quaternion(rotation_axis, 90)
//                .multiplied(new Quaternion(rotation_axis2, player.getLocation().getYaw()))
//                .multiplied(new Quaternion(rotation_axis, player.getLocation().getPitch()));
//        Quaternion point = new Quaternion(0, 0, 5, 0);
//        Quaternion point_rotated = rotationQuaternion.multiplied(point).multiplied(rotationQuaternion.getInverse());
//        Vec3d pos = new Vec3d(player.getEyeLocation().getX()+point_rotated.getX(), player.getEyeLocation().getY()+point_rotated.getY(), player.getEyeLocation().getZ()+point_rotated.getZ());
//        Vec3f size = new Vec3f(0, 0,0);
//        CircleParticleRenderer particleRenderer = new CircleParticleRenderer(Particles.FLAME, true, pos, size, 0.0f, 5, 16, 0.25, rotationQuaternion);
//        particleRenderer.render(Bukkit.getOnlinePlayers());
//    }

}
