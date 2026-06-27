package de.mario.roguelette.boss;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;

/**
 * Boss debuff: the house skims a fixed fraction off every win's <em>profit</em>. The player keeps
 * {@code keepFactor} of the profit (the winnings above the returned stake), so a win is always still a
 * win, just smaller — it never turns a winning bet into a net loss (which scaling the whole gross
 * payout would do for low-multiplier bets like even-money colour).
 */
public class HouseEdgeEffect implements GameEventListener {

    private final float keepFactor; // e.g. 0.75 -> the player keeps 75% of each win's profit

    public HouseEdgeEffect(final float keepFactor) {
        this.keepFactor = keepFactor;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin()) {
            // base payout multiplier is gross (stake-inclusive), so profit = base - 1
            float profit = resolution.getBet().getBetType().getPayoutMultiplier() - 1f;
            if (profit > 0f) {
                resolution.addBase(-profit * (1f - keepFactor));
            }
        }
    }
}
