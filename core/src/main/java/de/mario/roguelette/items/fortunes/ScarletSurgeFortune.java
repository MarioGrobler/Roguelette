package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.effects.MultiplierEffect;

public class ScarletSurgeFortune extends FortuneShopItem{
    public ScarletSurgeFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/scarletSurge.png")), Color.GOLDENROD, Color.BROWN));
        this.cost = 8;
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        int rnd = MathUtils.random(gameState.getWheel().size() - 1);
        gameState.getWheel().getSegmentAt(rnd).setColor(Segment.SegmentColor.RED);
        gameState.getWheel().getSegmentAt(rnd).addEffect(new MultiplierEffect(1, 9));
    }

    @Override
    public float baseModifier(final Bet bet) {
        return 0; // no change
    }

    @Override
    public float totalModifier(final Bet bet) {
        return 1; // no change
    }

    @Override
    public String getShortDescription() {
        return "Scarlet Surge";
    }

    @Override
    public String getDescription() {
        return "Every turn, paints a random segment of the wheel red and significantly increases its multiplier for one turn.";
    }
}
