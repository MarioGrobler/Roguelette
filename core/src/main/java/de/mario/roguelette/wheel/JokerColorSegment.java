package de.mario.roguelette.wheel;

import de.mario.roguelette.exception.GameException;

public class JokerColorSegment extends Segment {

    public JokerColorSegment(SegmentColor color) {
        super(color);
        //TODO is this legal?
        if (color == Segment.SegmentColor.BOTH || color == Segment.SegmentColor.NONE) {
            throw new GameException("Color for JokerColorSegment may neither be BOTH nor NONE");
        }
    }

    @Override
    public String getDisplayText() {
        return "";
    }

}
