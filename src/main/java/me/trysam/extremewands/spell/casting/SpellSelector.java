package me.trysam.extremewands.spell.casting;

import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.wand.WandCategory;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SpellSelector {

    private Player player;
    private ExtremeWandsPlugin plugin;


    public SpellSelector(Player player, ExtremeWandsPlugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public FireModeSelectionInventory getFireModeSelectionInventory(WandCategory wandCategory, UUID wandUUID) {
        return new FireModeSelectionInventory(player, this, "§6Select a firing mode§8:", wandCategory,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJhNmYwZTg0ZGFlZmM4YjIxYWE5OTQxNWIxNmVkNWZkYWE2ZDhkYzBjM2NkNTkxZjQ5Y2E4MzJiNTc1In19fQ==",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTZmYWI5OTFkMDgzOTkzY2I4M2U0YmNmNDRhMGI2Y2VmYWM2NDdkNDE4OWVlOWNiODIzZTljYzE1NzFlMzgifX19", wandUUID);
    }

    public SpellSelectionInventory getSpellSelectionInventory(FiringMode firingMode, WandCategory wandCategory, UUID wandUUID) {
        return new SpellSelectionInventory(player, this, firingMode, "§eSelect a spell§8:", wandCategory, wandUUID);
    }

    public ExtremeWandsPlugin getPlugin() {
        return plugin;
    }
}
