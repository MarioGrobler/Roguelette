package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * Passive: consecutive winning spins ramp up a payout multiplier; a spin with no win resets it.
 *
 * <p>The streak counts <em>spins</em> (a spin is a "win" if any bet won), not individual bets.
 * {@link #streak} is the number of prior consecutive winning spins and is only updated in
 * {@link #onTurnChange} (after all bets of a spin have resolved), so during {@link #onResolveBet}
 * it correctly reflects the run going <em>into</em> the current spin. {@link #wonThisTurn}
 * collects whether the current spin produced any win.
 */
public class StreakBonusFortune extends FortuneShopItem {

    private static final float BONUS_PER_STREAK = 0.25f; // +25% payout per consecutive winning spin

    private int streak = 0;
    private boolean wonThisTurn = false;

    public StreakBonusFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/streakBonus.png")),
            Color.GOLDENROD, Color.FIREBRICK));
        this.cost = 18;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        wonThisTurn = true;
        if (streak > 0) {
            resolution.multiplyTotal(1f + streak * BONUS_PER_STREAK);
        }
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        if (wonThisTurn) {
            streak++;
        } else {
            streak = 0;
        }
        wonThisTurn = false;
    }

    @Override
    public String getShortDescription() {
        return "Streak Bonus";
    }

    @Override
    public String getDescription() {
        return "Each consecutive spin you win increases your payout by " + Math.round(BONUS_PER_STREAK * 100)
            + "%. A spin with no win resets the streak.\nCurrent streak: " + streak;
    }
}
