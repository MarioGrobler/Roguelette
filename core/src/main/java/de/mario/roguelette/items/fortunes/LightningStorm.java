package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;

public class LightningStorm extends FortuneShopItem {
    public LightningStorm() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/lightningStorm.png")), Color.GOLDENROD, Color.BROWN));
    }

    @Override
    public void onTurnChange(GameState gameState) {
        for (int i = 0; i < 3; i++) {
            int index = MathUtils.random(0, gameState.getWheel().size() - 1);
            gameState.getWheel().getSegmentAt(index).setMultiplier(gameState.getWheel().getSegmentAt(index).getMultiplier() + 0.5f);
        }
    }

    @Override
    public float baseModifier(Bet bet) {
        return 0; // no change
    }

    @Override
    public float totalModifier(Bet bet) {
        return 1; // no change
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
