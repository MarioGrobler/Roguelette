package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.ParityBet;

public class ParityRegion extends BetRegion {
    private final boolean even;

    protected ParityRegion(boolean even, Rectangle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.even = even;
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new ParityBet(even), amount);
    }

    @Override
    public String getLabel() {
        return even ? "even" : "odd";
    }

    public boolean isEven() {
        return even;
    }
}
