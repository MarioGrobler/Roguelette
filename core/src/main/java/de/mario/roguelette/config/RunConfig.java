package de.mario.roguelette.config;

import de.mario.roguelette.characters.Character;

/**
 * All the knobs that define a run, extracted from the previously hardcoded constants in
 * {@code GameState} (stage targets/rounds, price scaling), {@code Shop} (segment-remover budget
 * and pricing, restock base, first-shop discount) and {@code Character} (starting balance).
 *
 * <p>Built once at run start via {@link #baseline(Character)} — the enabling refactor for
 * <b>Casino Curses</b>: a curse level will take the baseline and apply its modifiers through the
 * setters before the run begins (pricier shops, fewer rounds, higher goals, ...), with no
 * scattered conditionals in the progression code. The baseline values ARE the balance table —
 * change them here, not at the call sites.
 */
public class RunConfig {

    // --- Progression curve ---
    // Stage S (1-indexed) must reach stageTargets[S-1] by the end of its stageRounds[S-1] spins;
    // clearing the last stage (and its boss) wins the run. Gentle stage-1 setup, then a smooth
    // ~3.2x/stage ramp to $1M. Flat 4 spins/stage keeps stages tight: the run is about engineering
    // one big hit per stage, not grinding spins.
    private long[] stageTargets = {150, 500, 1500, 5000, 16000, 55000, 200000, 1_000_000};
    private int[] stageRounds = {4, 4, 4, 4, 4, 4, 4, 4};

    // --- Economy ---
    // Extra discount on the opening (stage 1) shop only: the early game has the least money and
    // the weakest items, so the first shop is meaningfully cheaper to ease the start.
    private float firstShopDiscount = 0.6f;
    // Segment Remover: a per-stage allowance that shrinks to zero (stages 1/2/3 -> 5/3/1, then
    // none) and a base price that doubles on each in-stage use. Removal is an early-game
    // odds-shaping tool; late removal could prune the board into a no-brainer.
    private int[] deleteBudget = {5, 3, 1, 0};
    private int[] deleteBaseCost = {1, 10, 30, 90, 300, 1000, 3000, 10000};
    // Restock price: cheap first reroll, doubling each time, scaled by stage.
    private int restockBase = 3;

    private long startingBalance = 100;

    /** The baseline (un-cursed) run for the given character. */
    public static RunConfig baseline(final Character character) {
        RunConfig config = new RunConfig();
        config.startingBalance = character.getStartingBalance();
        return config;
    }

    /** Clamps a 1-indexed stage into an array (higher stages reuse the last entry). */
    private static int index(int stage, int length) {
        return Math.min(Math.max(stage, 1), length) - 1;
    }

    public int getStageCount() {
        return stageTargets.length;
    }

    public long getStageTarget(int stage) {
        return stageTargets[index(stage, stageTargets.length)];
    }

    public long getFinalGoal() {
        return stageTargets[stageTargets.length - 1];
    }

    public int getStageRounds(int stage) {
        return stageRounds[index(stage, stageRounds.length)];
    }

    /**
     * Shop price scaling per stage: prices follow the target curve (~previous stage's target /100)
     * so an item stays a roughly constant fraction of the bankroll across the run. The opening
     * shop (stage 1) uses base prices.
     */
    public int getPriceMultiplier(int stage) {
        if (stage <= 1) {
            return 1;
        }
        return Math.max(1, Math.round(getStageTarget(stage - 1) / 100f));
    }

    public float getFirstShopDiscount() {
        return firstShopDiscount;
    }

    public int getDeleteBudget(int stage) {
        return deleteBudget[index(stage, deleteBudget.length)];
    }

    public int getDeleteBaseCost(int stage) {
        return deleteBaseCost[index(stage, deleteBaseCost.length)];
    }

    public int getRestockBase() {
        return restockBase;
    }

    public long getStartingBalance() {
        return startingBalance;
    }

    // --- Modifiers (for Casino Curses; applied between baseline() and run start) ---

    public void setStageTargets(long[] stageTargets) {
        this.stageTargets = stageTargets;
    }

    public void setStageRounds(int[] stageRounds) {
        this.stageRounds = stageRounds;
    }

    public void setFirstShopDiscount(float firstShopDiscount) {
        this.firstShopDiscount = firstShopDiscount;
    }

    public void setDeleteBudget(int[] deleteBudget) {
        this.deleteBudget = deleteBudget;
    }

    public void setDeleteBaseCost(int[] deleteBaseCost) {
        this.deleteBaseCost = deleteBaseCost;
    }

    public void setRestockBase(int restockBase) {
        this.restockBase = restockBase;
    }

    public void setStartingBalance(long startingBalance) {
        this.startingBalance = startingBalance;
    }

    /** Scales every stage target by the given factor (a classic curse: "all goals ×k"). */
    public void scaleStageTargets(float factor) {
        long[] scaled = new long[stageTargets.length];
        for (int i = 0; i < stageTargets.length; i++) {
            scaled[i] = Math.max(1, Math.round(stageTargets[i] * (double) factor));
        }
        this.stageTargets = scaled;
    }
}
