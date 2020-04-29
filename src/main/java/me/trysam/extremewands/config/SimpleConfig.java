package me.trysam.extremewands.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleConfig {

    private File file;
    private FileConfiguration fileConfiguration;

    public SimpleConfig(File parent, String filename) {
        if(!parent.exists()) {
            parent.mkdirs();
        }
        file = new File(parent, filename+".yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public void addDefaults(Map<String, Object> defaults) {
        getFileConfiguration().options().copyDefaults(true);
        getFileConfiguration().addDefaults(defaults);
        save();
    }

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
