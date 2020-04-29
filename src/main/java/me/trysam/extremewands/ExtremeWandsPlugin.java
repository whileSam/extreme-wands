package me.trysam.extremewands;

import com.google.inject.Injector;
import me.trysam.extremewands.command.SetMana;
import me.trysam.extremewands.config.LevelPropertyInfoConfig;
import me.trysam.extremewands.config.SimpleConfig;
import me.trysam.extremewands.config.SpellInfoConfig;
import me.trysam.extremewands.listener.*;
import me.trysam.extremewands.modules.WandsModule;
import me.trysam.extremewands.player.ManaBar;
import me.trysam.extremewands.player.ManaController;
import me.trysam.extremewands.player.ManaRepository;
import me.trysam.extremewands.spell.InfoProvider;
import me.trysam.extremewands.spell.SpellHandler;
import me.trysam.extremewands.spell.casting.SpellSelectionRepository;
import me.trysam.extremewands.util.BookWriter;
import me.trysam.extremewands.util.Namespaces;
import me.trysam.extremewands.util.RecipeLoader;
import me.trysam.extremewands.wand.WandActivator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

/*
TODO: remove infinite items witch & wandering trader bug @me.trysam.extremewands.spell.impl.DisarmSpell
 */
public class ExtremeWandsPlugin extends JavaPlugin {

    private Injector injector;

    private SimpleConfig pluginConfig;
    private SpellHandler spellHandler;
    private WandActivator wandActivator;
    private Namespaces namespaces;
    private BookWriter ancientKnowledgeBookWriter;
    private InfoProvider spellInfoProvider;
    private InfoProvider levelPropertyInfoProvider;
    private ManaRepository manaRepository;
    private ManaBar manaBar;
    private ManaController manaController;
    private SpellSelectionRepository spellSelectionRepository;
    private SimpleConfig spellPersistentDataConfig;

    @Override
    public void onLoad() {
        injector = new WandsModule(this).createInjector();
    }

    @Override
    public void onEnable() {
        namespaces = new Namespaces(new NamespacedKey(this, "wand-type"),
                new NamespacedKey(this, "is-activated"),
                new NamespacedKey(this, "wand-owner"),
                new NamespacedKey(this, "effect-multiplier"),
                new NamespacedKey(this, "protection-multiplier"),
                new NamespacedKey(this, "wand-uuid"),
                new NamespacedKey(this, "is-ancient-knowledge-book"),
                new NamespacedKey(this, "selected-spell"),
                new NamespacedKey(this, "selected-upgrade"),
                new NamespacedKey(this, "can-select-upgrade"),
                new NamespacedKey(this, "page-selector"),
                new NamespacedKey(this, "firing-mode-selector"),
                new NamespacedKey(this, "wooden_wand"),
                new NamespacedKey(this, "bone_wand"),
                new NamespacedKey(this, "blaze_wand"),
                new NamespacedKey(this, "ender_wand"));
        RecipeLoader loader = injector.getInstance(RecipeLoader.class);
        pluginConfig = new SimpleConfig(getDataFolder(), "config");

        SimpleConfig spellInfoConfig = new SpellInfoConfig(getDataFolder(), "spell_info");
        SimpleConfig levelPropertyInfoConfig = new LevelPropertyInfoConfig(getDataFolder(), "level_property_info");

        spellPersistentDataConfig = new SimpleConfig(getDataFolder(), "spell_persistent_data");

        spellInfoProvider = new InfoProvider(spellInfoConfig);
        levelPropertyInfoProvider = new InfoProvider(levelPropertyInfoConfig);


        spellHandler = injector.getInstance(SpellHandler.class);
        wandActivator = injector.getInstance(WandActivator.class);

        manaRepository = new ManaRepository(pluginConfig, 30, 10_000);
        manaBar = new ManaBar(this, "▌", "▍", "▎", "▏");
        manaController = injector.getInstance(ManaController.class);
        manaController.startManaProvider(1, 20*10);

        spellSelectionRepository = new SpellSelectionRepository(pluginConfig);


        File ancientKnowledgeFile = new File(getDataFolder(), "ancient_knowledge");
        if(!ancientKnowledgeFile.exists()) {
            try {
                ancientKnowledgeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ancientKnowledgeBookWriter = new BookWriter(ancientKnowledgeFile, "§6Ancient Knowledge", "§5§kYurdog Blazingfall", this);

        //Load wand recipes
        loader.loadWoodenWandRecipe();
        loader.loadBoneWandRecipe();
        loader.loadBlazeWandRecipe();
        loader.loadEnderWandRecipe();

        spellHandler.startScheduler();

        //Register Events
        Bukkit.getPluginManager().registerEvents(injector.getInstance(CraftItem.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(PlayerInteract.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(InventoryClick.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(VillagerAcquireTrade.class), this);
        Bukkit.getPluginManager().registerEvents(injector.getInstance(PlayerDropItem.class), this);
        getCommand("setmana").setExecutor(injector.getInstance(SetMana.class));

        startManaScheduler(20);
        System.out.println("[EW] Plugin enabled!");
    }

    @Override
    public void onDisable() {
        manaRepository.flush();
        System.out.println("[EW] Plugin disabled!");
    }

    private void startManaScheduler(int period) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(o -> {
                    manaBar.drawManaBar(o, period);
                });
            }
        }.runTaskTimerAsynchronously(this, 0, period);
    }

    public SimpleConfig getPluginConfig() {
        return pluginConfig;
    }

    public SpellHandler getSpellHandler() {
        return spellHandler;
    }

    public WandActivator getWandActivator() {
        return wandActivator;
    }

    public Namespaces getNamespaces() {
        return namespaces;
    }

    public BookWriter getAncientKnowledgeBookWriter() {
        return ancientKnowledgeBookWriter;
    }

    public InfoProvider getSpellInfoProvider() {
        return spellInfoProvider;
    }

    public InfoProvider getLevelPropertyInfoProvider() {
        return levelPropertyInfoProvider;
    }

    public ManaRepository getManaRepository() {
        return manaRepository;
    }

    public ManaBar getManaBar() {
        return manaBar;
    }

    public ManaController getManaController() {
        return manaController;
    }

    public SpellSelectionRepository getSpellSelectionRepository() {
        return spellSelectionRepository;
    }

    public SimpleConfig getSpellPersistentDataConfig() {
        return spellPersistentDataConfig;
    }
}
