package de.mario.roguelette.wheel.effects;

public class MultiplierEffect extends SegmentEffect implements MultiplierModifier {
    private final int factor;

    public MultiplierEffect(int rounds, int factor) {
        super(rounds);
        this.factor = factor;
    }

    @Override
    public float modifyMultiplier() {
        return factor;
    }
}
