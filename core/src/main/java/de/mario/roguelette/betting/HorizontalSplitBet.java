package de.mario.roguelette.betting;

import de.mario.roguelette.exception.GameException;

public class HorizontalSplitBet extends SplitBet {

    /**
     * Creates a split bet (two numbers) based in the number given.
     * Expects the number to be the smaller one, that is,
     * if the parameter is n, creates a bet for the numbers n and n+1.
     */
    public HorizontalSplitBet(int firstNumber) {
        super(firstNumber, firstNumber + 1);
        if (firstNumber < 1 || firstNumber > 34 || firstNumber % 3 == 0) {
            throw new GameException("Illegal number for horizontal bet");
        }
    }
}
