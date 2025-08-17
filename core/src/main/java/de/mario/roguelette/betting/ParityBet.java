package de.mario.roguelette.betting;

import de.mario.roguelette.wheel.JokerNumberRangeSegment;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;

import java.util.Objects;

public class ParityBet implements BetType {

    private final boolean even;

    /**
     * @param even true if even, false if odd
     */
    public ParityBet(boolean even) {
        this.even = even;
    }

    @Override
    public boolean isWinningSegment(Segment segment) {
        if(segment instanceof NumberSegment) {
            NumberSegment numberSegment = (NumberSegment)segment;
            return numberSegment.getCurrentNumber() != 0 && (numberSegment.getCurrentNumber() % 2 == 0) == even;
        }
        if(segment instanceof JokerNumberRangeSegment) {
            JokerNumberRangeSegment range = (JokerNumberRangeSegment)segment;
            return range.getMin() != range.getMax() || (range.getMin() % 2 == 0) == even ; // only false if range is trivial and of incorrect parity
        }
        // Color Joker: always false
        return false;
    }

    @Override
    public float getPayoutMultiplier() {
        return 2f;
    }

    @Override
    public boolean isInsideBet() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParityBet parityBet = (ParityBet) o;
        return even == parityBet.even;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(even);
    }

    public boolean isEven() {
        return even;
    }
}
