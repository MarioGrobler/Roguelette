package de.mario.roguelette.betting;

public class HalfBet extends RangeBet {

    private final boolean low;

    /**
     * @param low false for 1-18, true for 19-36
     */
    public HalfBet(boolean low) {
        super((low ? 0 : 1) * 18 + 1, ((low ? 1 : 2)) * 18);
        this.low = low;
    }

    @Override
    public float getPayoutMultiplier() {
        return 2f;
    }

    public boolean isLow() {
        return low;
    }
}
