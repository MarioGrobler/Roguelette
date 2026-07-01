package de.mario.roguelette.curses;

import de.mario.roguelette.GameState;
import de.mario.roguelette.config.RunConfig;
import de.mario.roguelette.events.GameEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A hex the house lays on the table — one modifier of a Casino-Curses run. Mirrors the
 * {@code Boss}/{@code Character} pattern: a curse is pure data + hooks, wired into the run with no
 * special-casing. It may act through any (or several) of three channels:
 * <ul>
 *   <li>{@link #applyToConfig(RunConfig)} — bend the run's rule set (prices, goals, rounds, ...),
 *       applied between {@code RunConfig.baseline()} and run construction;</li>
 *   <li>{@link #createListeners()} — run-wide passive {@link GameEventListener}s, collected by
 *       {@code GameState.collectListeners()} like fortunes;</li>
 *   <li>{@link #applyRunSetup(GameState)} — one-time setup once the run exists (wheel mutation
 *       such as adding the Devil's Segment).</li>
 * </ul>
 * See {@link CurseLevels} for how curses assemble into the 8-level ladder.
 */
public abstract class Curse {

    /** Display name, e.g. "The Devil's Mark". */
    public abstract String getName();

    /** Player-facing description of what the curse does. */
    public abstract String getDescription();

    /**
     * Nastiness tier of a sub-curse (1–3); higher tiers only enter the draw pool at higher curse
     * levels. Main curses ignore this (they are placed by level, not drawn).
     */
    public int getTier() {
        return 1;
    }

    /**
     * Draw-exclusion category: at most one active curse per non-null category (e.g. the two
     * goal-scaling curses must not stack). {@code null} = no restriction.
     */
    public String getCategory() {
        return null;
    }

    /** Bends the run's rule set. Called before the run is constructed. */
    public void applyToConfig(final RunConfig config) {
    }

    /** Run-wide passive listeners, built fresh per run (stateful curses get clean state). */
    public List<GameEventListener> createListeners() {
        return new ArrayList<>();
    }

    /** One-time setup against the live run (wheel mutation etc.). Called once after construction. */
    public void applyRunSetup(final GameState gameState) {
    }
}
