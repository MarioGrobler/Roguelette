package de.mario.roguelette.items;

import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.CrystalBallChance;
import de.mario.roguelette.items.chances.DoubleNextWinChance;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.fortunes.LightningStormFortune;
import de.mario.roguelette.items.fortunes.PaintItBlackFortune;
import de.mario.roguelette.items.fortunes.ScarletSurgeFortune;
import de.mario.roguelette.items.segments.AddSegmentShopItem;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
import de.mario.roguelette.items.segments.SegmentShopItem;
import de.mario.roguelette.wheel.JokerColorSegment;
import de.mario.roguelette.wheel.JokerNumberRangeSegment;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

//TODO
public class RandomItemGenerator {

    private Segment.SegmentColor randomColor() {
        int roll = MathUtils.random(99); // 0 - 99

        // Red: 35%
        // Black: 35%
        // Both: 20%
        // None: 10%
        if (roll < 35) {
            return Segment.SegmentColor.RED;
        }
        if (roll < 70) {
            return Segment.SegmentColor.BLACK;
        }
        if(roll < 90)
            return Segment.SegmentColor.BOTH;

        return Segment.SegmentColor.NONE;
    }

    private AddSegmentShopItem generateSimpleNumberSegmentItem() {
        int number = MathUtils.random(36); // 0 – 36 (!)
        float multiplier = 1f + (MathUtils.random(6) * 0.5f); // 1.0, 1.5, ..., 4.0
        Segment.SegmentColor color = randomColor();

        NumberSegment segment = new NumberSegment(number, color, multiplier);
        int price = (int)(2 + multiplier * 2); // TODO: modify later to match current game progress
        return new AddSegmentShopItem(segment, price);
    }

    private AddSegmentShopItem generateColorJokerSegmentItem() {
        Segment.SegmentColor color = MathUtils.randomBoolean() ? Segment.SegmentColor.RED : Segment.SegmentColor.BLACK;
        float multiplier = 1f + (MathUtils.random(4) * 0.5f); // 1.0 – 3.0

        JokerColorSegment segment = new JokerColorSegment(color, multiplier);
        int price = (int)(8 + multiplier * 3); // TODO: price
        return new AddSegmentShopItem(segment, price);
    }

    private AddSegmentShopItem generateNumberRangeJokerSegmentItem() {
        int rangeMin = 3;
        int rangeMax = 8;
        int rangeSize = MathUtils.random(rangeMin, rangeMax);

        int maxStart = 36 - rangeSize + 1;
        int start = MathUtils.random(maxStart);
        int end = start + rangeSize - 1;

        Segment.SegmentColor color = randomColor();
        float multiplier = 1f + (MathUtils.random(4) * 0.5f); // 1.0 – 3.0

        JokerNumberRangeSegment segment = new JokerNumberRangeSegment(start, end, color, multiplier);

        int price = rangeSize
            + (int)(2 * multiplier)
            + (color == Segment.SegmentColor.BOTH ? 3 : 1)
            + 4; // TODO price

        return new AddSegmentShopItem(segment, price);
    }

    private AddSegmentShopItem generateRandomJokerSegmentItem() {
        if (MathUtils.randomBoolean()) {
            return generateColorJokerSegmentItem();
        } else {
            return generateNumberRangeJokerSegmentItem();
        }
    }


    public List<ChanceShopItem> generateChances() {
        List<ChanceShopItem> chances = new ArrayList<>();

        chances.add(new DoubleNextWinChance());
        chances.add(new CrystalBallChance());
        chances.add(new DoubleNextWinChance());

        return chances;
    }

    public List<FortuneShopItem> generateFortunes() {
        List<FortuneShopItem> fortunes = new ArrayList<>();

        fortunes.add(new LightningStormFortune());
        fortunes.add(new PaintItBlackFortune());
        fortunes.add(new ScarletSurgeFortune());

        return fortunes;
    }

    public List<SegmentShopItem> generateSegments(int deleteSegmentPrice) {
        List<SegmentShopItem> segments = new ArrayList<>();

        segments.add(generateSimpleNumberSegmentItem());
        segments.add(generateRandomJokerSegmentItem());
        segments.add(new DeleteSegmentShopItem(deleteSegmentPrice));

        return segments;
    }
}
