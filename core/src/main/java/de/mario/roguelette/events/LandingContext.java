package de.mario.roguelette.events;

import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.Wheel;

/**
 * Mutable description of where the ball is about to land, passed to
 * {@link GameEventListener#onBallLanded}. Listeners may override the landing by changing the
 * target segment index.
 */
public class LandingContext {
    private final Wheel wheel;
    private final int originalIndex;
    private int segmentIndex;

    public LandingContext(final Wheel wheel, final int segmentIndex) {
        this.wheel = wheel;
        this.originalIndex = segmentIndex;
        this.segmentIndex = segmentIndex;
    }

    public Wheel getWheel() {
        return wheel;
    }

    /**
     * @return the segment index originally rolled, before any listener changed it
     */
    public int getOriginalIndex() {
        return originalIndex;
    }

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(final int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    public Segment getSegment() {
        return wheel.getSegmentAt(segmentIndex);
    }

    /**
     * @return true if a listener changed the landing away from the originally rolled segment
     */
    public boolean wasChanged() {
        return segmentIndex != originalIndex;
    }
}
