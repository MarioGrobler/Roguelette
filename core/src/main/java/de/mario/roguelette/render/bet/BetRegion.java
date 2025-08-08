package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.render.Renderable;

public abstract class BetRegion implements Renderable {
    protected final ShapeRenderer shapeRenderer;
    protected final SpriteBatch batch;
    protected final BitmapFont font;

    protected Color color = Color.FOREST;

    protected Chip chip = null;

    protected BetRegion(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
    }

    protected abstract void makeChip(int amount);

    protected void deleteChip() {
        this.chip = null;
    }

    @Override
    public abstract boolean contains(float x, float y);

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract Bet createBet(int amount);

    public abstract String getLabel();
}
