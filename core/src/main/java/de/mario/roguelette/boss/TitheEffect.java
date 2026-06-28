package de.mario.roguelette.boss;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.GameEventListener;

/**
 * Boss debuff: at the end of every boss spin the boss skims a fraction of the player's balance. Bites
 * regardless of how the player bets, so it bleeds even a passive player toward defeat — the gain goal
 * has to outrun the tithe.
 */
public class TitheEffect implements GameEventListener {

    private final float fraction; // e.g. 0.10 -> 10% of the balance is skimmed each spin

    public TitheEffect(final float fraction) {
        this.fraction = fraction;
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        long skim = Math.round(gameState.getPlayer().getBalance() * (double) fraction);
        if (skim > 0) {
            gameState.getPlayer().pay(skim);
        }
    }
}
