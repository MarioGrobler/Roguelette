package de.mario.roguelette.events;

import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.wheel.Segment;

/**
 * Mutable accumulator for a single bet's payout, passed to {@link GameEventListener#onResolveBet}.
 *
 * <p>The final payout follows the formula
 * <pre>amount * (baseTypeMultiplier + baseAdd) * segmentMultiplier * totalMul</pre>
 * on a win, or {@code amount * refundFraction} on a loss. Listeners add their contributions
 * through {@link #addBase}, {@link #multiplyTotal} and {@link #addRefund}.
 */
public class BetResolution {
    private final Bet bet;
    private final Segment landed;
    private final boolean win;

    private float baseAdd = 0f;
    private float totalMul = 1f;
    private float refundFraction = 0f;

    public BetResolution(final Bet bet, final Segment landed, final boolean win) {
        this.bet = bet;
        this.landed = landed;
        this.win = win;
    }

    public Bet getBet() {
        return bet;
    }

    public Segment getLanded() {
        return landed;
    }

    public boolean isWin() {
        return win;
    }

    /** Adds to the additive base multiplier (only meaningful on a win). */
    public void addBase(final float amount) {
        this.baseAdd += amount;
    }

    /** Multiplies the final total multiplier (only meaningful on a win). */
    public void multiplyTotal(final float factor) {
        this.totalMul *= factor;
    }

    /** Adds to the fraction of the stake refunded (only meaningful on a loss). */
    public void addRefund(final float fraction) {
        this.refundFraction += fraction;
    }

    public float getBaseAdd() {
        return baseAdd;
    }

    public float getTotalMul() {
        return totalMul;
    }

    public float getRefundFraction() {
        return refundFraction;
    }
}
