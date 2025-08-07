package de.mario.roguelette.wheel;

public class JokerNumberRangeSegment extends Segment {

    private final int min;
    private final int max;

    public JokerNumberRangeSegment(int min, int max) {
        this(min, max, SegmentColor.NONE);
    }

    public JokerNumberRangeSegment(int min, int max, SegmentColor color) {
        this(min, max, color, 1f);
    }

    public JokerNumberRangeSegment(int min, int max, SegmentColor color, float multiplier) {
        super(color, multiplier);
        this.min = min;
        this.max = max;
    }

    @Override
    public String getDisplayText() {
        return String.format("%d-%d", min, max);
    }

    @Override
    public String getShortDescription() {
        return getFunnyAdjective() +  " Range Joker";
    }

    @Override
    public String getDescription() {
        return String.format("Numbers: %d - %d\nColor: %s\nMultiplier: %s", min, max, color, multiplier);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
