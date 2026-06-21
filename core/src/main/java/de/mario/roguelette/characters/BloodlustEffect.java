package de.mario.roguelette.characters;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;

/**
 * The Count's signature passive: a glass-cannon snowball driven by feeding on wins.
 *
 * <p>Each winning <em>spin</em> adds a permanent {@value #BONUS_PER_STACK}-per-stack payout bonus
 * with no cap, so a winning streak compounds viciously. But a losing spin (no bet won) is
 * punishing: the streak resets to zero <em>and</em> the Count "starves", bleeding
 * {@value #BLEED} of the current balance. Snowball hard or bleed out.
 *
 * <p>Stateful, modelled on {@code StreakBonusFortune}: {@link #stacks} is only updated in
 * {@link #onTurnChange} (after every bet of a spin has resolved), so during {@link #onResolveBet}
 * it reflects the streak going <em>into</em> the current spin. {@link #wonThisTurn} collects
 * whether the current spin produced any win. {@code onResolveBet} runs before {@code onTurnChange}
 * each spin, and {@code onTurnChange} fires exactly once per spin.
 */
public class BloodlustEffect implements GameEventListener {

    private static final float BONUS_PER_STACK = 0.25f; // +25% payout per consecutive winning spin
    private static final float BLEED = 0.15f;           // lose 15% of balance on a losing spin

    private int stacks = 0;
    private boolean wonThisTurn = false;

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        wonThisTurn = true;
        if (stacks > 0) {
            resolution.multiplyTotal(1f + stacks * BONUS_PER_STACK);
        }
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        if (wonThisTurn) {
            stacks++;
        } else {
            stacks = 0;
            int bleed = Math.round(gameState.getPlayer().getBalance() * BLEED);
            if (bleed > 0) {
                gameState.getPlayer().pay(bleed);
            }
        }
        wonThisTurn = false;
    }

    public static int bonusPercent() {
        return Math.round(BONUS_PER_STACK * 100);
    }

    public static int bleedPercent() {
        return Math.round(BLEED * 100);
    }
}
