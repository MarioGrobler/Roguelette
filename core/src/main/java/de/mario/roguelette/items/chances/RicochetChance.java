package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.events.LandingContext;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * While active, if the ball would land on a <em>losing</em> tile (one where none of your bets win) it
 * bounces off to a random other segment, giving the spin a second roll. Demonstrates the landing hook
 * of the event layer.
 */
public class RicochetChance extends PendingChanceShopItem {
    public RicochetChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/ricochet.png")),
            Color.WHITE, new Color(0.15f, 0.65f, 0.6f, 1f), new Color(0.94f, 0.55f, 0.16f, 1f)));
        this.cost = 10;
    }

    /** @return whether any of the player's current bets would win on the given segment. */
    private boolean winsSomething(final GameState gameState, final Segment segment) {
        for (Bet bet : gameState.getBetManager().getBets()) {
            if (bet.isWin(segment)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBallLanded(final GameState gameState, final LandingContext landing) {
        // Already a winning landing? Leave it. Only re-roll a tile that wins the player nothing.
        if (winsSomething(gameState, landing.getSegment())) {
            return;
        }

        int current = landing.getSegmentIndex();
        List<Integer> others = new ArrayList<>();
        for (int i = 0; i < landing.getWheel().size(); i++) {
            if (i != current) {
                others.add(i);
            }
        }
        if (!others.isEmpty()) {
            landing.setSegmentIndex(others.get(MathUtils.random(others.size() - 1)));
        }
    }

    @Override
    public String getShortDescription() {
        return "Ricochet";
    }

    @Override
    public String getDescription() {
        return "If the ball would land on a losing tile, it bounces off to a random other segment for "
            + "a second roll. Lasts for one turn.";
    }
}
