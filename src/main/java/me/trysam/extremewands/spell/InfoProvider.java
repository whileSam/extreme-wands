package me.trysam.extremewands.spell;

import me.trysam.extremewands.config.SimpleConfig;
import me.trysam.extremewands.util.Provider;

import java.util.Collections;
import java.util.List;

public class InfoProvider implements Provider<InfoEntry, String> {

    private SimpleConfig dataConfig;

    public InfoProvider(SimpleConfig dataConfig) {
        this.dataConfig = dataConfig;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public InfoEntry provide(String input) {
        return new InfoEntry(dataConfig.getFileConfiguration().getString(input+".name", input), (List<String>) dataConfig.getFileConfiguration().getList(input+".description", Collections.singletonList(input)));
    }
}
