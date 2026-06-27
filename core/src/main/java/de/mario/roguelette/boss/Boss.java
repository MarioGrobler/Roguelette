package de.mario.roguelette.boss;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.events.GameEventListener;
import de.mario.roguelette.wheel.Wheel;

import java.util.ArrayList;
import java.util.List;

/**
 * A boss: a temporary antagonist that gates progression at the end of certain stages (2, 4, 6, 8).
 *
 * <p>Structurally a boss is the dark mirror of a {@link de.mario.roguelette.characters.Character}:
 * it carries a set of run-bending {@link GameEventListener}s (its <em>debuff</em>) plus an optional
 * wheel mutation, both active only for the duration of the boss fight. The listeners are registered
 * the same way owned fortunes are (see {@code GameState.collectListeners}), so a boss reuses the
 * whole event layer without any special casing.
 *
 * <p><b>Why a boss can't be skipped.</b> The win condition is a <em>gain</em>: the player must grow
 * their balance by {@code balanceAtBossStart * getGoalFraction()} within {@link #getSpinCount()}
 * spins. Doing nothing yields a gain of zero and therefore fails, and betting tiny can't reach the
 * goal either — so the goal itself forces meaningful bets, which exposes the player to the debuff.
 * The debuff is the challenge; the goal is what makes engaging with it mandatory.
 */
public abstract class Boss {

    /** Number of spins the player gets to beat a boss. */
    public static final int SPINS_PER_BOSS = 3;

    /** Display name, e.g. "The House". */
    public abstract String getName();

    /** Short epithet shown under the name, e.g. "Always Wins". */
    public abstract String getTitle();

    /** Player-facing explanation of the debuff this boss imposes. */
    public abstract String getDescription();

    /** Theme colour used for the boss's intro card / HUD accents. */
    public abstract Color getAccentColor();

    /**
     * The fraction of the player's balance (measured at the start of the fight) that must be gained
     * to defeat this boss. Kept low because it is pure profit demanded in a few spins under a debuff.
     */
    public abstract float getGoalFraction();

    /** How many spins this boss lasts. Defaults to {@link #SPINS_PER_BOSS}. */
    public int getSpinCount() {
        return SPINS_PER_BOSS;
    }

    /**
     * Builds this boss's debuff listeners. Called once when the fight begins, so stateful effects get
     * fresh state per fight. Defaults to none.
     */
    public List<GameEventListener> createListeners() {
        return new ArrayList<>();
    }

    /**
     * Mutates the wheel when the fight begins (e.g. enlarge the zero zone). Defaults to no-op. A boss
     * that overrides this should make the change reversible via {@link #revertWheelMutation(Wheel)}.
     */
    public void applyWheelMutation(final Wheel wheel) {}

    /** Reverts any wheel mutation when the fight ends. Defaults to no-op. */
    public void revertWheelMutation(final Wheel wheel) {}
}
