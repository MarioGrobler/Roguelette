package de.mario.roguelette.betting;

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

    public float getPayout(final Segment landed) {
        if (isWin(landed)) {
            //TODO: there might be even more factors
            return amount * betType.getPayoutMultiplier() * landed.getMultiplier();
        }
        return 0;
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
