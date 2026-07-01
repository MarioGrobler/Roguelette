package de.mario.roguelette.curses;

import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Casino-Curses ladder: 8 levels (+level 0 = clean run), <b>strictly additive</b> — each level
 * is the previous plus exactly one curse, so difficulty is monotone by construction. Three main
 * curses land as structural beats at levels 1/4/7; the other levels each add one random sub-curse
 * drawn from a pool that is tier-gated by level (≤3: tier 1 only, 4–6: up to tier 2, 7+: tier 3).
 *
 * <pre>
 * L1 M1 | L2 +sub | L3 +sub | L4 +M2 | L5 +sub | L6 +sub | L7 +M3 | L8 +sub
 * </pre>
 *
 * Draws are without replacement and honour {@link Curse#getCategory()} (max one per category, so
 * e.g. the two goal-scalers never stack). Draws are random per run for now — reproducible seeded
 * draws (daily challenge) need a run-seed system first.
 */
public final class CurseLevels {

    public static final int MAX_LEVEL = 8;

    /** How many random sub-curses each level carries (index = level 0..8). */
    private static final int[] SUB_COUNT = {0, 0, 1, 2, 2, 3, 4, 4, 5};

    private CurseLevels() {
    }

    /** The always-on main curses active at the given level (levels 1/4/7 add one each). */
    public static List<Curse> mains(int level) {
        List<Curse> mains = new ArrayList<>();
        if (level >= 1) {
            mains.add(new DevilsMarkCurse());
        }
        if (level >= 4) {
            mains.add(new DevilsHarvestCurse());
        }
        if (level >= 7) {
            mains.add(new ConfigCurse("Borrowed Against Time",
                "Every stage has one fewer round.", 3, null,
                config -> config.adjustStageRounds(-1)));
        }
        return mains;
    }

    /** Fresh instances of the whole sub-curse pool. */
    private static List<Curse> subPool() {
        List<Curse> pool = new ArrayList<>();
        // --- tier 1 ---
        pool.add(new ConfigCurse("Inflation",
            "All shop prices are raised by 25%.", 1, "prices",
            config -> config.setShopPriceFactor(config.getShopPriceFactor() * 1.25f)));
        pool.add(new ConfigCurse("No Welcome Bonus",
            "The opening shop's discount is gone.", 1, null,
            config -> config.setFirstShopDiscount(1f)));
        pool.add(new ConfigCurse("Reroll Racket",
            "Restocking the shop costs three times as much.", 1, null,
            config -> config.setRestockBase(config.getRestockBase() * 3)));
        pool.add(new ConfigCurse("Thin Wallet",
            "You start the run with 25% less money.", 1, null,
            config -> config.setStartingBalance(Math.max(1, config.getStartingBalance() * 3 / 4))));
        pool.add(new ConfigCurse("Dull Knife",
            "The Segment Remover allowance is halved every stage.", 1, null,
            config -> config.scaleDeleteBudget(0.5f)));
        // --- tier 2 ---
        pool.add(new ConfigCurse("Greedy Goals",
            "Every stage goal is raised by 25%.", 2, "goals",
            config -> config.scaleStageTargets(1.25f)));
        pool.add(new ConfigCurse("Impatient Bosses",
            "Boss fights grant one fewer spin.", 2, null,
            config -> config.setBossSpinDelta(config.getBossSpinDelta() - 1)));
        pool.add(new HouseEdgeCurse());
        pool.add(new FrayedNetsCurse());
        // --- tier 3 ---
        pool.add(new ConfigCurse("Crushing Goals",
            "Every stage goal is raised by 50%.", 3, "goals",
            config -> config.scaleStageTargets(1.5f)));
        return pool;
    }

    private static int maxTier(int level) {
        if (level >= 7) {
            return 3;
        }
        return level >= 4 ? 2 : 1;
    }

    /**
     * Assembles the active curses for a run at the given level: the level's mains plus its number
     * of randomly drawn sub-curses (no duplicates, max one per category).
     */
    public static List<Curse> rollForLevel(int level) {
        List<Curse> active = new ArrayList<>(mains(level));
        if (level <= 0) {
            return active;
        }

        List<Curse> pool = new ArrayList<>();
        for (Curse curse : subPool()) {
            if (curse.getTier() <= maxTier(level)) {
                pool.add(curse);
            }
        }

        int toDraw = SUB_COUNT[Math.min(Math.max(level, 0), MAX_LEVEL)];
        Set<String> usedCategories = new HashSet<>();
        while (toDraw > 0 && !pool.isEmpty()) {
            Curse drawn = pool.remove(MathUtils.random(pool.size() - 1));
            if (drawn.getCategory() != null && !usedCategories.add(drawn.getCategory())) {
                continue; // category already active (e.g. a second goal-scaler): skip, draw again
            }
            active.add(drawn);
            toDraw--;
        }
        return active;
    }

    /** One-line summary for the level-select UI. */
    public static String describeLevel(int level) {
        if (level <= 0) {
            return "A clean run. The house plays fair... mostly.";
        }
        StringBuilder sb = new StringBuilder();
        List<Curse> mains = mains(level);
        for (int i = 0; i < mains.size(); i++) {
            if (i > 0) {
                sb.append(" + ");
            }
            sb.append(mains.get(i).getName());
        }
        int subs = SUB_COUNT[Math.min(level, MAX_LEVEL)];
        if (subs > 0) {
            sb.append(" + ").append(subs).append(subs == 1 ? " random curse" : " random curses");
        }
        return sb.toString();
    }
}
