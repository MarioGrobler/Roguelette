package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.events.SpinContext;

/**
 * Adds a second ball to the next spin. Both balls land independently and the bets pay out against
 * each, so every winning bet is effectively doubled (and losses are not). The first ball modifier
 * built on the multi-ball spin infrastructure.
 */
public class DoubleBallChance extends PendingChanceShopItem {

    private static final Color SECOND_BALL_TINT = new Color(1f, 0.78f, 0.2f, 1f); // gold, to tell the balls apart

    public DoubleBallChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/doubleBall.png")),
            new Color(0.15f, 0.28f, 0.5f, 1f),   // navy fill so the white ball stays visible
            new Color(1f, 0.78f, 0.2f, 1f),      // gold border
            new Color(0.45f, 0.7f, 1f, 1f)));    // light-blue pattern
        this.cost = 16;
    }

    @Override
    public void onPrepareSpin(final GameState gameState, final SpinContext spin) {
        spin.addBall(new Ball(SECOND_BALL_TINT));
    }

    @Override
    public String getShortDescription() {
        return "Double Ball";
    }

    @Override
    public String getDescription() {
        return "The next spin is played with two balls. Each ball lands separately and your bets pay out for both. Lasts for one turn.";
    }
}
