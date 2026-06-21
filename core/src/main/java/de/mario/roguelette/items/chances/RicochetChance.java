package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.LandingContext;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * While active, if the ball would land on a zero it bounces off to a random non-zero segment.
 * Demonstrates the landing hook of the event layer.
 */
public class RicochetChance extends PendingChanceShopItem {
    public RicochetChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/ricochet.png")),
            Color.WHITE, new Color(0.15f, 0.65f, 0.6f, 1f), new Color(0.94f, 0.55f, 0.16f, 1f)));
        this.cost = 10;
    }

    private boolean isZero(final Segment segment) {
        return segment instanceof NumberSegment && ((NumberSegment) segment).getCurrentNumber() == 0;
    }

    @Override
    public void onBallLanded(final GameState gameState, final LandingContext landing) {
        if (!isZero(landing.getSegment())) {
            return;
        }

        List<Integer> nonZeroIndices = new ArrayList<>();
        for (int i = 0; i < landing.getWheel().size(); i++) {
            if (!isZero(landing.getWheel().getSegmentAt(i))) {
                nonZeroIndices.add(i);
            }
        }
        if (!nonZeroIndices.isEmpty()) {
            landing.setSegmentIndex(nonZeroIndices.get(MathUtils.random(nonZeroIndices.size() - 1)));
        }
    }

    @Override
    public String getShortDescription() {
        return "Ricochet";
    }

    @Override
    public String getDescription() {
        return "If the ball would land on a zero, it bounces off to a random non-zero segment. Lasts for one turn.";
    }
}
