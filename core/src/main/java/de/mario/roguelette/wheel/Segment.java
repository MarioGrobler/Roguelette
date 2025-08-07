package de.mario.roguelette.wheel;

public abstract class Segment {

    public enum SegmentColor {
        // NONE eg for 0, BOTH for custom segments
        RED, BLACK, BOTH, NONE
    }

    protected SegmentColor color = SegmentColor.NONE;
    protected float multiplier = 1f;

    public Segment() {}

    public Segment(SegmentColor color) {
        this(color, 1f);
    }

    public Segment(SegmentColor color, float multiplier) {
        this.color = color;
        this.multiplier = multiplier;
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

    protected String getFunnyAdjective() {
        if (multiplier < 1f) {
            return "Bad";
        }
        if (multiplier == 1f) {
            return "Normalmode";
        }
        if (multiplier <= 2f) {
            return "Lucky";
        }
        if (multiplier <= 3f) {
            return "Brilliant";
        }
        return "Magnificent";
    }

    /**
     * @return A very short display text to draw on the number
     */
    public abstract String getDisplayText();

    /**
     * @return A short description
     */
    public abstract String getShortDescription();

    /**
     * @return A not-so-short description
     */
    public abstract String getDescription();
}
