package de.mario.roguelette.betting;

import de.mario.roguelette.wheel.Segment;

import java.util.Arrays;
import java.util.Objects;

public abstract class SplitBet implements BetType {

    protected final NumberBet[] numberBets = new NumberBet[2];

    protected SplitBet(int firstNumber, int secondNumber) {
        this.numberBets[0] = new NumberBet(firstNumber);
        this.numberBets[1] = new NumberBet(secondNumber);
    }

    @Override
    public boolean isWinningSegment(Segment segment) {
        return numberBets[0].isWinningSegment(segment) || numberBets[1].isWinningSegment(segment);
    }

    @Override
    public float getPayoutMultiplier() {
        return 18f;
    }

    @Override
    public boolean isInsideBet() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SplitBet splitBet = (SplitBet) o;
        return Objects.deepEquals(numberBets, splitBet.numberBets);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(numberBets);
    }

    public int getFirstNumber() {
        return numberBets[0].getNumber();
    }
}
