package de.mario.roguelette.items;

import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO ...
public class Inventory {
    private final List<FortuneShopItem> fortunes = new ArrayList<>();
    private final List<ChanceShopItem> chances = new ArrayList<>();
    // no need for segment items as they are consumed immediately

    private final int fortuneMaxSize;
    private final int chanceMaxSize;

    public Inventory() {
        this(5,5);
    }

    public Inventory(int fortuneSize, int chanceSize) {
        this.fortuneMaxSize = fortuneSize;
        this.chanceMaxSize = chanceSize;
    }

    public boolean addChance(final ChanceShopItem chance) {
        if (chancesFull()) {
            return false;
        }
        return chances.add(chance);
    }

    public boolean addFortune(final FortuneShopItem fortune) {
        if (fortunesFull()) {
            return false;
        }
        return fortunes.add(fortune);
    }

    public ChanceShopItem popChanceAtIndex(int index) {
        ChanceShopItem chance = chances.get(index);
        chances.remove(index);
        return chance;
    }

    public FortuneShopItem popFortuneAtIndex(int index) {
        FortuneShopItem fortune = fortunes.get(index);
        fortunes.remove(index);
        return fortune;
    }

    /** @return how many owned fortunes are of exactly the given type (for explicit stacking). */
    public int countFortunes(final Class<? extends FortuneShopItem> type) {
        int n = 0;
        for (FortuneShopItem f : fortunes) {
            if (f.getClass() == type) {
                n++;
            }
        }
        return n;
    }

    /**
     * @return whether {@code item} is the <em>primary</em> (first-owned) copy of its type. Stack-aware
     * fortunes let only their primary copy apply the shared, count-scaled effect, so the other copies
     * don't multiply it; see e.g. {@code PaintItBlackFortune}.
     */
    public boolean isPrimaryFortune(final FortuneShopItem item) {
        for (FortuneShopItem f : fortunes) {
            if (f.getClass() == item.getClass()) {
                return f == item;
            }
        }
        return false;
    }

    public boolean chancesFull() {
        return chances.size() >= chanceMaxSize;
    }

    public boolean fortunesFull() {
        return fortunes.size() >= fortuneMaxSize;
    }

    public List<ChanceShopItem> getChances() {
        return Collections.unmodifiableList(chances);
    }

    public List<FortuneShopItem> getFortunes() {
        return Collections.unmodifiableList(fortunes);
    }

    public int getFortuneMaxSize() {
        return fortuneMaxSize;
    }

    public int getChanceMaxSize() {
        return chanceMaxSize;
    }
}
