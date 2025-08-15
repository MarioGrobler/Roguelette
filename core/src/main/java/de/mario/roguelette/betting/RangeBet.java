package de.mario.roguelette.betting;

import de.mario.roguelette.wheel.*;

import java.util.Objects;

public abstract class RangeBet implements BetType {
    private final int min;
    private final int max;

    protected RangeBet(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isWinningSegment(Segment segment) {
        if (segment instanceof NumberSegment) {
            int number = ((NumberSegment)segment).getNumber();
            return number != 0 && min <= number && number <= max;
        }
        if (segment instanceof JokerNumberRangeSegment) {
            JokerNumberRangeSegment range = (JokerNumberRangeSegment)segment;
            return range.getMax() >= min && range.getMin() <= max; //do the ranges intersect?
        }
        if (segment instanceof JokerColorSegment) {
            JokerColorSegment color = (JokerColorSegment)segment;
            for (int i = min; i <= max; i++) {
                if (RouletteRules.getStandardColor(i) == color.getCurrentColor()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RangeBet rangeBet = (RangeBet) o;
        return min == rangeBet.min && max == rangeBet.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
