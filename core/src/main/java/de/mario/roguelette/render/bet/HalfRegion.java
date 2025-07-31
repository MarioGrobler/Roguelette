package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.HalfBet;

public class HalfRegion extends BetRegion {
    private final boolean low; // true: 1-18, false: 19-36

    protected HalfRegion(boolean low, Rectangle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.low = low;
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new HalfBet(low), amount);
    }

    @Override
    public String getLabel() {
        return low ? "1-18" : "19-36";
    }

    public boolean isLow() {
        return low;
    }
}
