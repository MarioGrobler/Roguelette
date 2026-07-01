package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.wheel.Segment;

/**
 * Passive: the real French-roulette <em>la partage</em> rule. When a ball lands on a 0 / colourless
 * segment, every losing bet refunds half its stake. Purely defensive — refunds are hard-capped at
 * the stake in {@code Bet.getPayout}, so two copies mean break-even on a zero, never profit.
 */
public class LaPartageFortune extends FortuneShopItem {

    private static final float REFUND = 0.5f;

    public LaPartageFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/laPartage.png")),
            Color.GOLDENROD, new Color(0.15f, 0.5f, 0.35f, 1f)));
        this.cost = 8;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()
            && resolution.getLanded().getCurrentColor() == Segment.SegmentColor.NONE) {
            resolution.addRefund(REFUND);
        }
    }

    @Override
    public String getShortDescription() {
        return "La Partage";
    }

    @Override
    public String getDescription() {
        return "When the ball lands on a 0 or other colourless segment, losing bets refund "
            + Math.round(REFUND * 100) + "% of their stake. The old French rule.";
    }
}
