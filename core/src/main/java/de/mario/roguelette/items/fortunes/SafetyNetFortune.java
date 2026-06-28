package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * Legendary passive (boss reward): permanent partial insurance. Every losing bet refunds a fixed
 * fraction of its stake, so aggressive betting bleeds far slower — a safety net under the whole run's
 * variance. The refund is capped by the payout formula at the full stake.
 */
public class SafetyNetFortune extends FortuneShopItem {

    private static final float REFUND = 0.30f;

    public SafetyNetFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/safetyNet.png")),
            Color.GOLDENROD, new Color(0.20f, 0.62f, 0.45f, 1f)));
        this.cost = 0; // boss reward, granted free
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            resolution.addRefund(REFUND);
        }
    }

    @Override
    public String getShortDescription() {
        return "Safety Net";
    }

    @Override
    public String getDescription() {
        return "Legendary. Every losing bet refunds " + Math.round(REFUND * 100)
            + "% of its stake. Bet big and bleed slow.";
    }
}
