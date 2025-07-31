package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.betting.Bet;

public abstract class BetRegion {
    protected final ShapeRenderer shapeRenderer;
    protected final SpriteBatch batch;
    protected final BitmapFont font;

    protected Rectangle bounds;
    protected Color color = Color.FOREST;

    protected int betValue = 0;

    protected Chip chip = null;

    protected BetRegion(final Rectangle bounds, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        this.bounds = bounds;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public void render() {
        // draw field
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // draw outline
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // draw label
        batch.begin();
        font.getData().setScale(1.5f);
        GlyphLayout layout = new GlyphLayout(font, getLabel(), Color.WHITE, 0, Align.left, false);
        float x = bounds.x + 8;
        float y = bounds.y + 24;
        font.draw(batch, layout, x, y);
        batch.end();

        // draw chip
        if (chip != null) {
            chip.render();
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getBetValue() {
        return betValue;
    }

    public void setBetValue(int betValue) {
        this.betValue = betValue;
    }

    public abstract Bet createBet(int amount);

    public abstract String getLabel();
}
