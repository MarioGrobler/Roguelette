package de.mario.roguelette.items;

/**
 * Item power tier. Drives weighted shop generation (rarer = lower {@link #weight}) and a stage gate
 * (an item can't roll before {@link #minStage}), so strong items are both uncommon and absent from
 * the early shops — killing first-shop "no-brainer" rolls.
 *
 * <p>Weights are intentionally a gentle spread (Common ~7x Legendary, not ~16x). LEGENDARY is gated
 * very late for now; once the boss system exists, legendaries are intended to move out of the shop
 * entirely and become boss rewards (pick one of N).
 */
public enum Rarity {
    COMMON(100, 1),
    UNCOMMON(60, 1),
    RARE(32, 3),
    LEGENDARY(15, 5);

    private final int weight;
    private final int minStage;

    Rarity(int weight, int minStage) {
        this.weight = weight;
        this.minStage = minStage;
    }

    public int getWeight() {
        return weight;
    }

    public int getMinStage() {
        return minStage;
    }
}
