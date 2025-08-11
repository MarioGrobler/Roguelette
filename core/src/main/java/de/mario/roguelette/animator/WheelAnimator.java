package de.mario.roguelette.animator;


import de.mario.roguelette.util.MathHelper;

public class WheelAnimator {

    public interface SpinEndListener {
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

    protected long spinStartTime;
    protected float spinStartAngle;
    protected float spinStartSpeed;

    // this stuff is needed to avoid cumulative floating point errors
    protected long brakeStartTime;
    protected float brakeStartAngle;
    protected float brakeStartSpeed;

    protected float stopTime; // duration of break phase in seconds

    protected boolean clockwise = false;

    protected SpinEndListener listener;

    private boolean passedTrigger(float prevAngle, float currentAngle, float triggerAngle) {
        prevAngle = MathHelper.normalizeAngle(prevAngle);
        currentAngle = MathHelper.normalizeAngle(currentAngle);
        triggerAngle = MathHelper.normalizeAngle(triggerAngle);

        // correct stuff at the 0/360 border
        if (currentAngle < prevAngle) {
            currentAngle += 360;
        }
        if (triggerAngle < prevAngle) {
            triggerAngle += 360;
        }

        return currentAngle >= triggerAngle && prevAngle <= triggerAngle;
    }


    public void update(float delta) {
        if (!spinning) return;

        // continue to spin until trigger angle is reached
        if (hasTarget && passedTrigger(rotationAngle, rotationAngle + currentSpeed*delta, triggerAngle)) {
            // hit the break
            decelerating = true;
            hasTarget = false;

            rotationAngle = triggerAngle;
            brakeStartAngle = rotationAngle;
            brakeStartSpeed = currentSpeed;
            brakeStartTime = System.nanoTime();

            stopTime = brakeStartSpeed / Math.abs(deceleration);
        }

        // use closed formula now to precent cumulative floating errors
        if (decelerating) {
            float t = (System.nanoTime() - brakeStartTime) / 1_000_000_000f;
            if (t >= stopTime) {
                rotationAngle = brakeStartAngle + brakeStartSpeed * stopTime - 0.5f * Math.abs(deceleration) * stopTime * stopTime;
                currentSpeed = 0f;
                spinning = false;
                decelerating = false;
                if (listener != null) listener.onSpinEnd();
                return;
            } else {
                rotationAngle = brakeStartAngle + brakeStartSpeed * t - 0.5f * Math.abs(deceleration) * t * t;
                currentSpeed = brakeStartSpeed - Math.abs(deceleration) * t;
            }
        } else if (spinning) {
            // constant spin
            float t = (System.nanoTime() - spinStartTime) / 1_000_000_000f;
            rotationAngle = spinStartAngle + spinStartSpeed * t;
            currentSpeed = spinStartSpeed;
        }

        rotationAngle = MathHelper.normalizeAngle(rotationAngle);
    }

    /**
     * Spins infinitely with constant speed
     * @param speed in degree per second
     */
    public void startInfiniteSpin(float speed) {
        this.spinning = true;
        this.decelerating = false;
        this.hasTarget = false;

        this.currentSpeed = speed;
        this.spinStartSpeed = speed;
        this.spinStartAngle = rotationAngle;
        this.spinStartTime = System.nanoTime();
    }

    /**
     * Stops with the given deceleration immediately.
     * @param deceleration in degree per second^2
     */
    public void stopWithDeceleration(float deceleration) {
        hasTarget = false;
        decelerating = true;

        this.deceleration = -Math.abs(deceleration); // immer negativ
        brakeStartAngle = rotationAngle;
        brakeStartSpeed = currentSpeed;
        brakeStartTime = System.nanoTime();

        // compute stop time
        stopTime = brakeStartSpeed / Math.abs(this.deceleration);
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

        // angle we need to spin before hitting the break
        float stopDistance = (currentSpeed * currentSpeed) / (2f * Math.abs(deceleration));
        triggerAngle = MathHelper.normalizeAngle(targetAngle - stopDistance);
    }

    public float getRotationAngle() {
        return clockwise ? 360f - rotationAngle : rotationAngle;
    }

    public void setListener(SpinEndListener listener) {
        this.listener = listener;
    }

}
