package de.mario.roguelette.render.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.util.ColorHelper;

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
        Color borderColor = item.getRenderInfo().getBorderColor();
        Color fillColor = item.getRenderInfo().getBackgrundColor();

        // Outer pentagon geometry (the "house": rectangular body + triangular roof)
        float bx = baseBounds.x;
        float rx = baseBounds.x + baseBounds.width;
        float by = baseBounds.y;
        float eavesY = baseBounds.y + baseBounds.height;          // where roof meets body
        float apexY = baseBounds.y + baseBounds.height * 3f / 2f; // roof tip
        float apexX = baseBounds.x + baseBounds.width / 2f;

        // Inner pentagon: the outer shape offset inward by `thickness` along every edge,
        // so the border has a uniform width all the way around (roof included).
        float bxIn = bx + thickness;
        float rxIn = rx - thickness;
        float byIn = by + thickness;
        // Offset the roof edges inward by `thickness` and intersect to find the inner eaves/apex.
        float dx = (rx - bx) / 2f;       // roof run (half the body width)
        float dy = apexY - eavesY;       // roof rise
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        float eavesYIn = (eavesY - thickness * dx / len) + dy * (thickness * (1f - dy / len) / dx);
        float apexYIn = (eavesY - thickness * dx / len) + dy * ((dx - thickness * dy / len) / dx);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Drop shadow (whole pentagon, offset)
        float shadowOffset = 4f;
        Color shadow = new Color(0, 0, 0, 0.4f);
        fillPentagon(bx + shadowOffset, rx + shadowOffset, by - shadowOffset,
            eavesY - shadowOffset, apexY - shadowOffset, shadow, shadow);

        // Border: single continuous gradient over the entire pentagon (no roof/body seam)
        Color borderDark = ColorHelper.darken(borderColor, 0.2f);
        Color borderLight = ColorHelper.lighten(borderColor, 0.1f);
        fillPentagon(bx, rx, by, eavesY, apexY, borderLight, borderDark);

        // Fill: inner pentagon, also a single continuous gradient
        Color fillDark = ColorHelper.darken(fillColor, 0.15f);
        Color fillLight = ColorHelper.lighten(fillColor, 0.1f);
        fillPentagon(bxIn, rxIn, byIn, eavesYIn, apexYIn, fillLight, fillDark);

        Gdx.gl.glDisable(GL20.GL_BLEND);

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

    /**
     * Fills a "house" pentagon (rectangular body with a triangular roof) using a single
     * vertical gradient from <code>topColor</code> (apex) to <code>bottomColor</code> (base).
     * Drawing it as one set of horizontal strips avoids any visible seam between roof and body.
     */
    private void fillPentagon(float bx, float rx, float by, float eavesY, float apexY, Color topColor, Color bottomColor) {
        float apexX = (bx + rx) / 2f;
        float totalHeight = apexY - by;
        if (totalHeight <= 0) {
            return;
        }
        int strips = Math.max(2, (int) Math.ceil(totalHeight / 4f));
        float stripHeight = totalHeight / strips;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < strips; i++) {
            float y0 = by + i * stripHeight;
            float y1 = (i == strips - 1) ? apexY : y0 + stripHeight;

            float t = (y0 + stripHeight / 2f - by) / totalHeight; // 0 at base, 1 at apex
            shapeRenderer.setColor(
                lerp(bottomColor.r, topColor.r, t),
                lerp(bottomColor.g, topColor.g, t),
                lerp(bottomColor.b, topColor.b, t),
                lerp(bottomColor.a, topColor.a, t)
            );

            float l0 = leftAt(y0, bx, apexX, eavesY, apexY);
            float r0 = rightAt(y0, rx, apexX, eavesY, apexY);
            float l1 = leftAt(y1, bx, apexX, eavesY, apexY);
            float r1 = rightAt(y1, rx, apexX, eavesY, apexY);

            // strip as a trapezoid (two triangles)
            shapeRenderer.triangle(l0, y0, r0, y0, r1, y1);
            shapeRenderer.triangle(l0, y0, r1, y1, l1, y1);
        }
        shapeRenderer.end();
    }

    private float leftAt(float y, float bx, float apexX, float eavesY, float apexY) {
        if (y <= eavesY) {
            return bx;
        }
        float f = (y - eavesY) / (apexY - eavesY);
        return lerp(bx, apexX, f);
    }

    private float rightAt(float y, float rx, float apexX, float eavesY, float apexY) {
        if (y <= eavesY) {
            return rx;
        }
        float f = (y - eavesY) / (apexY - eavesY);
        return lerp(rx, apexX, f);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
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
