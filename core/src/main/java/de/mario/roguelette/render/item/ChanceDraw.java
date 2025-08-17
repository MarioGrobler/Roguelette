package de.mario.roguelette.render.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.PendingChanceShopItem;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.render.RoundedRectRenderer;

public class ChanceDraw implements Renderable {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle bounds;
    private final ChanceShopItem item;

    private final RoundedRectRenderer roundedRectRenderer;

    private boolean drawDuration = false;

    public ChanceDraw(final ChanceShopItem item, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.bounds = bounds;
        this.item = item;

        roundedRectRenderer = new RoundedRectRenderer(shapeRenderer, bounds);
        roundedRectRenderer.setFillColor(item.getRenderInfo().getBackgrundColor());
        roundedRectRenderer.setBorderColor(item.getRenderInfo().getBorderColor1());
        roundedRectRenderer.setThickness(bounds.width / 10f);
        roundedRectRenderer.setRadius(bounds.width / 10f);
    }

    @Override
    public void render() {
        roundedRectRenderer.render();

        // pattern
        float step = bounds.width / 5f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(item.getRenderInfo().getBorderColor2());
        for (float i = .5f; i < 4.5f; i += 2) {
            shapeRenderer.rect(bounds.x + roundedRectRenderer.getThickness() + i*step, bounds.y, step, roundedRectRenderer.getThickness()); // down
            shapeRenderer.rect(bounds.x + roundedRectRenderer.getThickness() + i*step, bounds.y + bounds.height - roundedRectRenderer.getThickness(), step, roundedRectRenderer.getThickness()); // up
            shapeRenderer.rect(bounds.x, bounds.y + roundedRectRenderer.getThickness() + i*step, roundedRectRenderer.getThickness(), step); // left
            shapeRenderer.rect(bounds.x + bounds.width - roundedRectRenderer.getThickness(), bounds.y + roundedRectRenderer.getThickness() + i*step, roundedRectRenderer.getThickness(), step); // right
        }
        shapeRenderer.end();

        // icon
        batch.begin();
        float thickness = roundedRectRenderer.getThickness();
        batch.draw(item.getRenderInfo().getBackgrund(), bounds.x + thickness, bounds.y + thickness, bounds.width - 2*thickness, bounds.height - 2*thickness);
        batch.end();

        // draw duration if flag is set
        if (drawDuration && item instanceof PendingChanceShopItem) {
            float centerX = bounds.x + bounds.width - 2*roundedRectRenderer.getThickness();
            float centerY = bounds.y + 2*roundedRectRenderer.getThickness();
            float radius = 2.5f*roundedRectRenderer.getThickness();

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.9f));
            shapeRenderer.circle(centerX, centerY, radius);
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
            shapeRenderer.circle(centerX, centerY, radius);
            shapeRenderer.end();

            String dur = String.valueOf(((PendingChanceShopItem) item).getDuration());
            font.getData().setScale(1.5f);
            GlyphLayout layout = new GlyphLayout(font, dur, Color.BLACK, 0, Align.center, false);
            batch.begin();
            font.draw(batch, layout, centerX, centerY + layout.height/2f);
            batch.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public float getThickness() {
        return roundedRectRenderer.getThickness();
    }

    public void setThickness(float thickness) {
        roundedRectRenderer.setThickness(thickness);
    }

    public boolean isDrawDuration() {
        return drawDuration;
    }

    public void setDrawDuration(boolean drawDuration) {
        this.drawDuration = drawDuration;
    }
}
