package de.mario.roguelette.render.wheel;

import de.mario.roguelette.wheel.Segment;


public class SegmentAngle {
    private Segment segment;
    private float startAngle;
    private float endAngle;

    public SegmentAngle() {}

    public SegmentAngle(final Segment segment, float startAngle, float endAngle) {
        this.segment = segment;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }

    public Segment getSegment() {
        return segment;
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
    }
}
