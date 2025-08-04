package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.CornerBet;

public class CornerRegion extends InsideBetRegion {

    private final int firstNumber;

    protected CornerRegion(int firstNumber, Circle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.firstNumber = firstNumber;
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new CornerBet(firstNumber), amount);
    }

    @Override
    public String getLabel() {
        return "";
    }

    public int getFirstNumber() {
        return firstNumber;
    }
}
