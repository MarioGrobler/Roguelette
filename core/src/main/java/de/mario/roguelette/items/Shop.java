package de.mario.roguelette.items;

import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
import de.mario.roguelette.items.segments.SegmentShopItem;

import java.util.Collections;
import java.util.List;

public class Shop {
    private List<ChanceShopItem> chances;
    private List<FortuneShopItem> fortunes;
    private List<SegmentShopItem> segments;

    private final RandomItemGenerator randomItemGenerator = new RandomItemGenerator();

    private int numOfSoldDeletes = 0;
    private int restocks;

    public Shop() {
        restockItems();
        restocks = 0;
    }

    /**
     * Restocks the
     */
    public void reset() {
        restockItems();
        resetRestocks();
    }

    public void restockItems() {
        this.chances = randomItemGenerator.generateChances();
        this.fortunes = randomItemGenerator.generateFortunes();
        this.segments = randomItemGenerator.generateSegments(1 << numOfSoldDeletes);

        restocks++;
    }

    public List<ChanceShopItem> getChances() {
        return Collections.unmodifiableList(chances);
    }

    public List<FortuneShopItem> getFortunes() {
        return Collections.unmodifiableList(fortunes);
    }

    public List<SegmentShopItem> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public int getNumOfSoldDeletes() {
        return numOfSoldDeletes;
    }

    public int getRestocks() {
        return restocks;
    }

    public void resetRestocks() {
        restocks = 0;
    }

    public void increaseNumOfSoldDeletes() {
        numOfSoldDeletes++;
    }

    /**
     * Increases all the prices by the given factor except the DeleteSegment
     */
    public void updatePrices(int factor) {
        for (ChanceShopItem item : chances) {
            item.cost *= factor;
        }
        for (FortuneShopItem item : fortunes) {
            item.cost *= factor;
        }
        for (SegmentShopItem item : segments) {
            if (!(item instanceof DeleteSegmentShopItem)) { // skip delete segment
                item.cost *= factor;
            }
        }
    }

    public int getRestockPrice(int factor) {
        // unscaled price: 2 + num of restocks
        return (2 + restocks) * factor;
    }
}
