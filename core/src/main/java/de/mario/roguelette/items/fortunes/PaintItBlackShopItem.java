package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.ColorBet;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

public class PaintItBlackShopItem extends FortuneShopItem {

    private float blackModifier = 0f;

    public PaintItBlackShopItem() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/paintItBlack.png")), Color.GOLDENROD, Color.BROWN));
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        List<Segment> nonBlacks = new ArrayList<>();
        for (Segment segment : gameState.getWheel().getSegments()) {
            if (segment.getColor() != Segment.SegmentColor.BLACK) {
                nonBlacks.add(segment);
            }
        }

        if (!nonBlacks.isEmpty()) {
            int rnd = MathUtils.random(0, nonBlacks.size() - 1);
            nonBlacks.get(rnd).setColor(Segment.SegmentColor.BLACK);
        }

        blackModifier += 0.5f;
    }

    @Override
    public float baseModifier(final Bet bet) {
        if (bet.getBetType() instanceof ColorBet && ((ColorBet) bet.getBetType()).getSegmentColor() == Segment.SegmentColor.BLACK) {
            return blackModifier;
        }
        return 0;
    }

    @Override
    public float totalModifier(final Bet bet) {
        return 1; // no change
    }

    @Override
    public String getShortDescription() {
        return "Paint It Black";
    }

    @Override
    public String getDescription() {
        return "Every turn, paints a random segment of the wheel black and increases the payout multiplier of black color bets by 0.5";
    }
}
