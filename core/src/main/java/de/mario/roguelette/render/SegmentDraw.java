package de.mario.roguelette.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import de.mario.roguelette.wheel.Segment;

public class SegmentDraw {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Segment segment;

    float centerX;
    float centerY;
    float startAngle;
    float sweepAngle;
    float outerRadius;
    float innerRadius;
    Color color;

    private Color getDrawingColorFromSegment(final Segment segment) {
        if (segment == null) {
            return Color.PINK;
        }

        switch(segment.getColor()) {
            case RED:
                return Color.RED;
            case BLACK:
                return Color.BLACK;
            case NONE:
                return Color.FOREST;
            case BOTH: //TODO: a more complex pattern here
                return new Color(128, 0, 0, 1);
            default: // should be unreachable
                return Color.PINK;
        }
    }

    public SegmentDraw(final Segment segment, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, float centerX, float centerY, float outerRadius, float innerRadius) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.segment = segment;
        this.color = getDrawingColorFromSegment(segment);

        this.centerX = centerX;
        this.centerY = centerY;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
    }

    private void drawDonutSlice(float rotationAngle) {
        int segments = Math.max(6, (int)(sweepAngle / 4f)); // smoothness

        float angleStep = sweepAngle / segments;

        // outer arc
        Array<Vector2> outerPoints = new Array<>();
        for (int i = 0; i <= segments; i++) {
            float angle = startAngle + i * angleStep + rotationAngle;
            outerPoints.add(new Vector2(
                centerX + MathUtils.cosDeg(angle) * outerRadius,
                centerY + MathUtils.sinDeg(angle) * outerRadius
            ));
        }

        // inner arc
        Array<Vector2> innerPoints = new Array<>();
        for (int i = segments; i >= 0; i--) {
            float angle = startAngle + i * angleStep + rotationAngle;
            innerPoints.add(new Vector2(
                centerX + MathUtils.cosDeg(angle) * innerRadius,
                centerY + MathUtils.sinDeg(angle) * innerRadius
            ));
        }

        // make polygon
        outerPoints.addAll(innerPoints);
        drawPolygon(outerPoints);
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

    public void render(float rotationAngle) {
        // draw slice
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        drawDonutSlice(rotationAngle);
        shapeRenderer.end();

        // draw texts
        batch.begin();
        font.getData().setScale(1.5f);

        float cAngle = startAngle + 0.5f * sweepAngle + rotationAngle;
        float textRadius = outerRadius * .9f;

        float x = centerX + MathUtils.cosDeg(cAngle) * textRadius;
        float y = centerY + MathUtils.sinDeg(cAngle) * textRadius;

        String text = segment.getDisplayText();

        // Rotate the text with the wheel
        GlyphLayout layout = new GlyphLayout(font, text, Color.WHITE, 0, Align.right, false);
        float originX = layout.width / 2;
        float originY = layout.height / 2;

        batch.setTransformMatrix(batch.getTransformMatrix().idt()
            .translate(x,y,0)
            .rotate(0,0,1, cAngle)
            .translate(originX, originY,0)
        );

        font.draw(batch, layout, 0, 0);
        batch.end();
    }

    public Segment getSegment() {
        return segment;
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
