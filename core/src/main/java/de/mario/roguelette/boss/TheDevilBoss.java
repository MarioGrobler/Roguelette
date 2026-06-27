package de.mario.roguelette.boss;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.events.GameEventListener;

import java.util.Arrays;
import java.util.List;

/**
 * Stage-8 final boss, the last guardian of the $1,000,000. Combines two debuffs — a steep house edge
 * <em>and</em> a red ban — so the player must both bet around red and out-multiply a halving tax with
 * the engine they have spent the whole run building.
 */
public class TheDevilBoss extends Boss {

    private static final float KEEP_FACTOR = 0.5f;  // all wins are halved
    private static final float RED_KEEP = 0.2f;     // red wins additionally cut to 20%
    private static final float GOAL_FRACTION = 0.25f;

    @Override
    public String getName() {
        return "The Devil";
    }

    @Override
    public String getTitle() {
        return "Takes His Due";
    }

    @Override
    public String getDescription() {
        return "Hellfire: every win keeps only half its profit, and red wins only " + Math.round(RED_KEEP * 100)
            + "% on top.\n\nThe last gate before the million: grow your fortune by "
            + Math.round(GOAL_FRACTION * 100) + "% within " + getSpinCount() + " spins.";
    }

    @Override
    public Color getAccentColor() {
        return new Color(0.55f, 0.06f, 0.06f, 1f); // hellfire red
    }

    @Override
    public float getGoalFraction() {
        return GOAL_FRACTION;
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Arrays.asList(new HouseEdgeEffect(KEEP_FACTOR), new RedPenaltyEffect(RED_KEEP));
    }
}
