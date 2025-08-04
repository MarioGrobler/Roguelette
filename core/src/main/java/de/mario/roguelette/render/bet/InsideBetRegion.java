package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;


public abstract class InsideBetRegion extends BetRegion {

    protected Circle bounds;

    protected InsideBetRegion(Circle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(shapeRenderer, batch, font);
        this.bounds = bounds;
        this.color = Color.YELLOW;
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    @Override
    protected void makeChip(int amount) {
        this.chip = new Chip(new Circle(bounds.x, bounds.y, 15), amount, 0, shapeRenderer, batch, font);
        this.chip.setFontScale(1f);
    }

    @Override
    public void render() {
        // not much to do here

        // draw chip
        if (chip != null) {
            chip.render();
        }
    }

    public Circle getBounds() {
        return bounds;
    }

    public void setBounds(Circle bounds) {
        this.bounds = bounds;
    }
}
