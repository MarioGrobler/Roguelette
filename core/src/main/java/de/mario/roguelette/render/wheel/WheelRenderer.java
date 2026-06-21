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
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.render.segment.SegmentDraw;
import de.mario.roguelette.util.MathHelper;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WheelRenderer implements Renderable {
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
        SegmentDraw sd = new SegmentDraw(segment, shapeRenderer, batch, font, centerX, centerY, wheelRadius, innerRadius*.99f);
        sd.setStartAngle(startAngle);
        sd.setSweepAngle(anglePerSegment);
        return sd;
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

        updateWheel();
    }

    public void updateWheel() {
        anglePerSegment = 360f / gameState.getWheel().size();
        segmentDraws.clear();
        for (int i = 0; i < gameState.getWheel().size(); i++) {
            float start = i * anglePerSegment;
            //TODO calling createSegmentDraw here might be unnecessarily expensive
            segmentDraws.add(createSegmentDraw(gameState.getWheel().getSegmentAt(i), start));
        }
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

    public int getCurrentSegmentIndex(float angle) {
        float actualAngle = MathHelper.normalizeAngle(angle - wheelAnimator.getRotationAngle());
        for (int i = 0; i < segmentDraws.size(); i++) {
            SegmentDraw sd = segmentDraws.get(i);
            if (actualAngle >= sd.getStartAngle() && actualAngle < sd.getEndAngle()) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns an angle (including the current rotation) that, when passed to
     * {@link #getCurrentSegment(float)} / {@link #getCurrentSegmentIndex(float)}, selects the
     * segment at the given index. Points at the segment center. Used to retarget the spin when a
     * listener overrides the landing.
     */
    public float getSelectAngleForIndex(int index) {
        SegmentDraw sd = segmentDraws.get(index);
        return MathHelper.normalizeAngle(sd.getStartAngle() + anglePerSegment * 0.5f + wheelAnimator.getRotationAngle());
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

    /**
     * Returns the start angle (ignoring rotation) of the given segment in the wheel.
     * If there is no such segment, returns an empty optional.
     * The comparison of the segments is done using <code>equals</code>
     */
    public Optional<Float> findStartAngleForSegment(final Segment segment) {
        for (SegmentDraw sd : segmentDraws) {
            if (sd.getSegment().equals(segment)) {
                // + rotation angle to be rotation independent
                return Optional.of(MathHelper.normalizeAngle(sd.getStartAngle() + wheelAnimator.getRotationAngle()));
            }
        }
        return Optional.empty();
    }

    @Override
    public void render() {
        int segmentCount = gameState.getWheel().size();
        float rotationAngle = wheelAnimator.getRotationAngle();

        // Render outer rim with gradient (darker outside, lighter inside)
        renderOuterRim();

        // Render decorative gold ring at segment edge
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.85f, 0.7f, 0.3f, 1f));
        shapeRenderer.circle(centerX, centerY, wheelRadius + 4);
        shapeRenderer.end();

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

            shapeRenderer.setColor(new Color(0.85f, 0.7f, 0.3f, 1f));
            shapeRenderer.rectLine(centerX, centerY, x, y, 2);
        }
        shapeRenderer.end();

        // Render center hub with gradient
        renderCenterHub();

        // draw ball with slight shadow
        renderBall();


    }

    private void renderOuterRim() {
        // Draw gradient rim from dark (outer) to lighter (inner)
        float rimThickness = outerRadius - wheelRadius - 4; // -4 for gold ring
        float targetLayerThickness = 4f;
        int layers = Math.max(2, (int) Math.ceil(rimThickness / targetLayerThickness));
        float layerThickness = rimThickness / layers;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < layers; i++) {
            float t = (float) i / (layers - 1);
            // Dark brown at outer edge, lighter brown at inner edge
            float r = lerp(0.25f, 0.5f, t);
            float g = lerp(0.12f, 0.3f, t);
            float b = lerp(0.08f, 0.2f, t);
            shapeRenderer.setColor(new Color(r, g, b, 1f));

            float radius = outerRadius - i * layerThickness;
            shapeRenderer.circle(centerX, centerY, radius);
        }
        shapeRenderer.end();

        // Add shadow/depth at inner edge of rim (3D effect)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < 10; i++) {
            float alpha = 0.12f * (1f - i / 10f);
            shapeRenderer.setColor(new Color(0, 0, 0, alpha));
            shapeRenderer.circle(centerX, centerY, wheelRadius + 6 + i * 2);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void renderCenterHub() {
        // The segments reach down to innerRadius*0.99 (see createSegmentDraw). Keep the
        // hub clearly inside that so it never covers the multiplier badges, and frame it
        // with a gold ring that mirrors the decorative ring at the outer segment edge.
        float segInnerRadius = innerRadius * 0.99f;
        float ringWidth = 5f;
        float hubRadius = segInnerRadius - ringWidth;

        // Gold ring border at the inner segment edge
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.85f, 0.7f, 0.3f, 1f));
        shapeRenderer.circle(centerX, centerY, segInnerRadius);
        shapeRenderer.end();

        // Simple gradient hub - darker at edge, lighter at center (like RoundedRects)
        float targetLayerThickness = 4f;
        int layers = Math.max(2, (int) Math.ceil(hubRadius / targetLayerThickness));

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < layers; i++) {
            float t = (float) i / (layers - 1);
            // Warm gold gradient - darker bronze at edge, brighter gold at center
            float r = lerp(0.6f, 0.95f, t);
            float g = lerp(0.4f, 0.7f, t);
            float b = lerp(0.15f, 0.25f, t);
            shapeRenderer.setColor(new Color(r, g, b, 1f));

            float radius = hubRadius * (1f - t * 0.95f) + hubRadius * 0.05f;
            shapeRenderer.circle(centerX, centerY, radius);
        }
        shapeRenderer.end();
    }

    private void renderBall() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float ballX = ballAnimator.getX(centerX);
        float ballY = ballAnimator.getY(centerY);

        // Ball shadow
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.3f));
        shapeRenderer.circle(ballX + 2, ballY - 2, ballRadius);
        shapeRenderer.end();

        // Ball with gradient (white to light gray)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 5; i >= 0; i--) {
            float t = (float) i / 5f;
            float c = lerp(1f, 0.85f, t);
            shapeRenderer.setColor(new Color(c, c, c, 1f));
            shapeRenderer.circle(ballX, ballY, ballRadius * (1f - t * 0.2f));
        }

        // Highlight on ball
        shapeRenderer.setColor(new Color(1f, 1f, 1f, 0.9f));
        shapeRenderer.circle(ballX - ballRadius * 0.3f, ballY + ballRadius * 0.3f, ballRadius * 0.25f);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public void lightshow(float originX, float originY, float centerX, float centerY, float radius) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // render circle
        shapeRenderer.setColor(1f, 1f, 1f, 0.12f);
        shapeRenderer.circle(centerX, centerY, radius);

        // distance
        float dx = centerX - originX;
        float dy = centerY - originY;

        // render cone
        //TODO there is something wrong with the trigonometry...
        float angleDeg = MathUtils.atan2Deg360(dx, dy);
        shapeRenderer.setColor(1f, 1f, 1f, 0.05f);

        float x1 = centerX + MathUtils.cosDeg(angleDeg - radius) * radius;
        float y1 = centerY + MathUtils.sinDeg(angleDeg - radius) * radius;

        float x2 = centerX + MathUtils.cosDeg(angleDeg + radius) * radius;
        float y2 = centerY + MathUtils.sinDeg(angleDeg + radius) * radius;

        shapeRenderer.triangle(originX, originY, x1, y1, x2, y2);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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

    public void setWheelListener(final WheelAnimator.SpinEndListener wheelListener) {
        wheelAnimator.setListener(wheelListener);
    }

    public void setBallListener(final WheelAnimator.SpinEndListener listener) {
        ballAnimator.setListener(listener);
    }

    @Override
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

    public Optional<Integer> getSegmentIndexAt(float x, float y) {
        if (!contains(x, y)) {
            return Optional.empty();
        }

        Circle wheelCircle = new Circle(centerX, centerY, wheelRadius);
        Circle innerCircle = new Circle(centerX, centerY, innerRadius);
        if (wheelCircle.contains(x, y) && !innerCircle.contains(x, y)) {
            float angle = MathUtils.atan2Deg360(y - centerY, x - centerX);
            return Optional.of(getCurrentSegmentIndex(angle));
        }
        return Optional.empty();
    }

}
