package de.mario.roguelette.items;

import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.DoubleBallChance;
import de.mario.roguelette.items.chances.DoubleNextWinChance;
import de.mario.roguelette.items.chances.FreezeChance;
import de.mario.roguelette.items.chances.InsuranceChance;
import de.mario.roguelette.items.chances.LuckySevenChance;
import de.mario.roguelette.items.chances.MirrorFateChance;
import de.mario.roguelette.items.chances.RicochetChance;
import de.mario.roguelette.items.fortunes.ComebackKidFortune;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.fortunes.InterestFortune;
import de.mario.roguelette.items.fortunes.LightningStormFortune;
import de.mario.roguelette.items.fortunes.PaintItBlackFortune;
import de.mario.roguelette.items.fortunes.ScarletSurgeFortune;
import de.mario.roguelette.items.fortunes.StreakBonusFortune;
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


    private static final int CHANCES_PER_RESTOCK = 3;

    public List<ChanceShopItem> generateChances(int stage) {
        List<ChanceShopItem> pool = new ArrayList<>();

        pool.add(rar(new DoubleNextWinChance(), Rarity.UNCOMMON));
        // Crystal Ball is now a boss-exclusive legendary (see LegendaryPool); no longer in the shop.
        pool.add(rar(new MirrorFateChance(), Rarity.UNCOMMON));
        pool.add(rar(new InsuranceChance(), Rarity.COMMON));
        pool.add(rar(new LuckySevenChance(), Rarity.UNCOMMON));
        pool.add(rar(new RicochetChance(), Rarity.COMMON));
        pool.add(rar(new FreezeChance(), Rarity.RARE));
        pool.add(rar(new DoubleBallChance(), Rarity.RARE));

        return pickWeighted(pool, CHANCES_PER_RESTOCK, stage);
    }

    private static final int FORTUNES_PER_RESTOCK = 3;

    public List<FortuneShopItem> generateFortunes(int stage) {
        List<FortuneShopItem> pool = new ArrayList<>();

        pool.add(rar(new LightningStormFortune(), Rarity.RARE));
        pool.add(rar(new PaintItBlackFortune(), Rarity.RARE));
        pool.add(rar(new ScarletSurgeFortune(), Rarity.RARE));
        pool.add(rar(new InterestFortune(), Rarity.UNCOMMON));
        pool.add(rar(new ComebackKidFortune(), Rarity.COMMON));
        pool.add(rar(new StreakBonusFortune(), Rarity.UNCOMMON));

        return pickWeighted(pool, FORTUNES_PER_RESTOCK, stage);
    }

    /**
     * Draws up to {@code count} items from the pool, weighted by rarity and without replacement, and
     * excluding items gated above the current {@code stage}. Stronger (rarer) items are both less
     * likely and absent from early shops.
     */
    /** Tags an item with its rarity (set centrally here so the whole balance table lives in one place). */
    private <T extends ShopItem> T rar(T item, Rarity rarity) {
        item.rarity = rarity;
        return item;
    }

    private <T extends ShopItem> List<T> pickWeighted(List<T> pool, int count, int stage) {
        List<T> eligible = new ArrayList<>();
        for (T item : pool) {
            if (item.getRarity().getMinStage() <= stage) {
                eligible.add(item);
            }
        }

        List<T> result = new ArrayList<>();
        while (result.size() < count && !eligible.isEmpty()) {
            int total = 0;
            for (T item : eligible) {
                total += item.getRarity().getWeight();
            }
            int roll = MathUtils.random(total - 1);
            int acc = 0;
            T chosen = eligible.get(eligible.size() - 1);
            for (T item : eligible) {
                acc += item.getRarity().getWeight();
                if (roll < acc) {
                    chosen = item;
                    break;
                }
            }
            result.add(chosen);
            eligible.remove(chosen);
        }
        return result;
    }

    public List<SegmentShopItem> generateSegments(int deleteSegmentPrice, boolean includeDelete) {
        List<SegmentShopItem> segments = new ArrayList<>();

        segments.add(generateSimpleNumberSegmentItem());
        segments.add(generateRandomJokerSegmentItem());
        if (includeDelete) {
            segments.add(new DeleteSegmentShopItem(deleteSegmentPrice));
        } else {
            // Delete cap reached this run: offer another wheel-add in the slot instead.
            segments.add(generateRandomJokerSegmentItem());
        }

        return segments;
    }
}
