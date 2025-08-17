package de.mario.roguelette.wheel.effects;

public class NumberEffect extends SegmentEffect implements NumberModifier {
    private final int number;

    public NumberEffect(int remainingRounds, int number) {
        super(remainingRounds);
        this.number = number;
    }

    @Override
    public int numberModifier() {
        return number;
    }
}
