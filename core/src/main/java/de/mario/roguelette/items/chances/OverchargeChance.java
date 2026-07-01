package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.effects.MultiplierEffect;

/**
 * The player selects a segment; its multiplier is tripled for one turn. Solo it's a prayer —
 * the point is the combo (Freeze the patch, Overcharge the target): a targeting item plus this
 * payout item is exactly the "engineer one big hit" the run is built around.
 *
 * <p>Implemented as a one-turn additive {@link MultiplierEffect} of twice the segment's base
 * multiplier (base + 2&middot;base = 3&middot;base), expiring through the wheel's regular
 * turn-change effect ticking.
 */
public class OverchargeChance extends PendingChanceShopItem implements WheelSelectChance {

    private boolean applied = false;

    public OverchargeChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/overcharge.png")),
            Color.WHITE, new Color(0.95f, 0.75f, 0.15f, 1f), new Color(0.9f, 0.4f, 0.1f, 1f)));
        this.cost = 14;
    }

    @Override
    public void onActivate(final GameState gameState) {
        gameState.pushState(GameState.GameStateMode.CHANCE_SEGMENT_SELECTING);
        gameState.setPendingChanceItem(this);
    }

    @Override
    public void onActivate(final GameState gameState, int segmentIndex) {
        Segment segment = gameState.getWheel().getSegmentAt(segmentIndex);
        segment.addEffect(new MultiplierEffect(1, segment.getMultiplier() * 2f));
        applied = true;

        gameState.popState();
        gameState.setPendingChanceItem(null);

        // add directly to list because we do not want to stack the duration
        gameState.getPendingChanceManager().getActiveChances().add(this);
    }

    @Override
    public String getShortDescription() {
        return "Overcharge";
    }

    @Override
    public String getDescription() {
        if (!applied) {
            return "Select a segment: its multiplier is tripled for one turn.";
        }
        return "The selected segment's multiplier is tripled for this turn.";
    }
}
