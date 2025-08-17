package de.mario.roguelette.wheel.effects;

import de.mario.roguelette.wheel.Segment;

public class ColorEffect extends SegmentEffect implements ColorModifier {

    private final Segment.SegmentColor color;

    public ColorEffect(int remainingRounds, Segment.SegmentColor segmentColor) {
        super(remainingRounds);
        this.color = segmentColor;
    }

    @Override
    public Segment.SegmentColor modifyColor() {
        return color;
    }
}
