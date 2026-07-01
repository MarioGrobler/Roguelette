package de.mario.roguelette.curses;

import de.mario.roguelette.config.RunConfig;

import java.util.function.Consumer;

/**
 * A curse that is nothing but a {@link RunConfig} tweak — most of the sub-curse pool. Declared
 * inline in {@link CurseLevels} (name + description + tier + config lambda) instead of one
 * near-empty class per curse.
 */
public class ConfigCurse extends Curse {

    private final String name;
    private final String description;
    private final int tier;
    private final String category;
    private final Consumer<RunConfig> modifier;

    public ConfigCurse(final String name, final String description, final int tier,
                       final String category, final Consumer<RunConfig> modifier) {
        this.name = name;
        this.description = description;
        this.tier = tier;
        this.category = category;
        this.modifier = modifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void applyToConfig(final RunConfig config) {
        modifier.accept(config);
    }
}
