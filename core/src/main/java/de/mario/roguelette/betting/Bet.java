package de.mario.roguelette.betting;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.wheel.Segment;

import java.util.Collections;
import java.util.List;

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
     * @return the payout for this bet given a single landing. See {@link #getPayout(List, GameState)}.
     */
    public float getPayout(final Segment landed, final GameState gameState) {
        return getPayout(Collections.singletonList(landed), gameState);
    }

    /**
     * @return the payout for this bet across every ball's landing segment. On a win the payout
     * follows the formula
     * <pre>amount * (base multiplier + listener base modifiers) * segment multiplier * listener total modifiers</pre>
     * and, with multiple balls, the winnings of each winning landing are <em>summed</em> (so a
     * second ball that also wins pays again). A refund (e.g. Insurance) only applies if the bet
     * won on <em>no</em> ball and is counted once, capped at the full stake — multiple balls do
     * not multiply the refund.
     */
    public float getPayout(final List<Segment> landedSegments, final GameState gameState) {
        float winnings = 0f;
        boolean wonAny = false;
        float refundFraction = 0f;

        for (Segment landed : landedSegments) {
            boolean win = isWin(landed);
            BetResolution resolution = new BetResolution(this, landed, win);
            gameState.dispatchResolveBet(resolution);

            if (win) {
                wonAny = true;
                float base = betType.getPayoutMultiplier() + resolution.getBaseAdd();
                winnings += amount * base * landed.getCurrentMultiplier() * resolution.getTotalMul();
            } else {
                refundFraction = Math.max(refundFraction, resolution.getRefundFraction());
            }
        }

        if (wonAny) {
            return winnings;
        }
        return amount * Math.min(1f, refundFraction);
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
