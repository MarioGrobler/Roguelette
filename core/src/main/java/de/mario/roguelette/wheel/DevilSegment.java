package de.mario.roguelette.wheel;

/**
 * The house's thumb on the scale (Casino Curses, "The Devil's Mark"): a segment that <b>never
 * wins</b> and <b>never leaves</b>. It carries no number and stays colourless, so no bet type can
 * match it; {@link #isUnremovable()} blocks the Segment Remover and the Devil boss's
 * destroy-segment effect, and {@link #isRecolorable()} keeps Paint It Black / Scarlet Surge from
 * accidentally making it winnable.
 *
 * <p>One deliberate out: it IS colourless, so the <em>Devil's Due</em> chance pays its 10x jackpot
 * when the ball lands here — the curse creates the market for the pact.
 */
public class DevilSegment extends Segment {

    public DevilSegment() {
        super(SegmentColor.NONE, 1f);
    }

    @Override
    public boolean isUnremovable() {
        return true;
    }

    @Override
    public boolean isRecolorable() {
        return false;
    }

    @Override
    public String getDisplayText() {
        return "666";
    }

    @Override
    public String getShortDescription() {
        return "Devil's Segment";
    }

    @Override
    public String getDescription() {
        return "The Devil's Segment. No bet ever wins here, and it cannot be removed.";
    }
}
