package me.trysam.extremewands.spell;

import java.util.List;

public class InfoEntry {

    private String name;
    private List<String> description;

    public InfoEntry(String name, List<String> description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }
}
