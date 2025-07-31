package de.mario.roguelette.animator;


public class WheelAnimator {

    public interface Listener {
        void onSpinEnd();
    }

    /** current rotation in degrees */
    protected float rotationAngle = 0f;

    /** current speed in degree per second */
    protected float currentSpeed = 0f;

    protected float deceleration = 0f; // negative value
    protected boolean spinning = false;
    protected boolean decelerating = false;

    // targeting an angle
    protected float triggerAngle = 0f;
    protected boolean hasTarget = false;

    protected boolean clockwise = false;

    protected Listener listener;

    public void update(float delta) {
        if (!spinning) return;

        // continue to spin until trigger angle is reached
        if (hasTarget) {
            float distToTrigger = Math.abs(triggerAngle - rotationAngle);
            if (distToTrigger < 9) { // TODO this can be a problem when the wheel is too fast
                rotationAngle = triggerAngle;
                decelerating = true;
                hasTarget = false;
            }
        }

        if (decelerating) {
            currentSpeed += deceleration * delta;
            if (currentSpeed < 0f) {
                currentSpeed = 0f;
                spinning = false;
                decelerating = false;
                if (listener != null) {
                    listener.onSpinEnd();
                }
                return;
            }
        }

        rotationAngle = normalizeAngle(rotationAngle + currentSpeed * delta);
    }

    /**
     * Spins infinitely with constant speed
     * @param speed in degree per second
     */
    public void startInfiniteSpin(float speed) {
        this.currentSpeed = speed;
        this.spinning = true;
        this.decelerating = false;
        this.hasTarget = false;
    }

    /**
     * Stops with the given deceleration
     * @param deceleration in degree per second^2
     */
    public void stopWithDeceleration(float deceleration) {
        this.decelerating = true;
        this.deceleration = -Math.abs(deceleration);
        this.hasTarget = false;
    }

    /**
     * Tries to stop at the given angle by decelerating with the given deceleration
     * @param targetAngle in degrees
     * @param deceleration in degree per second^2
     */
    public void stopAtTarget(float targetAngle, float deceleration) {
        targetAngle = clockwise ? 360f - targetAngle : targetAngle;

        this.decelerating = false; // wait until decelerating guarantees the target angle
        this.deceleration = -Math.abs(deceleration);
        this.hasTarget = true;

        //angle we need to spin before hitting the break
        float stopDistance = (currentSpeed * currentSpeed) / (2f * Math.abs(deceleration));
        triggerAngle = normalizeAngle(targetAngle - stopDistance);
    }

    public float getRotationAngle() {
        return clockwise ? 360f - rotationAngle : rotationAngle;
    }

    public boolean isSpinning() {
        return spinning;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public float normalizeAngle(float angle) {
        angle %= 360f;
        if (angle < 0f) angle += 360f;
        return angle;
    }
}
