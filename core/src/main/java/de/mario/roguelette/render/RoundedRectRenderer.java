package de.mario.roguelette.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.util.ColorHelper;

public class RoundedRectRenderer implements Renderable {

    private final ShapeRenderer shapeRenderer;
    final Rectangle bounds;

    private float radius = 10f;
    private float thickness = 6f;

    private Color borderColor;
    private Color fillColor;
    private Color fillColorTop;
    private Color fillColorBottom;
    private Color highlightFillColor;
    private Color highlightFillColorTop;
    private Color highlightFillColorBottom;
    private Color innerHighlightColor;

    // Shadow settings
    private boolean shadowEnabled = true;
    private float shadowOffsetX = 4f;
    private float shadowOffsetY = -4f;
    private Color shadowColor = new Color(0, 0, 0, 0.4f);

    private boolean highlight = false;

    public RoundedRectRenderer(final ShapeRenderer shapeRenderer, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.bounds = bounds;

        // More refined color palette
        this.fillColor = new Color(0.35f, 0.25f, 0.18f, 1f);
        this.fillColorTop = ColorHelper.lighten(fillColor, 0.15f);
        this.fillColorBottom = ColorHelper.darken(fillColor, 0.1f);

        this.borderColor = ColorHelper.darken(fillColor, 0.3f);

        this.highlightFillColor = ColorHelper.lighten(fillColor, 0.1f);
        this.highlightFillColorTop = ColorHelper.lighten(highlightFillColor, 0.15f);
        this.highlightFillColorBottom = ColorHelper.darken(highlightFillColor, 0.1f);

        this.innerHighlightColor = new Color(1f, 1f, 1f, 0.15f);
    }

    private void drawRoundedRect(final Rectangle rect, float r) {
        // middle
        shapeRenderer.rect(rect.x + r, rect.y + r, rect.width - 2 * r, rect.height - 2 * r);

        // sides
        shapeRenderer.rect(rect.x + r, rect.y, rect.width - 2 * r, r); // down
        shapeRenderer.rect(rect.x + r, rect.y + rect.height - r, rect.width - 2 * r, r); // up
        shapeRenderer.rect(rect.x, rect.y + r, r, rect.height - 2 * r); // left
        shapeRenderer.rect(rect.x + rect.width - r, rect.y + r, r, rect.height - 2 * r); // right

        // rounded corners
        shapeRenderer.circle(rect.x + r, rect.y + r, r);
        shapeRenderer.circle(rect.x + rect.width - r, rect.y + r, r);
        shapeRenderer.circle(rect.x + r, rect.y + rect.height - r, r);
        shapeRenderer.circle(rect.x + rect.width - r, rect.y + rect.height - r, r);
    }

    private void drawRoundedRectGradient(final Rectangle rect, float r, Color topColor, Color bottomColor) {
        // Use fixed strip height for consistent smoothness regardless of rectangle size
        float targetStripHeight = 4f;
        int strips = Math.max(2, (int) Math.ceil(rect.height / targetStripHeight));
        float stripHeight = rect.height / strips;

        for (int i = 0; i < strips; i++) {
            // Interpolate color based on position (0 = top, 1 = bottom)
            float t = (float) i / (strips - 1);
            Color stripColor = new Color(
                lerp(topColor.r, bottomColor.r, t),
                lerp(topColor.g, bottomColor.g, t),
                lerp(topColor.b, bottomColor.b, t),
                1f
            );
            shapeRenderer.setColor(stripColor);

            float y = rect.y + rect.height - (i + 1) * stripHeight;
            float h = stripHeight + 0.5f; // slight overlap to avoid gaps

            // Adjust for rounded corners at top and bottom
            if (i == 0) {
                // Top strip - needs rounded top corners
                shapeRenderer.rect(rect.x + r, y, rect.width - 2 * r, h);
                shapeRenderer.rect(rect.x, y, r, h - r);
                shapeRenderer.rect(rect.x + rect.width - r, y, r, h - r);
                shapeRenderer.circle(rect.x + r, rect.y + rect.height - r, r);
                shapeRenderer.circle(rect.x + rect.width - r, rect.y + rect.height - r, r);
            } else if (i == strips - 1) {
                // Bottom strip - needs rounded bottom corners
                shapeRenderer.rect(rect.x + r, y, rect.width - 2 * r, h);
                shapeRenderer.rect(rect.x, y + r, r, h - r);
                shapeRenderer.rect(rect.x + rect.width - r, y + r, r, h - r);
                shapeRenderer.circle(rect.x + r, rect.y + r, r);
                shapeRenderer.circle(rect.x + rect.width - r, rect.y + r, r);
            } else {
                // Middle strips - full width
                shapeRenderer.rect(rect.x, y, rect.width, h);
            }
        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    public void render() {
        // Enable blending for shadow transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw shadow first (behind everything)
        if (shadowEnabled) {
            shapeRenderer.setColor(shadowColor);
            Rectangle shadowBounds = new Rectangle(
                bounds.x + shadowOffsetX,
                bounds.y + shadowOffsetY,
                bounds.width,
                bounds.height
            );
            drawRoundedRect(shadowBounds, radius);
        }

        // Draw border
        shapeRenderer.setColor(borderColor);
        drawRoundedRect(bounds, radius);

        // Draw gradient fill
        Rectangle inner = new Rectangle(
            bounds.x + thickness,
            bounds.y + thickness,
            bounds.width - 2 * thickness,
            bounds.height - 2 * thickness
        );

        if (highlight) {
            drawRoundedRectGradient(inner, radius - thickness/2, highlightFillColorTop, highlightFillColorBottom);
        } else {
            drawRoundedRectGradient(inner, radius - thickness/2, fillColorTop, fillColorBottom);
        }

        // Draw inner highlight (soft sheen fading down from the top edge)
        float highlightHeight = Math.min(10f, inner.height / 4f);
        int highlightStrips = Math.max(2, (int) Math.ceil(highlightHeight));
        float stripH = highlightHeight / highlightStrips;
        for (int i = 0; i < highlightStrips; i++) {
            float t = (float) i / highlightStrips; // 0 at top -> fades out going down
            shapeRenderer.setColor(
                innerHighlightColor.r,
                innerHighlightColor.g,
                innerHighlightColor.b,
                innerHighlightColor.a * (1f - t)
            );
            float y = inner.y + inner.height - (i + 1) * stripH;
            shapeRenderer.rect(inner.x + radius, y, inner.width - 2 * radius, stripH + 0.5f);
        }

        shapeRenderer.end();
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
        this.fillColorTop = ColorHelper.lighten(fillColor, 0.15f);
        this.fillColorBottom = ColorHelper.darken(fillColor, 0.1f);
    }

    public Color getHighlightFillColor() {
        return highlightFillColor;
    }

    public void setHighlightFillColor(Color highlightFillColor) {
        this.highlightFillColor = highlightFillColor;
        this.highlightFillColorTop = ColorHelper.lighten(highlightFillColor, 0.15f);
        this.highlightFillColorBottom = ColorHelper.darken(highlightFillColor, 0.1f);
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public void setShadowEnabled(boolean enabled) {
        this.shadowEnabled = enabled;
    }

    public void setShadowOffset(float x, float y) {
        this.shadowOffsetX = x;
        this.shadowOffsetY = y;
    }

    public void handleHover(float x, float y) {
        highlight = contains(x, y);
    }
}
