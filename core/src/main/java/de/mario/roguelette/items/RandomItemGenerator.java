package de.mario.roguelette.items;

import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.segments.AddSegmentShopItem;
import de.mario.roguelette.items.segments.SegmentShopItem;
import de.mario.roguelette.wheel.JokerColorSegment;
import de.mario.roguelette.wheel.JokerNumberRangeSegment;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

//TODO
public class RandomItemGenerator {
    public List<ChanceShopItem> generateChances() {
        return new ArrayList<>();
    }

    public List<FortuneShopItem> generateFortunes() {
        return new ArrayList<>();
    }

    public List<SegmentShopItem> generateSegments() {
        List<SegmentShopItem> segments = new ArrayList<>();
        NumberSegment ns = new NumberSegment(1, Segment.SegmentColor.BLACK);
        ns.setMultiplier(2f);
        segments.add(new AddSegmentShopItem(ns, 2));

        JokerNumberRangeSegment rs = new JokerNumberRangeSegment(23, 32, Segment.SegmentColor.BOTH);
        rs.setMultiplier(3f);
        segments.add(new AddSegmentShopItem(rs, 10));

        JokerColorSegment js = new JokerColorSegment(Segment.SegmentColor.RED);
        js.setMultiplier(1.5f);
        segments.add(new AddSegmentShopItem(js, 12));

        return segments;
    }
}
