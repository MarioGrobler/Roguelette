package de.mario.roguelette.render.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.util.ColorHelper;

public class ShopRenderer {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle bounds;

    private final Color color = new Color(0.5f, 0.35f, 0.2f, 1);
    private final float thickness = 5f;

    public ShopRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.bounds = bounds;
    }

    private void drawRoundedRect(final Rectangle rect, float radius) {
        // middle
        shapeRenderer.rect(rect.x + radius, rect.y + radius, rect.width - 2 * radius, rect.height - 2 * radius);

        // sides
        shapeRenderer.rect(rect.x + radius, rect.y, rect.width - 2 * radius, radius); // unten
        shapeRenderer.rect(rect.x + radius, rect.y + rect.height - radius, rect.width - 2 * radius, radius); // oben
        shapeRenderer.rect(rect.x, rect.y + radius, radius, rect.height - 2 * radius); // links
        shapeRenderer.rect(rect.x + rect.width - radius, rect.y + radius, radius, rect.height - 2 * radius); // rechts

        // rounded corners
        shapeRenderer.circle(rect.x + radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + radius, rect.y + rect.height - radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + rect.height - radius, radius);
    }

    public void render() {
        // outer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        drawRoundedRect(bounds, 10f);

        // inner
        shapeRenderer.setColor(ColorHelper.darker(color));
        Rectangle inner = new Rectangle(bounds.x + thickness, bounds.y + thickness, bounds.width - 2*thickness, bounds.height - 2*thickness);
        drawRoundedRect(inner, 10f);

        // lines
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(bounds.x + bounds.width/3f, bounds.y + 20, bounds.x + bounds.width/3f, bounds.y + bounds.height - 20, 3);
        shapeRenderer.rectLine(bounds.x + bounds.width*2/3f, bounds.y + 20, bounds.x + bounds.width*2/3f, bounds.y + bounds.height - 20, 3);
        shapeRenderer.end();
    }
}
