package de.mario.roguelette.curses;

import de.mario.roguelette.GameState;
import de.mario.roguelette.wheel.DevilSegment;

/**
 * Main curse 1 (levels 1+): a {@link DevilSegment} joins the wheel at run start — never wins,
 * never removable. The house's permanent thumb on the scale, and the anchor of the Casino-Curses
 * "deal with the devil" framing.
 */
public class DevilsMarkCurse extends Curse {

    @Override
    public String getName() {
        return "The Devil's Mark";
    }

    @Override
    public String getDescription() {
        return "A Devil's Segment joins the wheel: no bet ever wins there, and it cannot be removed.";
    }

    @Override
    public void applyRunSetup(final GameState gameState) {
        gameState.getWheel().addSegmentRandom(new DevilSegment());
    }
}
