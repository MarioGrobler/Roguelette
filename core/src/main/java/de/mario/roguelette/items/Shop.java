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

    // --- Segment Remover budget & pricing (tunable) ---
    // A per-stage allowance that shrinks over the run: generous early (shape the board's odds --
    // drop the 0, prune a few segments for a >50% wheel) but tight late, where deletion combined
    // with recolour items (Paint It Black, Scarlet Surge) could otherwise assemble a 100%-win
    // "no-brainer" board. Clamps to the last entry for higher stages.
    private static final int[] DELETE_BUDGET = {5, 3, 1};
    // Base price of the FIRST remover in a stage; doubles on each use within the stage. Cheap early
    // (early odds-shaping is intended), scaling up so late removals cost real money on top of being
    // rationed.
    private static final int[] DELETE_BASE_COST = {1, 10, 30, 90, 300, 1000, 3000, 10000};

    // Restocks aren't hard-capped (rarity stage-gating already prevents fishing the shop for a
    // game-breaker). Instead the price escalates geometrically: a cheap first reroll that quickly
    // becomes a real money sink, so rerolling for synergy is a genuine economic decision.
    private static final int RESTOCK_BASE = 3;

    private int currentStage = 1;      // drives rarity stage-gating of generated items
    private int priceMultiplier = 1;   // current stage's price scaling for normal items
    private int deletesThisStage = 0;
    private int deleteBudget = DELETE_BUDGET[0];
    private int deleteBaseCost = DELETE_BASE_COST[0];

    private int restocks;

    public Shop() {
        startStage(1, 1);
    }

    /**
     * Opens the shop for the given stage: sets the price scaling and the (decreasing) Segment
     * Remover allowance/base price for the stage, then stocks fresh items. Used for the opening
     * shop (stage 1) and at the start of every later shop phase.
     */
    public void startStage(int stage, int priceMultiplier) {
        this.currentStage = stage;
        this.priceMultiplier = priceMultiplier;
        this.deletesThisStage = 0;
        this.deleteBudget = pick(DELETE_BUDGET, stage);
        this.deleteBaseCost = pick(DELETE_BASE_COST, stage);
        restockItems();
        resetRestocks();
    }

    private static int pick(int[] arr, int stage) {
        return arr[Math.min(Math.max(stage, 1), arr.length) - 1];
    }

    public void restockItems() {
        this.chances = randomItemGenerator.generateChances(currentStage);
        this.fortunes = randomItemGenerator.generateFortunes(currentStage);
        this.segments = randomItemGenerator.generateSegments(currentDeletePrice(), canDelete());

        applyPriceMultiplier(); // scale on every (re)stock so the Restock button keeps prices correct

        restocks++;
    }

    /** @return whether another Segment Remover may still be offered/bought this stage. */
    public boolean canDelete() {
        return deletesThisStage < deleteBudget;
    }

    /** @return how many Segment Removers remain this stage (for the shop's removal-count badge). */
    public int getRemainingDeletes() {
        return Math.max(0, deleteBudget - deletesThisStage);
    }

    /** @return price of the next Segment Remover this stage (base price doubling on each use). */
    public int currentDeletePrice() {
        return deleteBaseCost * (1 << deletesThisStage);
    }

    /** Records a Segment Remover purchase toward this stage's budget. */
    public void registerDelete() {
        deletesThisStage++;
    }

    /** Scales the prices of normal items by the current stage multiplier (delete is priced separately). */
    private void applyPriceMultiplier() {
        for (ChanceShopItem item : chances) {
            item.cost *= priceMultiplier;
        }
        for (FortuneShopItem item : fortunes) {
            item.cost *= priceMultiplier;
        }
        for (SegmentShopItem item : segments) {
            if (!(item instanceof DeleteSegmentShopItem)) {
                item.cost *= priceMultiplier;
            }
        }
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

    public int getRestocks() {
        return restocks;
    }

    public void resetRestocks() {
        restocks = 0;
    }

    public int getRestockPrice(int factor) {
        // Cheap first reroll, doubling each time (a self-limiting money sink), scaled by the stage so
        // it stays meaningful late. Exponent clamped to avoid int overflow at absurd restock counts.
        int exp = Math.min(restocks, 15);
        return RESTOCK_BASE * (1 << exp) * factor;
    }
}
