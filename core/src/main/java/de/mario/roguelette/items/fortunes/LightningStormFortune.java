package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;

public class LightningStormFortune extends FortuneShopItem {
    public LightningStormFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/lightningStorm.png")), Color.GOLDENROD, new Color(0.22f, 0.4f, 0.85f, 1f)));
        this.cost = 15;
    }

    @Override
    public void onTurnChange(GameState gameState) {
        for (int i = 0; i < 3; i++) {
            int index = MathUtils.random(0, gameState.getWheel().size() - 1);
            gameState.getWheel().getSegmentAt(index).setMultiplier(gameState.getWheel().getSegmentAt(index).getMultiplier() + 0.5f);
        }
    }

    @Override
    public String getShortDescription() {
        return "Lightning Storm";
    }

    @Override
    public String getDescription() {
        return "Every turn, randomly selects a segment three times and increases their multiplier.";
    }

}
