package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.ColorBet;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

public class PaintItBlackFortune extends FortuneShopItem {

    // Bounded so this is a strong synergy piece, not an unbounded solo win-engine. Over a long
    // run an uncapped +0.5/turn black payout AND ever-more black segments turns a black color bet
    // into a near-guaranteed high-multiplier machine; the caps make it a defined power level.
    private static final float MAX_BLACK_MODIFIER = 2.0f; // black color bets pay at most +2 (up to 4x)
    private static final int   MAX_PAINTS = 6;            // total extra segments turned black over a run

    private float blackModifier = 0f;
    private int paints = 0;

    public PaintItBlackFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/paintItBlack.png")), Color.GOLDENROD, new Color(0.12f, 0.12f, 0.15f, 1f)));
        this.cost = 20;
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        if (paints < MAX_PAINTS) {
            List<Segment> nonBlacks = new ArrayList<>();
            for (Segment segment : gameState.getWheel().getSegments()) {
                if (segment.getColor() != Segment.SegmentColor.BLACK) {
                    nonBlacks.add(segment);
                }
            }

            if (!nonBlacks.isEmpty()) {
                int rnd = MathUtils.random(0, nonBlacks.size() - 1);
                nonBlacks.get(rnd).setColor(Segment.SegmentColor.BLACK);
                paints++;
            }
        }

        blackModifier = Math.min(MAX_BLACK_MODIFIER, blackModifier + 0.5f);
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        if (resolution.getBet().getBetType() instanceof ColorBet
            && ((ColorBet) resolution.getBet().getBetType()).getSegmentColor() == Segment.SegmentColor.BLACK) {
            resolution.addBase(blackModifier);
        }
    }

    @Override
    public String getShortDescription() {
        return "Paint It Black";
    }

    @Override
    public String getDescription() {
        return "Every turn, paints a random segment of the wheel black (up to " + MAX_PAINTS
            + ") and increases the payout of black color bets by 0.5 (up to +" + (int) MAX_BLACK_MODIFIER
            + ").\nCurrent bonus: " + blackModifier;
    }
}
