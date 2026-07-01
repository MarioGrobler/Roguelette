package de.mario.roguelette.items;

import de.mario.roguelette.config.RunConfig;
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

    // All tunables (first-shop discount, Segment Remover budget & pricing, restock base price)
    // live in the run's RunConfig — the balance rationale is documented there, and Casino Curses
    // modify them per run without touching this class.
    private final RunConfig runConfig;

    private int currentStage = 1;      // drives rarity stage-gating of generated items
    private int priceMultiplier = 1;   // current stage's price scaling for normal items
    private int deletesThisStage = 0;
    private int deleteBudget;
    private int deleteBaseCost;

    private int restocks;

    // Shop Voucher: while set, restocking costs nothing and doesn't advance the escalating
    // restock counter. Cleared when the shop closes / a new shop stage starts.
    private boolean freeRestocks = false;

    public Shop(final RunConfig runConfig) {
        this.runConfig = runConfig;
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
        this.deleteBudget = runConfig.getDeleteBudget(stage);
        this.deleteBaseCost = runConfig.getDeleteBaseCost(stage);
        this.freeRestocks = false;
        restockItems();
        resetRestocks();
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
        float factor = priceMultiplier * (currentStage == 1 ? runConfig.getFirstShopDiscount() : 1f);
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
        return runConfig.getRestockBase() * (1 << exp) * factor;
    }
}
