package me.trysam.extremewands.wand;

public enum WandCategory {

    WOOD(1.0f, 0.25f, 1.0f, 0.25f, 0),
    BONE(1.3f, 0.33f, 1.33f, 0.33f, 1),
    BLAZE(1.8f, 0.5f, 1.8f, 0.5f, 2),
    ENDER(3.0f, 1.0f, 25f, 5.0f, 3);

    private float base_effect_multiplier;
    private float effect_multiplier_variation;
    private float base_protection_multiplier;
    private float protection_multiplier_variation;
    private int canHandleSpellLevel;

    WandCategory(float base_effect_multiplier, float effect_multiplier_variation, float base_protection_multiplier, float protection_multiplier_variation, int canHandleSpellLevel) {
        this.base_effect_multiplier = base_effect_multiplier;
        this.effect_multiplier_variation = effect_multiplier_variation;
        this.base_protection_multiplier = base_protection_multiplier;
        this.protection_multiplier_variation = protection_multiplier_variation;
        this.canHandleSpellLevel = canHandleSpellLevel;
    }

    public float getBase_effect_multiplier() {
        return base_effect_multiplier;
    }

    public float getEffect_multiplier_variation() {
        return effect_multiplier_variation;
    }

    public float getBase_protection_multiplier() {
        return base_protection_multiplier;
    }

    public float getProtection_multiplier_variation() {
        return protection_multiplier_variation;
    }

    public int getCanHandleSpellLevel() {
        return canHandleSpellLevel;
    }
}
