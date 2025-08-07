package de.mario.roguelette.render.segment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class SegmentDrawBase {
    protected final ShapeRenderer shapeRenderer;
    protected final SpriteBatch batch;
    protected final BitmapFont font;

    protected final SegmentShapeRenderer segmentShapeRenderer;

    protected SegmentDrawBase(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, float centerX, float centerY, float outerRadius, float innerRadius) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        this.segmentShapeRenderer = new SegmentShapeRenderer(shapeRenderer, centerX, centerY, outerRadius, innerRadius);
    }

    public abstract void render();

    public void renderOutline(final Color color) {
        segmentShapeRenderer.renderOutline(color);
    }

    public float getCenterX() {
        return segmentShapeRenderer.getCenterX();
    }

    public void setCenterX(float centerX) {
        segmentShapeRenderer.setCenterX(centerX);
    }

    public float getCenterY() {
        return segmentShapeRenderer.getCenterY();
    }

    public void setCenterY(float centerY) {
        segmentShapeRenderer.setCenterY(centerY);
    }

    public float getStartAngle() {
        return segmentShapeRenderer.getStartAngle();
    }

    public void setStartAngle(float startAngle) {
        segmentShapeRenderer.setStartAngle(startAngle);
    }

    public float getSweepAngle() {
        return segmentShapeRenderer.getSweepAngle();
    }

    public void setSweepAngle(float sweepAngle) {
        segmentShapeRenderer.setSweepAngle(sweepAngle);
    }

    public Color getColor() {
        return segmentShapeRenderer.getColor();
    }

    public void setColor(Color color) {
        segmentShapeRenderer.setColor(color);
    }

    public float getEndAngle() {
        return segmentShapeRenderer.getEndAngle();
    }

    public float getOuterRadius() {
        return segmentShapeRenderer.getOuterRadius();
    }

    public void setOuterRadius(float outerRadius) {
        segmentShapeRenderer.setOuterRadius(outerRadius);
    }

    public float getInnerRadius() {
        return segmentShapeRenderer.getInnerRadius();
    }

    public void setInnerRadius(float innerRadius) {
        segmentShapeRenderer.setInnerRadius(innerRadius);
    }

    public float getRotation() {
        return segmentShapeRenderer.getRotation();
    }

    public void setRotation(float rotation) {
        segmentShapeRenderer.setRotation(rotation);
    }

    public boolean angleInArc(float angle) {
        return segmentShapeRenderer.angleInArc(angle);
    }

    public boolean contains(float x, float y) {
        return segmentShapeRenderer.contains(x, y);
    }
}
