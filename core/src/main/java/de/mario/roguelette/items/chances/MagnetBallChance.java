package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.ColorBet;
import de.mario.roguelette.events.LandingContext;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * While active, the ball is drawn toward the player's most-bet colour: if the natural landing
 * doesn't match it, there is a {@link #PULL_CHANCE} chance of one re-roll uniformly among the
 * matching segments — i.e. the miss chance is halved (wheel fraction p of your colour lands
 * p + (1-p)/2 of the time). A pull, never a guarantee. The dominant colour is read from the
 * stakes on the red/black colour bets; with no colour bet down, the magnet has nothing to grip.
 */
public class MagnetBallChance extends PendingChanceShopItem {

    private static final float PULL_CHANCE = 0.5f;

    public MagnetBallChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/magnetBall.png")),
            Color.WHITE, new Color(0.8f, 0.2f, 0.2f, 1f), new Color(0.35f, 0.35f, 0.4f, 1f)));
        this.cost = 14;
    }

    /** @return RED or BLACK with the larger colour-bet stake, or null if there is none (or a tie). */
    private Segment.SegmentColor dominantBetColor(final GameState gameState) {
        long red = 0;
        long black = 0;
        for (Bet bet : gameState.getBetManager().getBets()) {
            if (bet.getBetType() instanceof ColorBet) {
                Segment.SegmentColor c = ((ColorBet) bet.getBetType()).getSegmentColor();
                if (c == Segment.SegmentColor.RED) {
                    red += bet.getAmount();
                } else if (c == Segment.SegmentColor.BLACK) {
                    black += bet.getAmount();
                }
            }
        }
        if (red == black) {
            return null;
        }
        return red > black ? Segment.SegmentColor.RED : Segment.SegmentColor.BLACK;
    }

    private boolean matches(final Segment segment, final Segment.SegmentColor color) {
        Segment.SegmentColor c = segment.getCurrentColor();
        return c == color || c == Segment.SegmentColor.BOTH;
    }

    @Override
    public void onBallLanded(final GameState gameState, final LandingContext landing) {
        Segment.SegmentColor dominant = dominantBetColor(gameState);
        if (dominant == null || matches(landing.getSegment(), dominant)) {
            return;
        }
        if (MathUtils.random() >= PULL_CHANCE) {
            return; // the magnet slips
        }
        List<Integer> targets = new ArrayList<>();
        for (int i = 0; i < landing.getWheel().size(); i++) {
            if (matches(landing.getWheel().getSegmentAt(i), dominant)) {
                targets.add(i);
            }
        }
        if (!targets.isEmpty()) {
            landing.setSegmentIndex(targets.get(MathUtils.random(targets.size() - 1)));
        }
    }

    @Override
    public String getShortDescription() {
        return "Magnet Ball";
    }

    @Override
    public String getDescription() {
        return "The ball is drawn toward the colour you bet the most on: if it would land elsewhere,"
            + " there is a " + Math.round(PULL_CHANCE * 100) + "% chance it is pulled onto a matching"
            + " segment. Lasts for one turn.";
    }
}
