package de.mario.roguelette.betting;

import de.mario.roguelette.GameState;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.wheel.Segment;

import java.util.Collections;
import java.util.List;

public class Bet {
    private final BetType betType;
    private long amount;

    public Bet(BetType betType, long amount) {
        this.betType = betType;
        this.amount = amount;
    }

    public boolean isWin(final Segment landed) {
        return betType.isWinningSegment(landed);
    }

    /**
     * @return the payout for this bet given a single landing. See {@link #getPayout(List, List, GameState)}.
     */
    public double getPayout(final Segment landed, final GameState gameState) {
        return getPayout(Collections.singletonList(landed), null, gameState);
    }

    /** Payout without per-ball information; every landing pays at full factor. */
    public double getPayout(final List<Segment> landedSegments, final GameState gameState) {
        return getPayout(landedSegments, null, gameState);
    }

    /**
     * @param landedBalls the ball behind each landing, parallel to {@code landedSegments} (may be
     *                    {@code null} / shorter — missing entries pay at full factor)
     * @return the payout for this bet across every ball's landing segment. On a win the payout
     * follows the formula
     * <pre>amount * (base multiplier + listener base modifiers) * segment multiplier * listener total modifiers * ball payout factor</pre>
     * and, with multiple balls, the winnings of each winning landing are <em>summed</em> (so a
     * second ball that also wins pays again). A refund (e.g. Insurance) only applies if the bet
     * won on <em>no</em> ball and is counted once, capped at the full stake — multiple balls do
     * not multiply the refund (and a ball's payout factor does not shrink it).
     */
    public double getPayout(final List<Segment> landedSegments, final List<Ball> landedBalls, final GameState gameState) {
        double winnings = 0d;
        boolean wonAny = false;
        float refundFraction = 0f;

        for (int i = 0; i < landedSegments.size(); i++) {
            Segment landed = landedSegments.get(i);
            boolean win = isWin(landed);
            BetResolution resolution = new BetResolution(this, landed, win);
            gameState.dispatchResolveBet(resolution);

            if (win) {
                wonAny = true;
                double base = betType.getPayoutMultiplier() + resolution.getBaseAdd();
                float ballFactor = (landedBalls != null && i < landedBalls.size()) ? landedBalls.get(i).getPayoutFactor() : 1f;
                winnings += (double) amount * base * landed.getCurrentMultiplier() * resolution.getTotalMul() * ballFactor;
            } else {
                refundFraction = Math.max(refundFraction, resolution.getRefundFraction());
            }
        }

        if (wonAny) {
            // a winning bet never returns less than its stake: a "win" must never be a net loss,
            // however harshly payout-reducing listeners (e.g. boss debuffs) have stacked
            return Math.max(winnings, amount);
        }
        return amount * Math.min(1f, refundFraction);
    }

    public BetType getBetType() {
        return betType;
    }

    public long getAmount() {
        return amount;
    }

    public void increaseAmount(long by) {
        this.amount += by;
    }


}
