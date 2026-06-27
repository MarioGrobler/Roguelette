package de.mario.roguelette.boss;

import java.util.HashMap;
import java.util.Map;

/**
 * Central mapping of which stages end in a boss fight and which boss appears. Bosses gate the run at
 * the end of stages 2, 4, 6 and 8, escalating in nastiness; stage 8's boss is the final guardian of
 * the $1,000,000 win. A fresh boss instance is built per fight so stateful debuffs start clean.
 */
public final class BossRoster {

    private BossRoster() {}

    /** Whether clearing the given stage's normal rounds triggers a boss fight. */
    public static boolean hasBoss(final int stage) {
        return stage == 2 || stage == 4 || stage == 6 || stage == 8;
    }

    /**
     * Builds a fresh boss for the given stage, or {@code null} if that stage has no boss. The instance
     * is new each call so its debuff listeners (built in {@link Boss#createListeners()}) start fresh.
     */
    public static Boss forStage(final int stage) {
        switch (stage) {
            case 2:  return new TheHouseBoss();
            case 4:  return new TheCrowBoss();
            case 6:  return new TheLeechBoss();
            case 8:  return new TheDevilBoss();
            default: return null;
        }
    }
}
