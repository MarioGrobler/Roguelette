package de.mario.roguelette.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.util.ColorHelper;

public class RoundedRectRenderer implements Renderable {

    private final ShapeRenderer shapeRenderer;
    final Rectangle bounds;

    private float radius = 10f;
    private float thickness = 10f;
    private Color borderColor;
    private Color fillColor;

    public RoundedRectRenderer(final ShapeRenderer shapeRenderer, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.bounds = bounds;

        this.fillColor = new Color(0.5f, 0.35f, 0.2f, 1);
        this.borderColor = ColorHelper.darken(new Color(0.5f, 0.35f, 0.2f, 1));
    }

    private void drawRoundedRect(final Rectangle rect) {
        // middle
        shapeRenderer.rect(rect.x + radius, rect.y + radius, rect.width - 2 * radius, rect.height - 2 * radius);

        // sides
        shapeRenderer.rect(rect.x + radius, rect.y, rect.width - 2 * radius, radius); // down
        shapeRenderer.rect(rect.x + radius, rect.y + rect.height - radius, rect.width - 2 * radius, radius); // up
        shapeRenderer.rect(rect.x, rect.y + radius, radius, rect.height - 2 * radius); // left
        shapeRenderer.rect(rect.x + rect.width - radius, rect.y + radius, radius, rect.height - 2 * radius); // right

        // rounded corners
        shapeRenderer.circle(rect.x + radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + radius, rect.y + rect.height - radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + rect.height - radius, radius);
    }

    @Override
    public void render() {
        // outer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(borderColor);
        drawRoundedRect(bounds);

        // inner
        shapeRenderer.setColor(fillColor);
        Rectangle inner = new Rectangle(bounds.x + thickness, bounds.y + thickness, bounds.width - 2*thickness, bounds.height - 2*thickness);
        drawRoundedRect(inner);

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
    }
}
