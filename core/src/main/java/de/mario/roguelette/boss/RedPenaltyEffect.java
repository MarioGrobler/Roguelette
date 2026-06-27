package de.mario.roguelette.boss;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;
import de.mario.roguelette.wheel.Segment;

/**
 * Boss debuff: a win whose ball landed on a <em>pure red</em> segment keeps only a fraction of its
 * <em>profit</em>. Heavily discourages red bets (and red-leaning wheel builds) without removing them
 * entirely — a red win is still a (small) win, never a net loss (which scaling the whole gross payout
 * would cause for even-money colour bets).
 *
 * <p>Only pure-red landings are taxed; a {@code BOTH} (purple) segment is not red and pays in full.
 */
public class RedPenaltyEffect implements GameEventListener {

    private final float keepFactor; // e.g. 0.25 -> a red win keeps 25% of its profit

    public RedPenaltyEffect(final float keepFactor) {
        this.keepFactor = keepFactor;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin() && resolution.getLanded().getCurrentColor() == Segment.SegmentColor.RED) {
            // base payout multiplier is gross (stake-inclusive), so profit = base - 1
            float profit = resolution.getBet().getBetType().getPayoutMultiplier() - 1f;
            if (profit > 0f) {
                resolution.addBase(-profit * (1f - keepFactor));
            }
        }
    }
}
