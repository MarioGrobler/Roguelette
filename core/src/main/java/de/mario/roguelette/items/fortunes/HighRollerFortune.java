package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * Passive: bets staked at a serious fraction of the bankroll gain a base-multiplier bonus on a win.
 * Rewards playing the run's big-hit identity and does nothing for turtling.
 *
 * <p>The stake is judged against the balance at the <em>start of the spin</em> (same anti-abuse
 * pattern as {@link ComebackKidFortune}): measured post-stake, any bet would trivially qualify
 * against the drained balance.
 */
public class HighRollerFortune extends FortuneShopItem {

    private static final float THRESHOLD_FRACTION = 0.25f; // stake >= 25% of pre-spin balance
    private static final float BASE_BONUS = 0.5f;

    private long balanceAtSpinStart = Long.MAX_VALUE; // pre-stake balance; set every onSpinStart

    public HighRollerFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/highRoller.png")),
            Color.GOLDENROD, new Color(0.55f, 0.1f, 0.35f, 1f)));
        this.cost = 18;
    }

    @Override
    public void onSpinStart(final GameState gameState) {
        balanceAtSpinStart = gameState.getPlayer().getBalance();
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin() || balanceAtSpinStart <= 0) {
            return;
        }
        if (resolution.getBet().getAmount() >= balanceAtSpinStart * (double) THRESHOLD_FRACTION) {
            resolution.addBase(BASE_BONUS);
        }
    }

    @Override
    public String getShortDescription() {
        return "High Roller";
    }

    @Override
    public String getDescription() {
        return "Bets staked at " + Math.round(THRESHOLD_FRACTION * 100)
            + "% or more of your balance gain +" + BASE_BONUS
            + " to their base multiplier when they win. Fortune favours the bold.";
    }
}
