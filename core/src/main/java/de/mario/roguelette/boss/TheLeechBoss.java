package de.mario.roguelette.boss;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * Stage-6 boss. A bleed: every boss spin skims a chunk of the player's balance, so standing still
 * loses ground. The gain goal has to outrun the tithe — passive play guarantees defeat.
 */
public class TheLeechBoss extends Boss {

    private static final float TITHE = 0.10f; // 10% of balance skimmed each spin
    private static final float GOAL_FRACTION = 0.20f;

    @Override
    public String getName() {
        return "The Leech";
    }

    @Override
    public String getTitle() {
        return "Ever Hungry";
    }

    @Override
    public String getDescription() {
        return "Tithe: the Leech drains " + Math.round(TITHE * 100)
            + "% of your balance after every spin.\n\nOutrun the bleed: grow your fortune by "
            + Math.round(GOAL_FRACTION * 100) + "% within " + getSpinCount() + " spins.";
    }

    @Override
    public Color getAccentColor() {
        return new Color(0.40f, 0.32f, 0.10f, 1f); // sickly bile
    }

    @Override
    public float getGoalFraction() {
        return GOAL_FRACTION;
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new TitheEffect(TITHE));
    }
}
