package de.mario.roguelette.curses;

import de.mario.roguelette.GameState;
import de.mario.roguelette.boss.BossRoster;
import de.mario.roguelette.events.GameEventListener;
import de.mario.roguelette.wheel.DevilSegment;

import java.util.Collections;
import java.util.List;

/**
 * Main curse 2 (levels 4+): after every defeated boss, another {@link DevilSegment} joins the
 * wheel — the dead odds grow with the run (up to +4 by the end).
 *
 * <p>Implementation note: there is no boss-defeated event, so the listener watches the stage
 * counter on {@code onTurnChange} and plants the segment when the previous stage was boss-gated.
 * Turn-change effects run after a spin resolves, so the new segment enters play from the second
 * spin of the post-boss stage (one spin of grace — acceptable, the wheel renderer refresh happens
 * on the same tick).
 */
public class DevilsHarvestCurse extends Curse {

    @Override
    public String getName() {
        return "The Devil's Harvest";
    }

    @Override
    public String getDescription() {
        return "After every boss, another Devil's Segment joins the wheel.";
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new GameEventListener() {
            private int lastStage = 1;

            @Override
            public void onTurnChange(final GameState gameState) {
                int stage = gameState.getCurrentStage();
                if (stage != lastStage) {
                    if (BossRoster.hasBoss(lastStage)) {
                        gameState.getWheel().addSegmentRandom(new DevilSegment());
                    }
                    lastStage = stage;
                }
            }
        });
    }
}
