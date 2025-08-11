package de.mario.roguelette.render.shop;

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

public class ChanceDraw implements Renderable {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle bounds;
    private final ChanceShopItem item;

    private float thickness;
    private boolean drawDuration = false;

    public ChanceDraw(final ChanceShopItem item, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.bounds = bounds;
        this.item = item;

        thickness = bounds.width / 10f;
    }

    @Override
    public void render() {
        // middle rect
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(item.getRenderInfo().getBackgrundColor());
        shapeRenderer.rect(bounds.x + thickness, bounds.y + thickness, bounds.width - 2*thickness, bounds.height - 2*thickness);

        // sides
        shapeRenderer.setColor(item.getRenderInfo().getBorderColor1());
        shapeRenderer.rect(bounds.x + thickness, bounds.y, bounds.width - 2 * thickness, thickness); // down
        shapeRenderer.rect(bounds.x + thickness, bounds.y + bounds.height - thickness, bounds.width - 2 * thickness, thickness); // up
        shapeRenderer.rect(bounds.x, bounds.y + thickness, thickness, bounds.height - 2 * thickness); // left
        shapeRenderer.rect(bounds.x + bounds.width - thickness, bounds.y + thickness, thickness, bounds.height - 2 * thickness); // right

        // corner circles
        shapeRenderer.circle(bounds.x + thickness, bounds.y + thickness, thickness);
        shapeRenderer.circle(bounds.x + thickness, bounds.y + bounds.height - thickness, thickness);
        shapeRenderer.circle(bounds.x + bounds.width - thickness, bounds.y + thickness, thickness);
        shapeRenderer.circle(bounds.x + bounds.width - thickness, bounds.y + bounds.height - thickness, thickness);

        // pattern
        float step = bounds.width / 5f;
        shapeRenderer.setColor(item.getRenderInfo().getBorderColor2());
        for (float i = .5f; i < 4.5f; i += 2) {
            shapeRenderer.rect(bounds.x + thickness + i*step, bounds.y, step, thickness); // down
            shapeRenderer.rect(bounds.x + thickness + i*step, bounds.y + bounds.height - thickness, step, thickness); // up
            shapeRenderer.rect(bounds.x, bounds.y + thickness + i*step, thickness, step); // left
            shapeRenderer.rect(bounds.x + bounds.width - thickness, bounds.y + thickness + i*step, thickness, step); // right
        }
        shapeRenderer.end();

        // icon
        batch.begin();
        batch.draw(item.getRenderInfo().getBackgrund(), bounds.x, bounds.y, bounds.width, bounds.height);
        batch.end();

        // draw duration if flag is set
        if (drawDuration && item instanceof PendingChanceShopItem) {
            float centerX = bounds.x + bounds.width - 2*thickness;
            float centerY = bounds.y + 2*thickness;
            float radius = 2.5f*thickness;

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
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public boolean isDrawDuration() {
        return drawDuration;
    }

    public void setDrawDuration(boolean drawDuration) {
        this.drawDuration = drawDuration;
    }
}
