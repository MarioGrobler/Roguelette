package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.NumberBet;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.effects.NumberEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MirrorFateChance extends PendingChanceShopItem implements WheelSelectChance {
    private int mirrorNumber = -1; // -1: not set yet

    public MirrorFateChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/mirrorFate.png")), Color.WHITE, Color.PURPLE, Color.VIOLET));
        this.cost = 10;
    }

    @Override
    public float baseModifier(Bet bet) {
        if (bet.getBetType() instanceof NumberBet && ((NumberBet) bet.getBetType()).getNumber() == mirrorNumber) {
            return 14; // 36 base + 14 = 50
        }
        return 0;
    }

    @Override
    public float totalModifier(Bet bet) {
        return 1; // no change
    }

    @Override
    public String getShortDescription() {
        return "Mirror Fate";
    }

    @Override
    public String getDescription() {
        return "Select a number segment. Selects randomly up to 10 number segments to copy the number of the chosen one. Increases the base payout multiplier for number bets involving the chosen number to 50. Lasts for one turn.";
    }

    @Override
    public void onActivate(final GameState gameState) {
        gameState.pushState(GameState.GameStateMode.CHANCE_SEGMENT_SELECTING);
        gameState.setPendingChanceItem(this);
    }

    @Override
    public void onActivate(final GameState gameState, int segmentIndex) {
        this.mirrorNumber = ((NumberSegment) gameState.getWheel().getSegments().get(segmentIndex)).getCurrentNumber();

        List<NumberSegment> numberSegments = new ArrayList<>();
        for (Segment segment : gameState.getWheel().getSegments()) {
            if (segment instanceof NumberSegment) {
                numberSegments.add((NumberSegment) segment);
            }
        }

        if (numberSegments.isEmpty()) {
            return;
        }
        Collections.shuffle(numberSegments);
        List<NumberSegment> mirrors = numberSegments.subList(0, Math.min(numberSegments.size(), MathUtils.random(5, 10)));

        for (NumberSegment segment : mirrors) {
            segment.addEffect(new NumberEffect(1, this.mirrorNumber));
        }

        gameState.popState();
        gameState.setPendingChanceItem(null);

        // add directly to list because we do not want to stack the duration
        gameState.getPendingChanceManager().getActiveChances().add(this);
    }
}
