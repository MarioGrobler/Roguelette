package de.mario.roguelette.events;

import de.mario.roguelette.GameState;

/**
 * Central hook interface for the game's event layer. Persistent game objects (owned fortunes,
 * active pending chances, ...) implement this and are dispatched to at well-defined points in
 * a round, instead of each one being hard-wired into the payout / spin / turn-change code.
 *
 * <p>All methods are default no-ops so implementations only override the hooks they care about.
 */
public interface GameEventListener {

    /**
     * Fired right before the wheel and ball start spinning, after the bets are locked in.
     */
    default void onSpinStart(final GameState gameState) {}

    /**
     * Fired before the landings are determined, while the set of balls for the spin is still being
     * assembled. The spin is seeded with the player's default ball(s); listeners may add more
     * through the mutable {@link SpinContext} (e.g. Double Ball). Each ball lands and pays out
     * independently.
     */
    default void onPrepareSpin(final GameState gameState, final SpinContext spin) {}

    /**
     * Fired once the landing segment has been determined (but before the spin animates to it).
     * Listeners may change the landing via the mutable {@link LandingContext} (e.g. Ricochet,
     * Freeze, or future ball modifiers).
     */
    default void onBallLanded(final GameState gameState, final LandingContext landing) {}

    /**
     * Fired for every bet when the round is resolved. Listeners contribute payout modifiers
     * (on a win) or a stake refund (on a loss) through the mutable {@link BetResolution}.
     */
    default void onResolveBet(final GameState gameState, final BetResolution resolution) {}

    /**
     * Fired when the turn changes (after the round is resolved). Used for per-turn upkeep such
     * as ticking down durations, applying interest, or mutating the wheel.
     */
    default void onTurnChange(final GameState gameState) {}
}
