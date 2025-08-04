package de.mario.roguelette.betting;

import de.mario.roguelette.wheel.Segment;

public interface BetType {
    /**
     * @return true if the hit segment is winning for the bet
     */
    boolean isWinningSegment(final Segment segment);

    /**
     * @return the payout multiplier (eg 2x for color, 36x for single number)
     */
    float getPayoutMultiplier();

    /**
     * @return is the bet an inside bet (split, street, ...).
     * Caution! For simplicity reasons we consider {@link NumberBet}s to be <i>not</i> inside bets!
     */
    boolean isInsideBet();
}
