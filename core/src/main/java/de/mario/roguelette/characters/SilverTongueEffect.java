package de.mario.roguelette.characters;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;

/**
 * The Gambler's signature passive: a smooth talker who always squeezes a little extra out of a win.
 * Every winning bet pays {@value #BONUS} more. Pure, gentle upside — the approachable starter
 * character, no downside to learn around.
 */
public class SilverTongueEffect implements GameEventListener {

    private static final float BONUS = 0.15f; // +15% on every win

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin()) {
            resolution.multiplyTotal(1f + BONUS);
        }
    }

    public static int bonusPercent() {
        return Math.round(BONUS * 100);
    }
}
