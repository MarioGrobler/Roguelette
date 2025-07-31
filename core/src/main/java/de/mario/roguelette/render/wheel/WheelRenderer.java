package de.mario.roguelette.render.wheel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.animator.BallAnimator;
import de.mario.roguelette.animator.WheelAnimator;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.Wheel;

import java.util.ArrayList;
import java.util.List;

public class WheelRenderer {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private float anglePerSegment;

    private float centerX;
    private float centerY;
    private float wheelRadius;
    private float innerRadius;
    private float outerRadius;
    private float ballRadius = 10f;

    private Wheel wheel;
    private final WheelAnimator wheelAnimator;
    private final BallAnimator ballAnimator;
    private final List<SegmentAngle> segmentAngles = new ArrayList<>();


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

    private void drawSegmentArc(final float cx, final float cy, final float radius, final float startDeg, final float sweepDeg) {
        int smoothness = 10; // approximate an arc by actually drawing 10 triangles
        float angleStep = sweepDeg / smoothness;

        float[] verts = new float[(smoothness + 2) * 2];
        verts[0] = cx;
        verts[1] = cy;

        for (int i = 0; i <= smoothness; i++) {
            float angle = startDeg + i * angleStep;
            float rad = MathUtils.degreesToRadians * angle;
            verts[2 * i + 2] = cx + MathUtils.cos(rad) * radius;
            verts[2 * i + 3] = cy + MathUtils.sin(rad) * radius;
        }

        shapeRenderer.triangle(verts[0], verts[1], verts[2], verts[3], verts[4], verts[5]);
        for (int i = 1; i < smoothness; i++) {
            shapeRenderer.triangle(
                cx, cy,
                verts[2 * i + 2], verts[2 * i + 3],
                verts[2 * i + 4], verts[2 * i + 5]
            );
        }
    }

    private void updateAnglePerSegment() {
        anglePerSegment = 360f / wheel.size();
        segmentAngles.clear();
        for (int i = 0; i < wheel.size(); i++) {
            float start = i * anglePerSegment;
            float end = start + anglePerSegment;
            segmentAngles.add(new SegmentAngle(wheel.getSegmentAt(i), start, end));
        }
    }

    public WheelRenderer(final Wheel wheel, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        this(wheel, shapeRenderer, batch, font, 500, 300, 600, 650, 650);
    }

    public WheelRenderer(final Wheel wheel, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, float wheelRadius, float innerRadius, float outerRadius, float centerX, float centerY) {
        this.wheel = wheel;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        this.centerX = centerX;
        this.centerY = centerY;
        this.wheelRadius = wheelRadius;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;

        this.wheelAnimator = new WheelAnimator();
        this.ballAnimator = new BallAnimator(wheelRadius-2*ballRadius);

        updateAnglePerSegment();
    }

    public void updateWheel(final Wheel newWheel) {
        this.wheel = newWheel;
        updateAnglePerSegment();
    }

    /**
     * @param angle the angle in degree (0 - 360)
     */
    public Segment getCurrentSegment(float angle) {
        return getCurrentSegmentAngle(angle).getSegment();
    }

    public SegmentAngle getCurrentSegmentAngle(float angle) {
        float actualAngle = wheelAnimator.normalizeAngle(angle - wheelAnimator.getRotationAngle());
        for  (SegmentAngle segmentAngle : segmentAngles) {
            if (actualAngle >= segmentAngle.getStartAngle() && actualAngle < segmentAngle.getEndAngle()) {
                return segmentAngle;
            }
        }
        //360°
        return segmentAngles.get(0);
    }

    public void render() {
        int segmentCount = wheel.size();
        float rotationAngle = wheelAnimator.getRotationAngle();

        //render outer ring
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BROWN);
        shapeRenderer.circle(centerX, centerY, outerRadius);
        shapeRenderer.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 20; i++) {
            float r = outerRadius - i * (outerRadius - wheelRadius) / 20;
            float alpha = 0.05f + 0.05f * (1f - i / 20f);
            shapeRenderer.setColor(new Color(0,0,0, alpha));
            shapeRenderer.circle(centerX, centerY, r);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // render segments
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (SegmentAngle segmentAngle : segmentAngles) {
            final Segment segment = segmentAngle.getSegment();
            shapeRenderer.setColor(getDrawingColorFromSegment(segment));
            shapeRenderer.arc(centerX, centerY, wheelRadius, segmentAngle.getStartAngle() + rotationAngle, anglePerSegment, 10);
        }
        shapeRenderer.end();

        // render lines between segments
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < segmentCount; i++) {
            float angle = anglePerSegment * i + rotationAngle;
            float rad = MathUtils.degreesToRadians * angle;
            float x = centerX + MathUtils.cos(rad) * wheelRadius;
            float y = centerY + MathUtils.sin(rad) * wheelRadius;

            shapeRenderer.setColor(Color.GOLDENROD);
            shapeRenderer.rectLine(centerX, centerY, x, y, 2);
        }
        shapeRenderer.end();


        // render center
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GOLDENROD);
        shapeRenderer.circle(centerX, centerY, innerRadius);
        shapeRenderer.end();

        // draw texts
        batch.begin();
        font.getData().setScale(1.5f);
        for(int i = 0; i < segmentCount; i++) {
            Segment segment = wheel.getSegmentAt(i);
            float angle = (i + .5f) * anglePerSegment + rotationAngle;

            float textRadius = wheelRadius * .9f;

            float x = centerX + MathUtils.cosDeg(angle) * textRadius;
            float y = centerY + MathUtils.sinDeg(angle) * textRadius;

            String text = segment.getDisplayText();

            // Rotate the text with the wheel
            GlyphLayout layout = new GlyphLayout(font, text, Color.WHITE, 0, Align.right, false);
            float originX = layout.width / 2;
            float originY = layout.height / 2;

            batch.setTransformMatrix(batch.getTransformMatrix().idt()
                .translate(x,y,0)
                .rotate(0,0,1, angle)
                .translate(originX, originY,0)
            );

            font.draw(batch, layout, 0, 0);
        }
        batch.end();

        // draw ball
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(ballAnimator.getX(centerX), ballAnimator.getY(centerY), ballRadius);
        shapeRenderer.end();

        // draw current segment
        batch.begin();
        batch.setTransformMatrix(batch.getTransformMatrix().idt());
        font.setColor(Color.WHITE);
        font.draw(batch, "Current number: " + getCurrentSegment(ballAnimator.getRotationAngle()).getDisplayText(), 50, 50);
        batch.end();



        // Cone of light
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(1, 1, 1, 0.1f);
//        shapeRenderer.arc(centerX, centerY, radius+40, 120, 60);
//        shapeRenderer.end();
//        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void update(float delta) {
        wheelAnimator.update(delta);
        ballAnimator.update(delta);
    }

    public void infiniteWheelSpin(float speed) {
        wheelAnimator.startInfiniteSpin(speed);
    }

    public void spinWheelToTarget(float targetAngle) {
        wheelAnimator.startInfiniteSpin(540);
        wheelAnimator.stopAtTarget(targetAngle, 120);
    }

    public void infiniteBallSpin(float speed) {
        infiniteBallSpin(speed, outerRadius);
    }

    public void infiniteBallSpin(float speed, float radius) {
        ballAnimator.startInfiniteSpin(speed, radius);
    }

    public void stopBallWithDeceleration(float deceleration) {
        ballAnimator.stopWithDeceleration(deceleration);
    }

    public void spinBallToTarget(float speed, float targetAngle) {
        spinBallToTarget(speed, targetAngle, outerRadius);
    }

    public void spinBallToTarget(float speed, float targetAngle, float radius) {
        ballAnimator.startInfiniteSpin(speed, radius);
        ballAnimator.stopAtTarget(targetAngle, 180f);
    }

    public float getCurrentRotation() {
        return wheelAnimator.getRotationAngle();
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getBallRadius() {
        return ballRadius;
    }

    public void setBallRadius(float ballRadius) {
        this.ballRadius = ballRadius;
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

    public float getWheelRadius() {
        return wheelRadius;
    }

    public void setWheelRadius(float wheelRadius) {
        this.wheelRadius = wheelRadius;
    }

    public void setRadii(float wheelRadius, float innerRadius, float outerRadius) {
        this.wheelRadius = wheelRadius;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;

        ballAnimator.setRadius(wheelRadius);
        ballAnimator.setMinRadius(wheelRadius-2*ballRadius);
    }

    public void setCenter(float centerX, float centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public boolean isSpinning() {
        return wheelAnimator.isSpinning();
    }

    public void setWheelListener(final WheelAnimator.Listener wheelListener) {
        wheelAnimator.setListener(wheelListener);
    }

    public void setBallListener(final WheelAnimator.Listener listener) {
        ballAnimator.setListener(listener);
    }

    public boolean contains(float x, float y) {
        return new Circle(centerX, centerY, outerRadius).contains(x, y);
    }

}
