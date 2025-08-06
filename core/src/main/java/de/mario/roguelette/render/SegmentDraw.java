package de.mario.roguelette.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import de.mario.roguelette.util.MathHelper;
import de.mario.roguelette.wheel.Segment;

import java.text.DecimalFormat;

public class SegmentDraw {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Segment segment;

    private final DecimalFormat df = new DecimalFormat("0.#");

    float centerX;
    float centerY;
    float startAngle;
    float sweepAngle;
    float outerRadius;
    float innerRadius;
    float rotation;
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
                return new Color(0.4f, 0f, 0f, 1);
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

    private void drawDonutSlice() {
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

    public void render() {
        // draw slice
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        drawDonutSlice();
        shapeRenderer.end();

        // draw texts
        batch.begin();
        font.getData().setScale(1.5f);

        float cAngle = startAngle + 0.5f * sweepAngle + rotation;
        float textRadius = outerRadius * .95f;

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
            .translate(0, originY,0)
        );

        font.draw(batch, layout, 0, 0);

        // draw multiplier if it is not the identity
        if (segment.getMultiplier() != 1f) {
            // add a golden shimmer (maybe later)
//            Gdx.gl.glEnable(GL20.GL_BLEND);
//            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.setColor(1, 1, 0, 0.3f);
//            shapeRenderer.arc(centerX, centerY, outerRadius, startAngle + rotation, sweepAngle);
//            shapeRenderer.end();
//            Gdx.gl.glDisable(GL20.GL_BLEND);


            float mulRadius = innerRadius + (outerRadius - innerRadius) * 0.2f;
            float mulX = centerX + MathUtils.cosDeg(cAngle) * mulRadius;
            float mulY = centerY + MathUtils.sinDeg(cAngle) * mulRadius;
            font.getData().setScale(1.2f);
            GlyphLayout mulLayout = new GlyphLayout(font, df.format(segment.getMultiplier()) + "x", Color.BLACK, 0, Align.left, false);

            float padding = 6f;

            Matrix4 matrix = new Matrix4().idt()
                .translate(mulX, mulY, 0)
                .rotate(0, 0, 1, cAngle)
                .translate(0, originY, 0);

            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setTransformMatrix(matrix);
            shapeRenderer.setColor(Color.GOLDENROD);
            shapeRenderer.rect(-padding, -mulLayout.height - padding, mulLayout.width + 2 * padding, mulLayout.height + 2 * padding);
            shapeRenderer.setTransformMatrix(shapeRenderer.getTransformMatrix().idt()); // reset matrix
            shapeRenderer.end();

            batch.begin();
            batch.setTransformMatrix(matrix);
            font.draw(batch, mulLayout, 0, 0);
            batch.setTransformMatrix(batch.getTransformMatrix().idt()); // also reset matrix
        }
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
}
