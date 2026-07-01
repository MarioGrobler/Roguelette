package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.events.SpinContext;

/**
 * Legendary passive (boss reward): every spin permanently plays one extra ball whose payouts
 * count half. Roughly ~1.5x throughput for the rest of the run — the permanent little sibling of
 * the Double Ball chance (whose one-turn ball stays at full strength). First user of the per-ball
 * payout factor on {@link Ball}.
 */
public class TwinBallFortune extends FortuneShopItem {

    private static final float TWIN_PAYOUT_FACTOR = 0.5f;
    private static final Color TWIN_TINT = new Color(0.75f, 0.55f, 0.95f, 1f); // violet, to tell it apart

    public TwinBallFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/twinBall.png")),
            Color.GOLDENROD, new Color(0.55f, 0.35f, 0.8f, 1f)));
        this.cost = 0; // boss reward, granted free
    }

    @Override
    public void onPrepareSpin(final GameState gameState, final SpinContext spin) {
        spin.addBall(new Ball(TWIN_TINT, "Twin Ball", TWIN_PAYOUT_FACTOR));
    }

    @Override
    public String getShortDescription() {
        return "Twin Ball";
    }

    @Override
    public String getDescription() {
        return "Legendary. Every spin plays an extra ball whose winnings count "
            + Math.round(TWIN_PAYOUT_FACTOR * 100) + "%. Forever.";
    }
}
