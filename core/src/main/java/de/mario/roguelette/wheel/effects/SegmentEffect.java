package de.mario.roguelette.wheel.effects;

public abstract class SegmentEffect {
    protected int remainingRounds;

    public SegmentEffect(int remainingRounds) {
        this.remainingRounds = remainingRounds;
    }

    /**
     * Decreases the number of remaining rounds
     *
     * @return true if no rounds are left
     */
    public boolean onTurnChange() {
        remainingRounds--;
        return remainingRounds <= 0;
    }
}
