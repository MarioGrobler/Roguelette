package de.mario.roguelette.wheel;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NumberSegment that = (NumberSegment) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}
