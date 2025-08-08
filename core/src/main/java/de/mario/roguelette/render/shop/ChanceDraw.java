package de.mario.roguelette.render.shop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.render.Renderable;

public class ChanceDraw implements Renderable {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final Rectangle bounds;
    private final ChanceShopItem item;

    private float thickness;

    public ChanceDraw(final ChanceShopItem item, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
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
}
