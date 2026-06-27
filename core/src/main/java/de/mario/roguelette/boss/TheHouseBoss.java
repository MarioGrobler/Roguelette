package de.mario.roguelette.boss;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * Stage-2 boss. The simplest debuff to read as a first boss: a flat house edge skims a quarter off
 * every win, so the player just has to out-bet the tax.
 */
public class TheHouseBoss extends Boss {

    private static final float KEEP_FACTOR = 0.75f; // player keeps 75% of each win
    private static final float GOAL_FRACTION = 0.15f;

    @Override
    public String getName() {
        return "The House";
    }

    @Override
    public String getTitle() {
        return "Always Wins";
    }

    @Override
    public String getDescription() {
        return "House Edge: the house skims " + Math.round((1 - KEEP_FACTOR) * 100)
            + "% of every win's profit.\n\nOut-bet the tax: grow your fortune by "
            + Math.round(GOAL_FRACTION * 100) + "% within " + getSpinCount() + " spins.";
    }

    @Override
    public Color getAccentColor() {
        return new Color(0.18f, 0.45f, 0.30f, 1f); // felt green
    }

    @Override
    public float getGoalFraction() {
        return GOAL_FRACTION;
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new HouseEdgeEffect(KEEP_FACTOR));
    }
}
