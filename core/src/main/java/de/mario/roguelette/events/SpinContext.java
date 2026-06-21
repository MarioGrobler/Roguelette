package de.mario.roguelette.events;

import de.mario.roguelette.balls.Ball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Mutable description of the balls about to take part in a spin, passed to
 * {@link GameEventListener#onPrepareSpin}. The spin is seeded with the player's default ball(s);
 * listeners may add more (e.g. Double Ball). Each ball gets its own landing and pays out
 * independently, so a spin with two balls resolves the bets against both segments.
 */
public class SpinContext {
    private final List<Ball> balls = new ArrayList<>();

    public void addBall(final Ball ball) {
        balls.add(ball);
    }

    public List<Ball> getBalls() {
        return Collections.unmodifiableList(balls);
    }

    public int ballCount() {
        return balls.size();
    }
}
