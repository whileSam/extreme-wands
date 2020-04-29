package me.trysam.extremewands.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import me.trysam.extremewands.ExtremeWandsPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WandsModule extends AbstractModule {

    private ExtremeWandsPlugin plugin;

    public WandsModule(ExtremeWandsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).to(ExtremeWandsPlugin.class);
        bind(ExtremeWandsPlugin.class).toInstance(plugin);
    }

    public Injector createInjector() {
        return Guice.createInjector(this);
    }
}
