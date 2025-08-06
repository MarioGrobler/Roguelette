package de.mario.roguelette.render.wheel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.animator.BallAnimator;
import de.mario.roguelette.animator.WheelAnimator;
import de.mario.roguelette.render.SegmentDraw;
import de.mario.roguelette.util.MathHelper;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WheelRenderer {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final GameState gameState;

    private float anglePerSegment;

    private float centerX;
    private float centerY;
    private float wheelRadius;
    private float innerRadius;
    private float outerRadius;
    private float ballRadius = 10f;

    private final WheelAnimator wheelAnimator;
    private final BallAnimator ballAnimator;
    private final List<SegmentDraw> segmentDraws = new ArrayList<>();


    private SegmentDraw createSegmentDraw(final Segment segment, float startAngle) {
        SegmentDraw sd = new SegmentDraw(segment, shapeRenderer, batch, font, centerX, centerY, wheelRadius, 0f);
        sd.setStartAngle(startAngle);
        sd.setSweepAngle(anglePerSegment);
        return sd;
    }

    private void updateAnglePerSegment() {
        anglePerSegment = 360f / gameState.getWheel().size();
        segmentDraws.clear();
        for (int i = 0; i < gameState.getWheel().size(); i++) {
            float start = i * anglePerSegment;
            //TODO calling createSegmentDraw here might be unnecessarily expensive
            segmentDraws.add(createSegmentDraw(gameState.getWheel().getSegmentAt(i), start));
        }
    }

    public WheelRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, float wheelRadius, float innerRadius, float outerRadius, float centerX, float centerY) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.gameState = gameState;

        this.centerX = centerX;
        this.centerY = centerY;
        this.wheelRadius = wheelRadius;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;

        this.wheelAnimator = new WheelAnimator();
        this.ballAnimator = new BallAnimator(wheelRadius-2*ballRadius);

        updateAnglePerSegment();
    }

    public void updateWheel() {
        updateAnglePerSegment();
    }

    /**
     * @param angle the angle in degree (0 - 360)
     */
    public Segment getCurrentSegment(float angle) {
        float actualAngle = MathHelper.normalizeAngle(angle - wheelAnimator.getRotationAngle());
        for (SegmentDraw sd : segmentDraws) {
            if (actualAngle >= sd.getStartAngle() && actualAngle < sd.getEndAngle()) {
                return sd.getSegment();
            }
        }
        //360°
        return segmentDraws.get(0).getSegment();
    }

    public float getCurrentSegmentStartAngle(float angle) {
        float actualAngle = MathHelper.normalizeAngle(angle - wheelAnimator.getRotationAngle());
        for (SegmentDraw sd : segmentDraws) {
            if (actualAngle >= sd.getStartAngle() && actualAngle < sd.getEndAngle()) {
                return sd.getStartAngle();
            }
        }
        //360°
        return segmentDraws.get(0).getStartAngle();
    }

    public void render() {
        int segmentCount = gameState.getWheel().size();
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
        for (SegmentDraw sd : segmentDraws) {
            sd.setRotation(rotationAngle);
            sd.render();
        }

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

    public Optional<Segment> getSegmentAt(float x, float y) {
        Circle wheelCircle = new Circle(centerX, centerY, wheelRadius);
        Circle innerCircle = new Circle(centerX, centerY, innerRadius);
        if (wheelCircle.contains(x, y) && !innerCircle.contains(x, y)) {
            float angle = MathUtils.atan2Deg360(y - centerY, x - centerX);
            return Optional.of(getCurrentSegment(angle));
        }
        return Optional.empty();
    }

}
