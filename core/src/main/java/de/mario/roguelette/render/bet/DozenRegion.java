package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.DozenBet;

public class DozenRegion extends BetRegion {

    private final DozenBet.Dozen dozen;

    protected DozenRegion(DozenBet.Dozen dozen, Rectangle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.dozen = dozen;
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new DozenBet(dozen), amount);
    }

    @Override
    public String getLabel() {
        return dozen.toString();
    }

    public DozenBet.Dozen getDozen() {
        return dozen;
    }
}
