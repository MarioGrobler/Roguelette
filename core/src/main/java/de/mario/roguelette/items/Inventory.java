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
        this(5,4);
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
