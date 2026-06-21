package de.mario.roguelette.balls;

import com.badlogic.gdx.graphics.Color;

/**
 * A single ball taking part in one spin. Today it only carries a display tint so multiple balls
 * are visually distinguishable, but it is the natural place to hang future per-ball behaviour:
 * the planned character-style "ball players" (a default ball, a red ball that pays more on red,
 * ...) would each supply their own {@link Ball}, and ball-related chances (e.g. Double Ball)
 * add extra ones to a {@link de.mario.roguelette.events.SpinContext} before the spin.
 */
public class Ball {

    private final Color tint;

    public Ball(final Color tint) {
        this.tint = tint;
    }

    /**
     * The default ball every spin starts with. A light, slightly cool grey (rather than pure
     * white) so the white shimmer in {@code WheelRenderer} actually reads as a highlight, giving
     * the ball a pearl/chrome look. Easy to retheme later per the planned ball "player select".
     */
    public static Ball defaultBall() {
        return new Ball(new Color(0.80f, 0.80f, 0.84f, 1f));
    }

    public Color getTint() {
        return tint;
    }
}
