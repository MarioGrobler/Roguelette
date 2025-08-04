package de.mario.roguelette.betting;

import de.mario.roguelette.exception.GameException;
import de.mario.roguelette.wheel.Segment;

import java.util.Arrays;
import java.util.Objects;

public class CornerBet implements BetType {

    private final NumberBet[] fourBets = new NumberBet[4];

    /**
     * Creates a corner bet (four numbers) based on the number given.
     * Expects the number to be the smallest of the four corner numbers, that is,
     * if the parameter is n, creates a bet for the numbers n, n+1, n+3, and n+4
     */
    public CornerBet(int firstNumber) {
        if (firstNumber < 1 || firstNumber > 31 || firstNumber % 3 == 0) {
            throw new GameException("Illegal number for corner bet");
        }
        fourBets[0] = new NumberBet(firstNumber);
        fourBets[1] = new NumberBet(firstNumber + 1);
        fourBets[2] = new NumberBet(firstNumber + 3);
        fourBets[3] = new NumberBet(firstNumber + 4);
    }

    @Override
    public boolean isWinningSegment(Segment segment) {
        for (NumberBet numberBet : fourBets) {
            if (numberBet.isWinningSegment(segment)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public float getPayoutMultiplier() {
        return 9f;
    }

    @Override
    public boolean isInsideBet() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CornerBet cornerBet = (CornerBet) o;
        return Objects.deepEquals(fourBets, cornerBet.fourBets);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fourBets);
    }

    public int getFirstNumber() {
        return fourBets[0].getNumber();
    }
}
