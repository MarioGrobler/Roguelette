package de.mario.roguelette.wheel;

public class JokerColorSegment extends Segment {

    public JokerColorSegment(SegmentColor color) {
        super(color);
    }

    public JokerColorSegment(SegmentColor color, float multiplier) {
        super(color, multiplier);
    }

    @Override
    public String getDisplayText() {
        return "";
    }

    @Override
    public String getShortDescription() {
        return getFunnyAdjective() + " Color Joker";
    }

    @Override
    public String getDescription() {
        return String.format("Color: %s\nMultiplier: %s", color, multiplier);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
