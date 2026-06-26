package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.NumberBet;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.wheel.JokerNumberRangeSegment;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.RouletteRules;
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
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        if (resolution.getBet().getBetType() instanceof NumberBet
            && ((NumberBet) resolution.getBet().getBetType()).getNumber() == mirrorNumber) {
            resolution.addBase(14); // 36 base + 14 = 50
        }
    }

    @Override
    public String getShortDescription() {
        return "Mirror Fate";
    }

    @Override
    public String getDescription() {
        if (mirrorNumber == -1) {
            return "Select any segment. Up to 10 random number segments copy a number drawn from it, and the base payout multiplier for number bets on that number is raised to 50. Lasts for one turn.";
        }
        return "The base payout multiplier for number bets on " + mirrorNumber + " is increased to 50.";
    }

    @Override
    public void onActivate(final GameState gameState) {
        gameState.pushState(GameState.GameStateMode.CHANCE_SEGMENT_SELECTING);
        gameState.setPendingChanceItem(this);
    }

    /**
     * Derives the number to mirror from any selected segment, so non-number segments don't crash
     * (and can be played for synergy): a number segment gives its number, a range segment a random
     * number inside its range, a colour joker a random number of that colour.
     */
    private int deriveNumber(final Segment segment) {
        if (segment instanceof NumberSegment) {
            return ((NumberSegment) segment).getCurrentNumber();
        }
        if (segment instanceof JokerNumberRangeSegment) {
            JokerNumberRangeSegment range = (JokerNumberRangeSegment) segment;
            return MathUtils.random(range.getMin(), range.getMax());
        }
        // colour joker (or anything else): pick a random number matching the segment's colour
        List<Integer> candidates = new ArrayList<>();
        for (int n = 0; n <= 36; n++) {
            if (RouletteRules.getStandardColor(n) == segment.getCurrentColor()) {
                candidates.add(n);
            }
        }
        if (candidates.isEmpty()) {
            return MathUtils.random(0, 36);
        }
        return candidates.get(MathUtils.random(candidates.size() - 1));
    }

    @Override
    public void onActivate(final GameState gameState, int segmentIndex) {
        this.mirrorNumber = deriveNumber(gameState.getWheel().getSegments().get(segmentIndex));

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
