package de.mario.roguelette.betting;

import de.mario.roguelette.exception.GameException;

public class VerticalSplitBet extends SplitBet {

    /**
     * Creates a vertical split bet (two numbers) based on the number given.
     * Expects the number to be the smaller one, that is,
     * if the parameter is n, creates a bet for the numbers n and n+3.
     */
    public VerticalSplitBet(int firstNumber) {
        super(firstNumber, firstNumber + 3);
        if (firstNumber < 1 || firstNumber > 33) {
            throw new GameException("Illegal number for horizontal bet");
        }
    }
}
