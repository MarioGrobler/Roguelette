package de.mario.roguelette.balls;

import com.badlogic.gdx.graphics.Color;

/**
 * A single ball taking part in one spin. Carries a display tint so multiple balls are visually
 * distinguishable, and a payout factor scaling every win this ball's landing produces (the
 * per-ball payout bias planned for ball "players"; Twin Ball's permanent extra ball pays 50%).
 * Still the natural place to hang future per-ball behaviour: the planned character-style "ball
 * players" (a default ball, a red ball that pays more on red, ...) would each supply their own
 * {@link Ball}, and ball-related chances (e.g. Double Ball) add extra ones to a
 * {@link de.mario.roguelette.events.SpinContext} before the spin.
 */
public class Ball {

    private final Color tint;
    private final String name;
    private final float payoutFactor;

    public Ball(final Color tint) {
        this(tint, "Classic Ball");
    }

    public Ball(final Color tint, final String name) {
        this(tint, name, 1f);
    }

    public Ball(final Color tint, final String name, final float payoutFactor) {
        this.tint = tint;
        this.name = name;
        this.payoutFactor = payoutFactor;
    }

    /**
     * The default ball every spin starts with. A light, slightly cool grey (rather than pure
     * white) so the white shimmer in {@code WheelRenderer} actually reads as a highlight, giving
     * the ball a pearl/chrome look. Easy to retheme later per the planned ball "player select".
     */
    public static Ball defaultBall() {
        return new Ball(new Color(0.80f, 0.80f, 0.84f, 1f), "Classic Ball");
    }

    public Color getTint() {
        return tint;
    }

    /** Display name shown on the character-select signature-ball chip. */
    public String getName() {
        return name;
    }

    /** Scales the winnings of every bet this ball's landing wins (1 = full payout). */
    public float getPayoutFactor() {
        return payoutFactor;
    }
}
