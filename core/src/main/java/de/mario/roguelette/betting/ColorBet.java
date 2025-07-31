package de.mario.roguelette.betting;

import de.mario.roguelette.exception.GameException;
import de.mario.roguelette.wheel.Segment;

import java.util.Objects;

public class ColorBet implements BetType {

    private final Segment.SegmentColor color;

    public ColorBet(Segment.SegmentColor color) {
        if (color == Segment.SegmentColor.BOTH || color == Segment.SegmentColor.NONE) {
            throw new GameException("Type of ColorBet may neither be BOTH nor NONE");
        }
        this.color = color;
    }

    @Override
    public boolean isWinningSegment(Segment segment) {
        return segment.getColor() == color;
    }

    @Override
    public float getPayoutMultiplier() {
        return 2f;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ColorBet colorBet = (ColorBet) o;
        return color == colorBet.color;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(color);
    }

    public Segment.SegmentColor getSegmentColor() {
        return color;
    }
}
