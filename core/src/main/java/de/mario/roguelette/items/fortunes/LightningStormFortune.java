package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;

public class LightningStormFortune extends FortuneShopItem {

    // Per-segment cap on the multiplier this effect can add. Uncapped, +0.5 to 3 segments every
    // turn juices the whole wheel to infinity (any winning bet becomes a huge hit) -- a solo
    // win-engine. Capped, it raises the CEILING for combo plays (target a juiced segment) without
    // running away.
    private static final float MAX_SEGMENT_MULTIPLIER = 3.0f;
    private static final int   STRIKES_PER_TURN = 3;

    public LightningStormFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/lightningStorm.png")), Color.GOLDENROD, new Color(0.22f, 0.4f, 0.85f, 1f)));
        this.cost = 15;
    }

    @Override
    public void onTurnChange(GameState gameState) {
        for (int i = 0; i < STRIKES_PER_TURN; i++) {
            int index = MathUtils.random(0, gameState.getWheel().size() - 1);
            float current = gameState.getWheel().getSegmentAt(index).getMultiplier();
            if (current < MAX_SEGMENT_MULTIPLIER) {
                gameState.getWheel().getSegmentAt(index).setMultiplier(Math.min(MAX_SEGMENT_MULTIPLIER, current + 0.5f));
            }
        }
    }

    @Override
    public String getShortDescription() {
        return "Lightning Storm";
    }

    @Override
    public String getDescription() {
        return "Every turn, strikes " + STRIKES_PER_TURN + " random segments and raises their multiplier by 0.5 (up to "
            + (int) MAX_SEGMENT_MULTIPLIER + "x).";
    }

}
