package de.mario.roguelette.items;

import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;

import java.util.ArrayList;
import java.util.List;

//TODO ...
public class Inventory {
    private final List<ChanceShopItem> chances = new ArrayList<>();
    private final List<FortuneShopItem> fortunes = new ArrayList<>();
    // no need for segment items as they are consumed immediately

    public void addChance(final ChanceShopItem chance) {
        chances.add(chance);
    }

    public void addFortune(final FortuneShopItem fortune) {
        fortunes.add(fortune);
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
}
