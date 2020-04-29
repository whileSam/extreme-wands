package me.trysam.extremewands.config;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SpellInfoConfig extends SimpleConfig {
    public SpellInfoConfig(File parent, String filename) {
        super(parent, filename);
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("NONE.name", "None");
        defaults.put("NONE.description", Arrays.asList("§7No spell"));

        defaults.put("FIRE.name", "Fire");
        defaults.put("FIRE.description", Arrays.asList("§7Unleash a controllable fire beam."));

        defaults.put("SHIELD.name", "Shield");
        defaults.put("SHIELD.description", Arrays.asList("§7Protect you from incoming spells."));

        defaults.put("REPAIR.name", "Repair");
        defaults.put("REPAIR.description", Arrays.asList("§7Create a beam, that repairs items."));

        defaults.put("DISARM.name", "Disarm");
        defaults.put("DISARM.description", Arrays.asList("§7Relieve your enemies of their items."));

        defaults.put("REVEAL.name", "Reveal");
        defaults.put("REVEAL.description", Arrays.asList("§7Reveal all entities in a certain radius."));

        defaults.put("MAGIC_ARROW.name", "Magic Arrow");
        defaults.put("MAGIC_ARROW.description", Arrays.asList("§7Launches a deadly magic arrow that automatically finds new targets."));

        defaults.put("HEAL.name", "Heal");
        defaults.put("HEAL.description", Arrays.asList("§7Regain your health."));

        defaults.put("EMPOWER.name", "Empower");
        defaults.put("EMPOWER.description", Arrays.asList("§7Increase your performance.", "§7Damage taken will be reduced and damage dealt increased", "§7Maximum health will be heightened"));

        addDefaults(defaults);
    }
}
