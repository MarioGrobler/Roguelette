package de.mario.roguelette.render.segment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import de.mario.roguelette.util.MathHelper;

public class SegmentShapeRenderer {
    private final ShapeRenderer shapeRenderer;

    float centerX;
    float centerY;
    float startAngle;
    float sweepAngle;
    float outerRadius;
    float innerRadius;
    float rotation;
    Color color;

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

    public void render() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        final Array<Vector2> points = prepareDonutSlice();
        drawPolygon(points);
        shapeRenderer.end();
    }

    private float[] pointsToArray(final Array<Vector2> points) {
        float[] result = new float[points.size * 2];
        for (int i = 0; i < points.size; i++) {
            result[i * 2] = points.get(i).x;
            result[i * 2 + 1] = points.get(i).y;
        }
        return result;
    }

    public void renderOutline(final Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        final Array<Vector2> points = prepareDonutSlice();
        shapeRenderer.polygon(pointsToArray(points));
        shapeRenderer.end();
    }

    public boolean angleInArc(float angle) {
        float norm = MathHelper.normalizeAngle(angle - startAngle - rotation);
        return norm <= sweepAngle;
    }

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

    public float getEndAngle() {
        return startAngle + sweepAngle;
    }
}
