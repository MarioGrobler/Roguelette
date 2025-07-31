package de.mario.roguelette.wheel;

import java.util.ArrayList;
import java.util.List;

public class WheelFactory {
    public static Wheel createClassicWheel() {
        List<Segment> segments = new ArrayList<>();

        for (int i : RouletteRules.getDefaultOrder()) {
            segments.add(new NumberSegment(i, RouletteRules.getStandardColor(i)));
        }

        return new Wheel(segments);
    }
}
