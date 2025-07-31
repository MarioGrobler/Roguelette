package de.mario.roguelette.wheel;

public class JokerNumberRangeSegment extends Segment {

    private final int min;
    private final int max;

    public JokerNumberRangeSegment(int min, int max) {
        super();
        this.min = min;
        this.max = max;
    }

    public JokerNumberRangeSegment(int min, int max, SegmentColor color) {
        super(color);
        this.min = min;
        this.max = max;
    }

    @Override
    public String getDisplayText() {
        return String.format("%d-%d", min, max);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
