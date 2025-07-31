package de.mario.roguelette.wheel;

public class NumberSegment extends Segment {

    private final int number;

    public NumberSegment(int number) {
        super();
        this.number = number;
    }

    public NumberSegment(int number, SegmentColor color) {
        super(color);
        this.number = number;
    }

    @Override
    public String getDisplayText() {
        return String.valueOf(this.number);
    }

    public int getNumber() {
        return number;
    }
}
