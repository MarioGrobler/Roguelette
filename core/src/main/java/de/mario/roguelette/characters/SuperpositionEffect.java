package de.mario.roguelette.characters;

import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;

/**
 * The Professor's signature passive: every spin's winnings exist in superposition until payout. A
 * hidden coin is flipped at spin start; when it is observed (the bets resolve) the spin's winnings
 * either collapse to <em>nothing</em> or are amplified {@value #AMPLIFY}x. High variance with a
 * positive expected multiplier ({@code 0.5*0 + 0.5*3 = 1.5}), but any single spin can pay nothing
 * even on a win.
 *
 * <p>The coin is rolled once per spin in {@link #onSpinStart} (so every bet of the spin — and every
 * ball — shares the same outcome) and applied in {@link #onResolveBet}.
 */
public class SuperpositionEffect implements GameEventListener {

    private static final float AMPLIFY = 3f;

    private boolean amplified = false;

    @Override
    public void onSpinStart(final GameState gameState) {
        amplified = MathUtils.randomBoolean();
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin()) {
            resolution.multiplyTotal(amplified ? AMPLIFY : 0f);
        }
    }

    public static int amplifyFactor() {
        return Math.round(AMPLIFY);
    }
}
