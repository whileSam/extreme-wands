package me.trysam.extremewands.config;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LevelPropertyInfoConfig extends SimpleConfig {
    public LevelPropertyInfoConfig(File parent, String filename) {
        super(parent, filename);
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("manaCost.name", "Mana Cost");
        defaults.put("manaCost.description", Arrays.asList("§7Mana required for one spell use"));

        defaults.put("fire.duration.name", "Fire Duration");
        defaults.put("fire.duration.description", Arrays.asList("§7How long affected targets will burn.", "§8(§e!§8) §7Upgrade doesn't affect blocks"));

        defaults.put("shield.duration.name", "Shield Duration");
        defaults.put("shield.duration.description", Arrays.asList("§7How long your shield will be active."));

        defaults.put("repair.damage.name", "Repair Damage");
        defaults.put("repair.damage.description", Arrays.asList("§7Creates a beam, that repairs items."));

        defaults.put("heal.amount.name", "Heal Amount");
        defaults.put("heal.amount.description", Arrays.asList("§7Amount of health gained per use."));

        defaults.put("empower.duration.name", "Duration");
        defaults.put("empower.duration.description", Arrays.asList("§7Time in seconds for which the spell will be active."));

        defaults.put("empower.strength.name", "Strength");
        defaults.put("empower.strength.description", Arrays.asList("§7Strength of the status effects applied."));

        defaults.put("reveal.duration.name", "Duration");
        defaults.put("reveal.duration.description", Arrays.asList("§7Time in seconds for entities stay revealed."));

        defaults.put("reveal.distance.name", "Distance");
        defaults.put("reveal.distance.description", Arrays.asList("§7The radius within entities will be revealed."));

        defaults.put("magicarrow.maxJumps.name", "Maximum Jumps");
        defaults.put("magicarrow.maxJumps.description", Arrays.asList("§7Maximum jumps before the spell comes to a standstill"));

        defaults.put("magicarrow.damage.name", "Damage");
        defaults.put("magicarrow.damage.description", Arrays.asList("§7The damage of the arrow."));
        addDefaults(defaults);
    }
}
