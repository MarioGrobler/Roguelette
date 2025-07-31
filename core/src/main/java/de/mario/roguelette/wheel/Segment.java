package de.mario.roguelette.wheel;

public abstract class Segment {

    public enum SegmentColor {
        // NONE eg for 0, BOTH for custom segments
        RED, BLACK, BOTH, NONE
    }

    private SegmentColor color = SegmentColor.NONE;
    private float multiplier = 1f;
    private boolean isGolden = false;

    public Segment() {}

    public Segment(SegmentColor color) {
        this.color = color;
    }

    public SegmentColor getColor() {
        return color;
    }

    public void setColor(SegmentColor color) {
        this.color = color;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public boolean isGolden() {
        return isGolden;
    }

    public void setGolden(boolean golden) {
        isGolden = golden;
    }

    public abstract String getDisplayText();

}
