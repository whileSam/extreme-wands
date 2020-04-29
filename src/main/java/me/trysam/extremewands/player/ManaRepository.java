package me.trysam.extremewands.player;

import me.trysam.extremewands.config.SimpleConfig;
import me.trysam.extremewands.util.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ManaRepository implements Repository<UUID, Integer> {

    private SimpleConfig config;
    private AtomicReference<Map<UUID, Integer>> cache = new AtomicReference<>(new HashMap<>());
    private int cacheAmount;
    private int maxMana;
    private int cachedEntries = 0;

    public ManaRepository(SimpleConfig config, int cacheAmount, int maxMana) {
        this.config = config;
        this.cacheAmount = cacheAmount;
        this.maxMana = maxMana;
    }

    @Override
    public void set(UUID key, Integer value) {
        if(value > maxMana) {
            value = maxMana;
        }
        if(cache.get().containsKey(key)) {
            cache.get().replace(key, value);
        }else {
            cache.get().put(key, value);
        }
        cachedEntries++;
        if(cachedEntries > cacheAmount) {
            flush();
        }
    }

    @Override
    public Integer get(UUID key) {
        if(cache.get().containsKey(key)) {
            return cache.get().get(key);
        }else {
            if(!config.getFileConfiguration().contains("playerSettings."+key.toString()+".mana")) {
                set(key, 0);
                return 0;
            }
            return config.getFileConfiguration().getInt("playerSettings."+key.toString()+".mana");
        }
    }

    public void flush() {
        cache.get().forEach((uuid, integer) -> {
            config.getFileConfiguration().set("playerSettings."+uuid.toString()+".mana", integer);
        });
        config.save();
        cache.get().clear();
        cachedEntries = 0;
    }

    public int getMaxMana() {
        return maxMana;
    }
}
