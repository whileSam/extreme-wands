package me.trysam.extremewands.spell;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import me.trysam.extremewands.ExtremeWandsPlugin;
import me.trysam.extremewands.config.SimpleConfig;
import me.trysam.extremewands.spell.data.PersistentSpellData;
import me.trysam.extremewands.spell.leveling.SpellLeveler;
import me.trysam.extremewands.wand.WandCategory;
import net.minecraft.server.v1_15_R1.AdvancementDataWorld;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.PacketPlayOutAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SpellHandler {
    private final float nextLevelUpMultiplier = 1.5f;
    private final int initialNextLevelUp = 25;

    private AtomicReference<Multimap<Player, Spell>> spells = new AtomicReference<>(LinkedListMultimap.create());

    private ExtremeWandsPlugin plugin;

    @Inject
    private SpellHandler(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    public void launchSpell(EnumSpell spell, Player caster, WandCategory wandCategory, float effectMultiplier, float protectionMultiplier) {
        if(spell == EnumSpell.NONE) {
            return;
        }
        int playerMana = plugin.getManaRepository().get(caster.getUniqueId());
        if(playerMana == 0) {
            return;
        }
        int requiredMana = 1;
        Map<String, LevelProperty> properties = new HashMap<>();
        for (SimpleLevelProperty levelProperty : spell.getLevelProperties()) {
            LevelProperty lp = new LevelProperty(plugin, caster, levelProperty);
            if(lp.getId().endsWith("manaCost")) {
                requiredMana = (int)Math.round(Math.ceil(lp.getValue()*effectMultiplier));
            }
            properties.put(levelProperty.id, lp);
        }
        if(requiredMana < 1) {
            requiredMana = 1;
        }
        if(playerMana - requiredMana < 0) {
            return;
        }
        try {
            Spell realSpell = spell.getSpellClass()
                    .getConstructor(ExtremeWandsPlugin.class, SpellHandler.class, Map.class, Player.class, WandCategory.class, SpellCategory.class, float.class, float.class, PersistentSpellData.class)
                    .newInstance(plugin, this, properties, caster, wandCategory, spell.getSpellCategory(), effectMultiplier, protectionMultiplier, new PersistentSpellData.Builder(spell).setConfiguration(plugin.getSpellPersistentDataConfig()).build());
            spells.get().put(caster, realSpell);
            realSpell.enable();
            if(!realSpell.isCancelled()) {
                setUseCount(caster, spell, getUseCount(caster, spell)+1);
                if(getUseCount(caster, spell) >= getNextLevelUp(caster, spell)) {
                    SpellLeveler leveler = new SpellLeveler(caster, plugin);
                    leveler.setSpellLevelPoints(spell, leveler.getSpellLevelPoints(spell)+1);
                    setNextLevelUp(caster, spell, (long) (getNextLevelUp(caster, spell)*nextLevelUpMultiplier));
                }
                plugin.getManaRepository().set(caster.getUniqueId(), playerMana-requiredMana);
            }else {
                caster.playSound(caster.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            caster.sendMessage("§4Unable to cast spell.");
            e.printStackTrace();
        }
    }

    public long getUseCount(Player player, EnumSpell spell) {
        FileConfiguration configuration = plugin.getPluginConfig().getFileConfiguration();
        if(!configuration.contains("playerSettings."+player.getUniqueId()+".useCount."+spell.name())) {
            setUseCount(player, spell, 0);
        }
        return configuration.getLong("playerSettings."+player.getUniqueId()+".useCount."+spell.name());
    }

    public void setUseCount(Player player, EnumSpell spell, long count) {
        SimpleConfig pluginConfig = plugin.getPluginConfig();
        FileConfiguration configuration = pluginConfig.getFileConfiguration();
        configuration.set("playerSettings."+player.getUniqueId()+".useCount."+spell.name(), count);
        pluginConfig.save();
    }

    public long getNextLevelUp(Player player, EnumSpell spell) {
        FileConfiguration configuration = plugin.getPluginConfig().getFileConfiguration();
        if(!configuration.contains("playerSettings."+player.getUniqueId()+".nextLevelUp."+spell.name())) {
            setNextLevelUp(player, spell, initialNextLevelUp);
        }
        return configuration.getLong("playerSettings."+player.getUniqueId()+".nextLevelUp."+spell.name());
    }

    public void setNextLevelUp(Player player, EnumSpell spell, long level) {
        SimpleConfig pluginConfig = plugin.getPluginConfig();
        FileConfiguration configuration = pluginConfig.getFileConfiguration();
        configuration.set("playerSettings."+player.getUniqueId()+".nextLevelUp."+spell.name(), level);
        pluginConfig.save();
    }



    public void startScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            clearSpells();
            spells.get().forEach((player, spell) -> {
                if(spell.isActive()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            spell.run();
                        }
                    }.runTaskAsynchronously(plugin);
                }
            });
        }, 0, 0);
    }

    public List<Spell> getActiveSpells(Player player) {
        return spells.get().entries().stream().filter(playerSpellEntry -> playerSpellEntry.getKey() == player).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<Spell> getSpells(Predicate<Spell> predicate) {
        return spells.get().entries().stream().filter(playerSpellEntry -> predicate.test(playerSpellEntry.getValue())).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<Spell> getSpellsOfClass(Class<? extends Spell> aClass) {
        return spells.get().entries().stream().filter(playerSpellEntry -> playerSpellEntry.getValue().getClass() == aClass).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<Spell> getSpellsOfCategory(SpellCategory spellCategory) {
        return spells.get().entries().stream().filter(playerSpellEntry -> playerSpellEntry.getValue().getSpellCategory() == spellCategory).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<Spell> getSpellsOfType(EnumSpell spell) {
        return spells.get().entries().stream().filter(playerSpellEntry -> playerSpellEntry.getValue().getClass() == spell.getSpellClass()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    private void clearSpells() {
        List<Map.Entry<Player, Spell>> toRemove = spells.get().entries().stream().filter(playerSpellEntry -> !playerSpellEntry.getValue().isActive() || playerSpellEntry.getValue().hasExpired() || playerSpellEntry.getKey() == null).collect(Collectors.toList());
        toRemove.forEach(playerSpellEntry -> {
            if(playerSpellEntry.getValue().isActive()) {
                playerSpellEntry.getValue().disable();
            }
            spells.get().remove(playerSpellEntry.getKey(), playerSpellEntry.getValue());
        });
    }

}
