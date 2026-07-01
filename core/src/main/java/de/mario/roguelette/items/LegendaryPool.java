package de.mario.roguelette.items;

import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.CrystalBallChance;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.fortunes.GoldenTouchFortune;
import de.mario.roguelette.items.fortunes.HotStreakFortune;
import de.mario.roguelette.items.fortunes.SafetyNetFortune;
import de.mario.roguelette.items.fortunes.TwinBallFortune;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The legendary item tier, exclusive to boss rewards. Legendaries no longer appear in the shop (the
 * economy pass removed {@code LEGENDARY} gating from {@code RandomItemGenerator}); a defeated boss
 * instead offers a free pick of {@link #OFFER_SIZE} freshly-built legendaries.
 *
 * <p>Fresh instances are built every draw so stateful fortunes (e.g. {@link HotStreakFortune}) start
 * clean and a single run can pick the same legendary from two different bosses independently.
 */
public final class LegendaryPool {

    /** How many legendaries are offered on a boss defeat (the player picks one). */
    public static final int OFFER_SIZE = 3;

    private LegendaryPool() {}

    /** Builds one fresh instance of every legendary, each tagged {@link Rarity#LEGENDARY}. */
    private static List<ShopItem> all() {
        List<ShopItem> pool = new ArrayList<>();
        pool.add(tag(new GoldenTouchFortune()));
        pool.add(tag(new SafetyNetFortune()));
        pool.add(tag(new HotStreakFortune()));
        pool.add(tag(new TwinBallFortune()));
        pool.add(tag(new CrystalBallChance()));
        return pool;
    }

    private static ShopItem tag(final ShopItem item) {
        item.rarity = Rarity.LEGENDARY;
        return item;
    }

    /** Draws {@link #OFFER_SIZE} distinct legendaries (or all of them, if the pool is smaller). */
    public static List<ShopItem> drawOffer() {
        List<ShopItem> pool = all();
        Collections.shuffle(pool);
        return new ArrayList<>(pool.subList(0, Math.min(OFFER_SIZE, pool.size())));
    }

    /**
     * Grants a chosen legendary to the player's inventory. Returns {@code false} if the relevant
     * inventory section is full (the reward is then lost — the player has 5 slots per section, so this
     * is a corner case).
     */
    public static boolean grant(final GameState gameState, final ShopItem item) {
        if (item instanceof FortuneShopItem) {
            return gameState.getPlayer().getInventory().addFortune((FortuneShopItem) item);
        }
        if (item instanceof ChanceShopItem) {
            return gameState.getPlayer().getInventory().addChance((ChanceShopItem) item);
        }
        return false;
    }
}
