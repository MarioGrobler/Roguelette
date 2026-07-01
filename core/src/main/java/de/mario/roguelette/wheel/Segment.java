package de.mario.roguelette.wheel;

import de.mario.roguelette.wheel.effects.ColorModifier;
import de.mario.roguelette.wheel.effects.MultiplierModifier;
import de.mario.roguelette.wheel.effects.SegmentEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Segment {

    protected final List<SegmentEffect> effects = new ArrayList<>();

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

    /**
     * Whether this segment can be removed from the wheel (Segment Remover, the Devil's
     * destroy-segment effect, ...). Cursed segments (e.g. the Devil's Segment) override to false;
     * every wheel-mutation point must honour this.
     */
    public boolean isUnremovable() {
        return false;
    }

    /**
     * Whether permanent recolours (Paint It Black, Scarlet Surge) may target this segment.
     * Cursed segments override to false — painting the Devil's Segment red/black would make it
     * winnable and un-curse it by accident.
     */
    public boolean isRecolorable() {
        return true;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Segment)) return false;
        Segment segment = (Segment) o;
        return Float.compare(multiplier, segment.multiplier) == 0 && color == segment.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, multiplier);
    }

    public void addEffect(SegmentEffect effect) {
        effects.add(effect);
    }

    public void onTurnChange() {
        effects.removeIf(SegmentEffect::onTurnChange);
    }

    public float getCurrentMultiplier() {
        float modifier = multiplier;
        for (SegmentEffect effect : effects) {
            if (effect instanceof MultiplierModifier) {
                modifier += ((MultiplierModifier) effect).modifyMultiplier();
            }
        }
        return modifier;
    }

    public SegmentColor getCurrentColor() {
        SegmentColor baseColor = color;
        for (SegmentEffect effect : effects) {
            if (effect instanceof ColorModifier) {
                baseColor = ((ColorModifier) effect).modifyColor();
            }
        }
        return baseColor;
    }
}
