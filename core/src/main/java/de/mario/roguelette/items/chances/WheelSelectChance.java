package de.mario.roguelette.items.chances;

import de.mario.roguelette.GameState;

/**
 * An interface for chance item that, on activation, require to select a segment of the wheel before they can "truly" activate
 */
public interface WheelSelectChance {

    void onActivate(final GameState gameState, int segmentIndex);
}
