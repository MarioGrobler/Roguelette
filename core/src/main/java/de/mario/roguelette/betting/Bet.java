package de.mario.roguelette.betting;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.wheel.Segment;

public class Bet {
    private final BetType betType;
    private int amount;

    public Bet(BetType betType, int amount) {
        this.betType = betType;
        this.amount = amount;
    }

    public boolean isWin(final Segment landed) {
        return betType.isWinningSegment(landed);
    }

    /**
     * @return the payout for this bet. On a win, the payout follows the formula
     * <pre>amount * (base multiplier + listener base modifiers) * segment multiplier * listener total modifiers</pre>
     * On a loss, listeners (e.g. Insurance) may refund part of the stake; the refund is capped at
     * the full stake.
     */
    public float getPayout(final Segment landed, final GameState gameState) {
        boolean win = isWin(landed);
        BetResolution resolution = new BetResolution(this, landed, win);
        gameState.dispatchResolveBet(resolution);

        if (win) {
            float base = betType.getPayoutMultiplier() + resolution.getBaseAdd();
            return amount * base * landed.getCurrentMultiplier() * resolution.getTotalMul();
        }
        return amount * Math.min(1f, resolution.getRefundFraction());
    }

    public BetType getBetType() {
        return betType;
    }

    public int getAmount() {
        return amount;
    }

    public void increaseAmount(int by) {
        this.amount += by;
    }


}
