package de.mario.roguelette.wheel.effects;

import de.mario.roguelette.wheel.Segment;

public class MultiplierEffect extends SegmentEffect {
    private final int factor;

    public MultiplierEffect(int rounds, int factor) {
        super(rounds);
        this.factor = factor;
    }

    @Override
    public float baseModifier() {
        return factor;
    }

    @Override
    public Segment.SegmentColor colorModifier() {
        return null;
    }
}
