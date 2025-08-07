package de.mario.roguelette.wheel;

public class NumberSegment extends Segment {

    private final int number;

    public NumberSegment(int number) {
        this(number, Segment.SegmentColor.NONE);
    }

    public NumberSegment(int number, SegmentColor color) {
        this(number, color, 1f);
    }

    public NumberSegment(int number, SegmentColor color, float multiplier) {
        super(color, multiplier);
        this.number = number;
    }

    @Override
    public String getDisplayText() {
        return String.valueOf(this.number);
    }

    @Override
    public String getShortDescription() {
        return getFunnyAdjective() + " " + number;
    }

    @Override
    public String getDescription() {
        return String.format("Number: %d\nColor: %s\nMultiplier: %s", number, color, multiplier);
    }

    public int getNumber() {
        return number;
    }
}
