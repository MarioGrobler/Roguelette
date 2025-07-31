package de.mario.roguelette.wheel;

import java.util.*;

/** Classical Roulette Rules */
public class RouletteRules {
    private static final Map<Integer, Segment.SegmentColor> STANDARD_COLORS = new HashMap<>();
    private static final List<Integer> defaultOrder; //starting at 0

    // Java 8, Map.ofEntries is not available
    static {
        STANDARD_COLORS.put(0, Segment.SegmentColor.NONE); // Zero
        int[] reds = {1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36};
        int[] blacks = {2,4,6,8,10,11,13,15,17,20,22,24,26,28,29,31,33,35};
        for (int r : reds) {
            STANDARD_COLORS.put(r, Segment.SegmentColor.RED);
        }
        for (int b : blacks) {
            STANDARD_COLORS.put(b, Segment.SegmentColor.BLACK);
        }

        Integer[] order = {0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5, 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26};
        defaultOrder = Arrays.asList(order);
    }

    public static Segment.SegmentColor getStandardColor(int number) {
        return STANDARD_COLORS.getOrDefault(number, Segment.SegmentColor.NONE);
    }

    public static boolean isNumberBlack(int number) {
        return getStandardColor(number) == Segment.SegmentColor.BLACK;
    }

    public static boolean isNumberRed(int number) {
        return getStandardColor(number) == Segment.SegmentColor.RED;
    }

    public static List<Integer> getDefaultOrder() {
        return Collections.unmodifiableList(defaultOrder);
    }
}
