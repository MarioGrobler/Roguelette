package de.mario.roguelette.wheel.effects;

public class MultiplierEffect extends SegmentEffect implements MultiplierModifier {
    private final float factor;

    public MultiplierEffect(int rounds, float factor) {
        super(rounds);
        this.factor = factor;
    }

    @Override
    public float modifyMultiplier() {
        return factor;
    }
}
