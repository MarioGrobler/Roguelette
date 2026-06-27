package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * Legendary passive (boss reward): a stronger, <em>capped</em> cousin of {@link StreakBonusFortune}.
 * Consecutive winning spins ramp a payout multiplier hard, but the stack is capped so it can't run
 * away (per the economy pass's rule that compounding engines stay bounded). A losing spin resets it.
 *
 * <p>Stateful, same contract as {@link StreakBonusFortune}: {@link #streak} is updated only in
 * {@link #onTurnChange} so during {@link #onResolveBet} it reflects the streak going into this spin.
 */
public class HotStreakFortune extends FortuneShopItem {

    private static final float BONUS_PER_STREAK = 0.5f; // +50% payout per consecutive winning spin
    private static final int MAX_STREAK = 5;            // capped at +250%

    private int streak = 0;
    private boolean wonThisTurn = false;

    public HotStreakFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/hotStreak.png")),
            Color.GOLDENROD, new Color(0.90f, 0.40f, 0.10f, 1f)));
        this.cost = 0; // boss reward, granted free
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
            streak = Math.min(streak + 1, MAX_STREAK);
        } else {
            streak = 0;
        }
        wonThisTurn = false;
    }

    @Override
    public String getShortDescription() {
        return "Hot Streak";
    }

    @Override
    public String getDescription() {
        return "Legendary. Each consecutive spin you win adds +" + Math.round(BONUS_PER_STREAK * 100)
            + "% payout, up to +" + Math.round(MAX_STREAK * BONUS_PER_STREAK * 100)
            + "%. A spin with no win resets it.\nCurrent streak: " + streak;
    }
}
