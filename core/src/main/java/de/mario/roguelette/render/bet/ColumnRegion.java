package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.ColumnBet;

public class ColumnRegion extends RectRegion {

    private final ColumnBet.Column column;

    protected ColumnRegion(ColumnBet.Column column, Rectangle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.column = column;
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new ColumnBet(column), amount);
    }

    @Override
    public String getLabel() {
        return column.toString();
    }

    public ColumnBet.Column getColumn() {
        return column;
    }
}
