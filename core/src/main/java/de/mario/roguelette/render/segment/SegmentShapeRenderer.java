package de.mario.roguelette.render.segment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.util.ColorHelper;
import de.mario.roguelette.util.MathHelper;

public class SegmentShapeRenderer implements Renderable {
    private final ShapeRenderer shapeRenderer;

    float centerX;
    float centerY;
    float startAngle;
    float sweepAngle;
    float outerRadius;
    float innerRadius;
    float rotation;
    Color color;
    Color secondaryColor = null; // For split segments (BOTH)
    Color outlineColor = null;
    boolean gradientEnabled = true;

    public SegmentShapeRenderer(final ShapeRenderer shapeRenderer, float centerX, float centerY, float outerRadius, float innerRadius) {
        this.shapeRenderer = shapeRenderer;

        this.centerX = centerX;
        this.centerY = centerY;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
    }

    private Array<Vector2> prepareDonutSlice() {
        int segments = Math.max(6, (int)(sweepAngle / 4f)); // smoothness

        float angleStep = sweepAngle / segments;

        // outer arc
        Array<Vector2> outerPoints = new Array<>();
        for (int i = 0; i <= segments; i++) {
            float angle = startAngle + i * angleStep + rotation;
            outerPoints.add(new Vector2(
                centerX + MathUtils.cosDeg(angle) * outerRadius,
                centerY + MathUtils.sinDeg(angle) * outerRadius
            ));
        }

        // inner arc
        Array<Vector2> innerPoints = new Array<>();
        for (int i = segments; i >= 0; i--) {
            float angle = startAngle + i * angleStep + rotation;
            innerPoints.add(new Vector2(
                centerX + MathUtils.cosDeg(angle) * innerRadius,
                centerY + MathUtils.sinDeg(angle) * innerRadius
            ));
        }

        // make polygon
        outerPoints.addAll(innerPoints);
        return outerPoints;
    }

    // necessary as shapeRenderer.polygon is not compatible with ShapeType.Filled
    private void drawPolygon(final Array<Vector2> points) {
        Vector2 first = points.get(0);
        for (int i = 1; i < points.size - 1; i++) {
            Vector2 p1 = points.get(i);
            Vector2 p2 = points.get(i + 1);
            shapeRenderer.triangle(first.x, first.y, p1.x, p1.y, p2.x, p2.y);
        }
    }

    @Override
    public void render() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (secondaryColor != null) {
            // Split segment: inner half secondary color, outer half primary color
            renderSplitSegment();
        } else if (gradientEnabled) {
            // Gradient from inner (darker) to outer (lighter)
            renderGradientSegment();
        } else {
            // Solid color
            shapeRenderer.setColor(color);
            final Array<Vector2> points = prepareDonutSlice();
            drawPolygon(points);
        }

        shapeRenderer.end();

        if (outlineColor != null) {
            renderOutline();
        }
    }

    private void renderGradientSegment() {
        // Use fixed layer thickness for consistent smoothness
        float targetLayerThickness = 4f;
        float totalThickness = outerRadius - innerRadius;
        int layers = Math.max(2, (int) Math.ceil(totalThickness / targetLayerThickness));
        float radiusStep = totalThickness / layers;

        // Stronger gradient - more contrast between inner and outer
        Color innerColor = ColorHelper.darken(color, 0.4f);
        Color outerColor = ColorHelper.lighten(color, 0.2f);

        for (int i = 0; i < layers; i++) {
            float t = (layers == 1) ? 0.5f : (float) i / (layers - 1);
            Color layerColor = new Color(
                lerp(innerColor.r, outerColor.r, t),
                lerp(innerColor.g, outerColor.g, t),
                lerp(innerColor.b, outerColor.b, t),
                1f
            );
            shapeRenderer.setColor(layerColor);

            float layerInner = innerRadius + i * radiusStep;
            float layerOuter = innerRadius + (i + 1) * radiusStep + 0.5f; // slight overlap

            Array<Vector2> points = prepareDonutSliceWithRadii(layerInner, layerOuter);
            drawPolygon(points);
        }
    }

    private void renderSplitSegment() {
        float midRadius = innerRadius + (outerRadius - innerRadius) * 0.5f;

        // Inner half (secondary color - e.g., black)
        Color innerColorDark = ColorHelper.darken(secondaryColor, 0.2f);
        Color innerColorLight = secondaryColor;

        int layers = 2;
        float radiusStep = (midRadius - innerRadius) / layers;
        for (int i = 0; i < layers; i++) {
            float t = (float) i / (layers - 1);
            Color layerColor = new Color(
                lerp(innerColorDark.r, innerColorLight.r, t),
                lerp(innerColorDark.g, innerColorLight.g, t),
                lerp(innerColorDark.b, innerColorLight.b, t),
                1f
            );
            shapeRenderer.setColor(layerColor);

            float layerInner = innerRadius + i * radiusStep;
            float layerOuter = innerRadius + (i + 1) * radiusStep;
            Array<Vector2> points = prepareDonutSliceWithRadii(layerInner, layerOuter);
            drawPolygon(points);
        }

        // Outer half (primary color - e.g., red)
        Color outerColorDark = color;
        Color outerColorLight = ColorHelper.lighten(color, 0.15f);

        radiusStep = (outerRadius - midRadius) / layers;
        for (int i = 0; i < layers; i++) {
            float t = (float) i / (layers - 1);
            Color layerColor = new Color(
                lerp(outerColorDark.r, outerColorLight.r, t),
                lerp(outerColorDark.g, outerColorLight.g, t),
                lerp(outerColorDark.b, outerColorLight.b, t),
                1f
            );
            shapeRenderer.setColor(layerColor);

            float layerInner = midRadius + i * radiusStep;
            float layerOuter = midRadius + (i + 1) * radiusStep;
            Array<Vector2> points = prepareDonutSliceWithRadii(layerInner, layerOuter);
            drawPolygon(points);
        }
    }

    private Array<Vector2> prepareDonutSliceWithRadii(float inner, float outer) {
        int segments = Math.max(6, (int)(sweepAngle / 4f));
        float angleStep = sweepAngle / segments;

        Array<Vector2> outerPoints = new Array<>();
        for (int i = 0; i <= segments; i++) {
            float angle = startAngle + i * angleStep + rotation;
            outerPoints.add(new Vector2(
                centerX + MathUtils.cosDeg(angle) * outer,
                centerY + MathUtils.sinDeg(angle) * outer
            ));
        }

        Array<Vector2> innerPoints = new Array<>();
        for (int i = segments; i >= 0; i--) {
            float angle = startAngle + i * angleStep + rotation;
            innerPoints.add(new Vector2(
                centerX + MathUtils.cosDeg(angle) * inner,
                centerY + MathUtils.sinDeg(angle) * inner
            ));
        }

        outerPoints.addAll(innerPoints);
        return outerPoints;
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private float[] pointsToArray(final Array<Vector2> points) {
        float[] result = new float[points.size * 2];
        for (int i = 0; i < points.size; i++) {
            result[i * 2] = points.get(i).x;
            result[i * 2 + 1] = points.get(i).y;
        }
        return result;
    }

    public void renderOutline() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(outlineColor);
        final Array<Vector2> points = prepareDonutSlice();
        shapeRenderer.polygon(pointsToArray(points));
        shapeRenderer.end();
    }

    public boolean angleInArc(float angle) {
        float norm = MathHelper.normalizeAngle(angle - startAngle - rotation);
        return norm <= sweepAngle;
    }

    @Override
    public boolean contains(float x, float y) {
        Circle outerCircle = new Circle(centerX, centerY, outerRadius);
        Circle innerCircle = new Circle(centerX, centerY, innerRadius);
        if (outerCircle.contains(x, y) && !innerCircle.contains(x, y)) {
            float angle = MathUtils.atan2Deg360(y - centerY, x - centerX);
            return angleInArc(angle);
        }
        return false;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public float getOuterRadius() {
        return outerRadius;
    }

    public void setOuterRadius(float outerRadius) {
        this.outerRadius = outerRadius;
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = innerRadius;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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

    public float getEndAngle() {
        return startAngle + sweepAngle;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(Color secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public boolean isGradientEnabled() {
        return gradientEnabled;
    }

    public void setGradientEnabled(boolean gradientEnabled) {
        this.gradientEnabled = gradientEnabled;
    }
}
