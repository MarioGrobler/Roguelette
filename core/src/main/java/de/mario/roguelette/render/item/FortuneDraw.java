package de.mario.roguelette.render.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.render.Renderable;

public class FortuneDraw implements Renderable {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle baseBounds; // the shape is a rectangle with a triangle as a "head".
    private final Polygon trueBounds; // The head is computed from the given rectangle, yielding the true bounds

    private final FortuneShopItem item;

    private Color outlineColor = null;
    private float thickness;

    private Polygon computeTrueBounds(final Rectangle baseBounds) {
        float[] verts = new float[10];

        // bottom left
        verts[0] = baseBounds.x;
        verts[1] = baseBounds.y;

        // top left
        verts[2] = baseBounds.x;
        verts[3] = baseBounds.y + baseBounds.height;

        // head
        verts[4] = baseBounds.x + baseBounds.width / 2f;
        verts[5] = baseBounds.y + baseBounds.height * 3f/2;

        // top right
        verts[6] = baseBounds.x + baseBounds.width;
        verts[7] = baseBounds.y + baseBounds.height;

        // bottom right
        verts[8] = baseBounds.x + baseBounds.width;
        verts[9] = baseBounds.y;

        return new Polygon(verts);
    }

    public FortuneDraw(final FortuneShopItem item, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final Rectangle baseBounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.baseBounds = baseBounds;
        this.trueBounds = computeTrueBounds(baseBounds);
        this.item = item;

        this.thickness = baseBounds.width / 10f;
    }


    @Override
    public void render() {
        // draw outer base and hat
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(item.getRenderInfo().getBorderColor());
        shapeRenderer.rect(baseBounds.x, baseBounds.y, baseBounds.width, baseBounds.height);
        shapeRenderer.triangle(baseBounds.x, baseBounds.y + baseBounds.height, trueBounds.getVertices()[4], trueBounds.getVertices()[5], baseBounds.x + baseBounds.width, baseBounds.y + baseBounds.height);
        shapeRenderer.end();

        // draw inner base and hat
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(item.getRenderInfo().getBackgrundColor());
        shapeRenderer.rect(baseBounds.x + thickness,
            baseBounds.y + thickness,
            baseBounds.width - 2*thickness,
            baseBounds.height - thickness);
        shapeRenderer.triangle(baseBounds.x + thickness+3, baseBounds.y + baseBounds.height, trueBounds.getVertices()[4], trueBounds.getVertices()[5] - thickness-3, baseBounds.x + baseBounds.width - thickness-3, baseBounds.y + baseBounds.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(item.getRenderInfo().getBorderColor());
        shapeRenderer.rectLine(baseBounds.x + thickness, baseBounds.y + baseBounds.height, baseBounds.x + baseBounds.width - thickness, baseBounds.y + baseBounds.height, thickness);
        shapeRenderer.end();

        if (outlineColor != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(outlineColor);
            shapeRenderer.polygon(trueBounds.getVertices());
            shapeRenderer.end();
        }

        // icon
        batch.begin();
        batch.draw(item.getRenderInfo().getBackgrund(), baseBounds.x, baseBounds.y+thickness, baseBounds.width, baseBounds.height-thickness);
        batch.end();
    }

    @Override
    public boolean contains(float x, float y) {
        return trueBounds.contains(x, y);
    }

    /**
     * @return the outline color or <code>null</code> if the outline is disabled. Default value is <code>null</code>.
     */
    public Color getOutlineColor() {
        return outlineColor;
    }

    /**
     * Sets the outline color. If the color is <code>null</code>, then the outline is disabled.
     */
    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }
}
