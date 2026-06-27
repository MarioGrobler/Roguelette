package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * Passive: when the balance is low relative to the current stage goal, winning bets pay out
 * extra. The bonus ramps from 0 (at the threshold) up to {@link #MAX_BONUS} (near broke), so it
 * scales with how desperate the player is and stays relevant across stages.
 *
 * <p>The "low balance" check uses the balance at the <em>start of the spin</em> (captured in
 * {@link #onSpinStart}, before the stakes are paid). Otherwise going all-in would momentarily empty
 * the balance and max out the bonus every time — rewarding big bets instead of genuine comebacks.
 */
public class ComebackKidFortune extends FortuneShopItem {

    private static final float THRESHOLD_FRACTION = 0.25f; // "low" = below 25% of the stage goal
    private static final float MAX_BONUS = 1.0f;           // up to +100% payout when nearly broke

    private float balanceAtSpinStart = Float.MAX_VALUE; // pre-stake balance; set every onSpinStart

    public ComebackKidFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/comebackKid.png")),
            Color.GOLDENROD, Color.FOREST));
        this.cost = 15;
    }

    @Override
    public void onSpinStart(final GameState gameState) {
        balanceAtSpinStart = gameState.getPlayer().getBalance();
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        float threshold = gameState.getRequiredChips() * THRESHOLD_FRACTION;
        if (threshold <= 0) {
            return;
        }
        // judge "low" by the balance going INTO the spin, not after stakes were paid (anti all-in abuse)
        float balance = balanceAtSpinStart;
        if (balance >= threshold) {
            return;
        }
        float bonus = MAX_BONUS * (1f - balance / threshold); // 0 at threshold -> MAX_BONUS at 0
        resolution.multiplyTotal(1f + bonus);
    }

    @Override
    public String getShortDescription() {
        return "Comeback Kid";
    }

    @Override
    public String getDescription() {
        return "When your balance is low (below " + Math.round(THRESHOLD_FRACTION * 100)
            + "% of the stage goal), winning bets pay out up to " + Math.round(MAX_BONUS * 100)
            + "% more. The lower your balance, the bigger the bonus.";
    }
}
