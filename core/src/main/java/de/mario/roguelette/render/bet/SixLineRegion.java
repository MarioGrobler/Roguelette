package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.SixLineBet;

public class SixLineRegion extends InsideBetRegion {

    private final SixLineBet.SixLine sixLine;

    protected SixLineRegion(SixLineBet.SixLine sixLine, Circle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.sixLine = sixLine;
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new SixLineBet(sixLine), amount);
    }

    @Override
    public String getLabel() {
        return "";
    }

    public SixLineBet.SixLine getSixLine() {
        return sixLine;
    }
}
