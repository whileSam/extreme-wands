package me.trysam.extremewands.spell;

public class SimpleLevelProperty {


    protected String id;
    protected String infoID;
    protected double defaultValue;
    protected double step;
    protected double min;
    protected double max;

    public SimpleLevelProperty(String id, String infoID, double defaultValue, double step, double min, double max) {
        this.id = id;
        this.infoID = infoID;
        this.defaultValue = defaultValue;
        this.step = step;
        this.min = min;
        this.max = max;
    }

    public String getId() {
        return id;
    }

    public String getInfoID() {
        return infoID;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double getStep() {
        return step;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
