package de.mario.roguelette.boss;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.GameEventListener;
import de.mario.roguelette.wheel.JokerNumberRangeSegment;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.Wheel;

/**
 * Boss debuff: before every spin, the most valuable segment on the wheel is destroyed (removed for
 * good). Directly attacks the engine the player has spent the run building — a wheel stacked with
 * high-multiplier joker segments simply has its best pieces eaten, three spins running.
 *
 * <p>"Value" mirrors what the player actually invested in: any joker segment outranks any plain
 * number segment, and within that ties break on the (current) multiplier, then on how many numbers a
 * range joker covers. A small floor on the wheel size keeps the wheel functional during the fight.
 */
public class DestroyValuableSegmentEffect implements GameEventListener {

    /** Never shrink the wheel below this many segments (keeps spins meaningful). */
    private static final int MIN_WHEEL_SIZE = 5;

    @Override
    public void onSpinStart(final GameState gameState) {
        final Wheel wheel = gameState.getWheel();
        if (wheel.size() <= MIN_WHEEL_SIZE) {
            return;
        }

        int bestIndex = -1;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < wheel.size(); i++) {
            if (wheel.getSegmentAt(i).isUnremovable()) {
                continue; // cursed segments (Devil's Segment) can't be destroyed, even by the Devil
            }
            double v = value(wheel.getSegmentAt(i));
            if (v > bestValue) {
                bestValue = v;
                bestIndex = i;
            }
        }
        if (bestIndex >= 0) {
            wheel.removeSegmentAt(bestIndex);
        }
    }

    /** Higher = more valuable. Jokers always outrank numbers; ties break on multiplier, then coverage. */
    private double value(final Segment segment) {
        double v = segment.getCurrentMultiplier();
        if (!(segment instanceof NumberSegment)) {
            v += 100.0; // any joker is worth more than any plain number segment
        }
        if (segment instanceof JokerNumberRangeSegment) {
            JokerNumberRangeSegment range = (JokerNumberRangeSegment) segment;
            v += (range.getMax() - range.getMin() + 1) * 0.1; // wider range = more valuable
        }
        return v;
    }
}
