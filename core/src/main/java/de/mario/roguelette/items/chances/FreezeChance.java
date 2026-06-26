package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.LandingContext;

/**
 * The player selects a segment of the wheel; while active, the ball is guaranteed to land somewhere
 * within a frozen <em>patch</em> of {@link #FREEZE_RADIUS} segments either side of it (not a single
 * segment). Because the wheel order is scrambled — adjacent segments are unrelated numbers of mixed
 * colour — knowing the landing patch can't be cashed in as a clean single-number 36x; the player has
 * to hedge across the patch for a much smaller guaranteed return. This keeps Freeze strong without
 * being an auto-win (it used to force one exact, chosen segment).
 */
public class FreezeChance extends PendingChanceShopItem implements WheelSelectChance {
    private static final int FREEZE_RADIUS = 2; // patch = selected segment +/- this many neighbours

    private int selectedIndex = -1; // -1: not selected yet

    public FreezeChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/freeze.png")),
            Color.WHITE, new Color(0.55f, 0.85f, 1f, 1f), new Color(0.15f, 0.45f, 0.85f, 1f)));
        this.cost = 18;
    }

    @Override
    public void onBallLanded(final GameState gameState, final LandingContext landing) {
        int size = landing.getWheel().size();
        if (selectedIndex < 0 || size <= 0) {
            return;
        }
        int center = ((selectedIndex % size) + size) % size;
        // If the natural landing is already inside the frozen patch, leave it; otherwise redirect
        // to a uniformly random segment within the patch.
        if (circularDistance(landing.getSegmentIndex(), center, size) <= FREEZE_RADIUS) {
            return;
        }
        int offset = MathUtils.random(-FREEZE_RADIUS, FREEZE_RADIUS);
        landing.setSegmentIndex(((center + offset) % size + size) % size);
    }

    /** Shortest distance between two indices around a wheel of the given size. */
    private static int circularDistance(int a, int b, int size) {
        int d = Math.abs(a - b) % size;
        return Math.min(d, size - d);
    }

    @Override
    public String getShortDescription() {
        return "Freeze";
    }

    @Override
    public String getDescription() {
        if (selectedIndex == -1) {
            return "Select a segment to freeze. The ball is guaranteed to land within " + FREEZE_RADIUS
                + " segments of it (a frozen patch). Lasts for one turn.";
        }
        return "The ball is frozen to land within the selected patch (+/-" + FREEZE_RADIUS + " segments).";
    }

    @Override
    public void onActivate(final GameState gameState) {
        gameState.pushState(GameState.GameStateMode.CHANCE_SEGMENT_SELECTING);
        gameState.setPendingChanceItem(this);
    }

    @Override
    public void onActivate(final GameState gameState, int segmentIndex) {
        this.selectedIndex = segmentIndex;

        gameState.popState();
        gameState.setPendingChanceItem(null);

        // add directly to list because we do not want to stack the duration
        gameState.getPendingChanceManager().getActiveChances().add(this);
    }
}
