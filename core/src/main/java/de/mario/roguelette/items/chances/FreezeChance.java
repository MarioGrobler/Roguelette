package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.LandingContext;

/**
 * The player selects a segment of the wheel; while active, the ball is guaranteed to land on
 * that frozen segment. Demonstrates combining the segment-select flow ({@link WheelSelectChance})
 * with the landing hook of the event layer.
 */
public class FreezeChance extends PendingChanceShopItem implements WheelSelectChance {
    private int frozenIndex = -1; // -1: not selected yet

    public FreezeChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/freeze.png")),
            Color.WHITE, new Color(0.55f, 0.85f, 1f, 1f), new Color(0.15f, 0.45f, 0.85f, 1f)));
        this.cost = 18;
    }

    @Override
    public void onBallLanded(final GameState gameState, final LandingContext landing) {
        if (frozenIndex >= 0 && frozenIndex < landing.getWheel().size()) {
            landing.setSegmentIndex(frozenIndex);
        }
    }

    @Override
    public String getShortDescription() {
        return "Freeze";
    }

    @Override
    public String getDescription() {
        if (frozenIndex == -1) {
            return "Select a segment to freeze. The ball is guaranteed to land on that segment. Lasts for one turn.";
        }
        return "The ball is frozen to land on the selected segment.";
    }

    @Override
    public void onActivate(final GameState gameState) {
        gameState.pushState(GameState.GameStateMode.CHANCE_SEGMENT_SELECTING);
        gameState.setPendingChanceItem(this);
    }

    @Override
    public void onActivate(final GameState gameState, int segmentIndex) {
        this.frozenIndex = segmentIndex;

        gameState.popState();
        gameState.setPendingChanceItem(null);

        // add directly to list because we do not want to stack the duration
        gameState.getPendingChanceManager().getActiveChances().add(this);
    }
}
