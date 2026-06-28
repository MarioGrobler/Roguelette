package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.util.ColorHelper;

public abstract class RectRegion extends BetRegion {
    protected Rectangle bounds;

    protected RectRegion(Rectangle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(shapeRenderer, batch, font);
        this.bounds = bounds;
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    @Override
    public void render() {
        // draw field with gradient (darker at bottom, lighter at top)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Color darkColor = ColorHelper.darken(color, 0.2f);
        Color lightColor = ColorHelper.lighten(color, 0.1f);

        float targetStripHeight = 4f;
        int strips = Math.max(2, (int) Math.ceil(bounds.height / targetStripHeight));
        float stripHeight = bounds.height / strips;

        for (int i = 0; i < strips; i++) {
            float t = (float) i / (strips - 1);
            Color stripColor = new Color(
                lerp(darkColor.r, lightColor.r, t),
                lerp(darkColor.g, lightColor.g, t),
                lerp(darkColor.b, lightColor.b, t),
                1f
            );
            shapeRenderer.setColor(stripColor);
            float y = bounds.y + i * stripHeight;
            shapeRenderer.rect(bounds.x, y, bounds.width, stripHeight + 0.5f);
        }
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

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    protected void makeChip(long amount) {
        float centerX = bounds.getX() + bounds.getWidth() / 2f;
        float centerY = bounds.getY() + bounds.getHeight() / 2f;
        this.chip = new Chip(new Circle(centerX, centerY, 25), amount, 0, shapeRenderer, batch, font);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
}
