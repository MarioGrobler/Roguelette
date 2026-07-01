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

    // Extra discount applied to the opening (stage 1) shop only: the early game has the least money
    // and the weakest items, so make the first shop's items meaningfully cheaper to ease the start.
    private static final float FIRST_SHOP_DISCOUNT = 0.6f;

    // --- Segment Remover budget & pricing (tunable) ---
    // A per-stage allowance that shrinks over the run: generous early (shape the board's odds --
    // drop the 0, prune a few segments for a >50% wheel) but tight late, where deletion combined
    // with recolour items (Paint It Black, Scarlet Surge) could otherwise assemble a 100%-win
    // "no-brainer" board. Clamps to the last entry for higher stages.
    // Generous early (shape the board's odds), then OFF from stage 4 on: even one removal per late
    // stage lets you prune the last few unwanted segments into a near-trivial board, so removal is a
    // strictly early-game tool now. Stages 1/2/3 -> 5/3/1 removers; stage 4+ -> 0 (none offered).
    private static final int[] DELETE_BUDGET = {5, 3, 1, 0};
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

    // Shop Voucher: while set, restocking costs nothing and doesn't advance the escalating
    // restock counter. Cleared when the shop closes / a new shop stage starts.
    private boolean freeRestocks = false;

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
        this.freeRestocks = false;
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

        if (!freeRestocks) {
            restocks++; // a vouchered restock doesn't advance the price escalation
        }
    }

    /** Shop Voucher: all restocks are free (and un-escalated) until the shop closes. */
    public void setFreeRestocks(boolean freeRestocks) {
        this.freeRestocks = freeRestocks;
    }

    public boolean isRestockFree() {
        return freeRestocks;
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
        float factor = priceMultiplier * (currentStage == 1 ? FIRST_SHOP_DISCOUNT : 1f);
        for (ChanceShopItem item : chances) {
            item.cost = scale(item.cost, factor);
        }
        for (FortuneShopItem item : fortunes) {
            item.cost = scale(item.cost, factor);
        }
        for (SegmentShopItem item : segments) {
            if (!(item instanceof DeleteSegmentShopItem)) {
                item.cost = scale(item.cost, factor);
            }
        }
    }

    private static int scale(int cost, float factor) {
        return Math.max(1, Math.round(cost * factor));
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
