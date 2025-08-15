package de.mario.roguelette.wheel;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Wheel {
    private final List<Segment> segments;

    public Wheel(final List<Segment> segments) {
        //TODO: this copy process might be expensive
        this.segments = new ArrayList<>(segments);
    }

    public void addSegment(final Segment segment) {
        segments.add(segment);
    }

    public void addSegmentRandom(final Segment segment) {
        int pos = MathUtils.random(0, segments.size());
        segments.add(pos, segment);
    }

    public Segment getSegmentAt(final int index) {
        return segments.get(index);
    }

    public int size() {
        return segments.size();
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public void setSegment(final int index, final Segment segment) {
        segments.set(index, segment);
    }

    public void reset(final List<Segment> newSegments) {
        segments.clear();
        segments.addAll(newSegments);
    }

    public void removeSegmentAt(int index) {
        segments.remove(index);
    }

    public void onTurnChange() {
        for (Segment segment : segments) {
            segment.onTurnChange();
        }
    }
}
