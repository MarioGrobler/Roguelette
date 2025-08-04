package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.NumberBet;
import de.mario.roguelette.wheel.RouletteRules;
import de.mario.roguelette.wheel.Segment;

public class NumberRegion extends RectRegion {

    private final int number;

    protected NumberRegion(int number, final Rectangle bounds, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.number = number;

        Segment.SegmentColor sc = RouletteRules.getStandardColor(number);
        switch (sc) {
            case RED:
                color = Color.RED;
                break;
            case BLACK:
                color = Color.BLACK;
                break;
            case NONE:
                color = Color.FOREST;
                break;
            default: //should be unreachable
                color = Color.PINK;
                break;
        }
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new NumberBet(number), amount);
    }

    @Override
    public String getLabel() {
        return String.valueOf(number);
    }

    public int getNumber() {
        return number;
    }
}
