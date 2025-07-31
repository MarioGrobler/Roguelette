package de.mario.roguelette.betting;

import de.mario.roguelette.wheel.Segment;

public class CornerBet implements BetType {


    @Override
    public boolean isWinningSegment(Segment segment) {
        return false;
    }

    @Override
    public float getPayoutMultiplier() {
        return 9f;
    }
}
