package de.mario.roguelette.animator;

import com.badlogic.gdx.math.MathUtils;

public class BallAnimator extends WheelAnimator {
    private float radius;
    private float minRadius;

    public BallAnimator(float minRadius) {
        this.minRadius = minRadius;
        this.clockwise = true;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (!spinning) {
           radius = minRadius;
        }

        if (decelerating) {
            radius = MathUtils.lerp(radius, minRadius, delta);
        }
    }

    @Override
    public void startInfiniteSpin(float delta) {
        this.radius = minRadius * 1.2f;
        super.startInfiniteSpin(delta);
    }

    public void startInfiniteSpin(float delta, float radius) {
        this.radius = radius;
        super.startInfiniteSpin(delta);
    }

    public float getX(float centerX) {
        return centerX + MathUtils.cosDeg(getRotationAngle()) * radius;
    }

    public float getY(float centerY) {
        return centerY + MathUtils.sinDeg(getRotationAngle()) * radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getMinRadius() {
        return minRadius;
    }

    public void setMinRadius(float minRadius) {
        this.minRadius = minRadius;
    }
}
