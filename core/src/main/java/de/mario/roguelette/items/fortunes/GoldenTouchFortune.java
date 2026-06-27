package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * Legendary passive (boss reward): a universal, permanent payout boost. Every winning bet gains a
 * flat addition to its base multiplier — strong but additive (not compounding), so it lifts the whole
 * run without the runaway behaviour the economy pass capped on other engines.
 */
public class GoldenTouchFortune extends FortuneShopItem {

    private static final float BASE_BONUS = 0.75f;

    public GoldenTouchFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/goldenTouch.png")),
            Color.GOLDENROD, new Color(0.93f, 0.78f, 0.20f, 1f)));
        this.cost = 0; // boss reward, granted free
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin()) {
            resolution.addBase(BASE_BONUS);
        }
    }

    @Override
    public String getShortDescription() {
        return "Golden Touch";
    }

    @Override
    public String getDescription() {
        return "Legendary. Every winning bet gains +" + BASE_BONUS
            + " to its base multiplier. Always on, every win, forever.";
    }
}
