package de.mario.roguelette.betting;

import de.mario.roguelette.wheel.*;

import java.util.Objects;

public class NumberBet implements BetType {

    private final int number;

    public NumberBet(int number) {
        this.number = number;
    }

    @Override
    public boolean isWinningSegment(Segment segment) {
        if(segment instanceof NumberSegment) {
            return ((NumberSegment)segment).getNumber() == number;
        }
        if(segment instanceof JokerNumberRangeSegment) {
            JokerNumberRangeSegment range = (JokerNumberRangeSegment)segment;
            return range.getMin() <= number && number <= range.getMax();
        }
        if(segment instanceof JokerColorSegment) {
            return RouletteRules.getStandardColor(number) == segment.getColor(); //TODO this might break if the betting area changes due to some effect
        }
        return false;
    }

    @Override
    public float getPayoutMultiplier() {
        return 36f;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        NumberBet numberBet = (NumberBet) o;
        return number == numberBet.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }

    public int getNumber() {
        return number;
    }
}
