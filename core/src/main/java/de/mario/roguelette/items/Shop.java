package de.mario.roguelette.items;

import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.segments.SegmentShopItem;

import java.util.Collections;
import java.util.List;

public class Shop {
    private List<ChanceShopItem> chances;
    private List<FortuneShopItem> fortunes;
    private List<SegmentShopItem> segments;

    private final RandomItemGenerator randomItemGenerator = new RandomItemGenerator();

    public Shop() {
        refreshItems();
    }

    public void refreshItems() {
        this.chances = randomItemGenerator.generateChances();
        this.fortunes = randomItemGenerator.generateFortunes();
        this.segments = randomItemGenerator.generateSegments();
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

}
