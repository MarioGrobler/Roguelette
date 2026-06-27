package de.mario.roguelette.boss;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * Stage-4 boss. A colour tax: wins landing on red pay only a fraction of normal, so the player must
 * steer their bets (and ideally their wheel build) toward black for the fight — but a red-heavy board
 * is punished, not auto-killed.
 */
public class TheCrowBoss extends Boss {

    private static final float RED_KEEP = 0.25f; // red wins pay 25% of normal
    private static final float GOAL_FRACTION = 0.15f;

    @Override
    public String getName() {
        return "The Crow";
    }

    @Override
    public String getTitle() {
        return "Seeing Red";
    }

    @Override
    public String getDescription() {
        return "Seeing Red: a win landing on a red segment keeps only " + Math.round(RED_KEEP * 100)
            + "% of its profit.\n\nBet around the red: grow your fortune by " + Math.round(GOAL_FRACTION * 100)
            + "% within " + getSpinCount() + " spins.";
    }

    @Override
    public Color getAccentColor() {
        return new Color(0.20f, 0.20f, 0.24f, 1f); // crow black
    }

    @Override
    public float getGoalFraction() {
        return GOAL_FRACTION;
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new RedPenaltyEffect(RED_KEEP));
    }
}
