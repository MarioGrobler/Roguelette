package de.mario.roguelette.curses;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * Sub-curse (tier 2): the house skims a cut of every winning payout. Multiplicative, so listener
 * order doesn't matter; winning bets are still floored at their stake in {@code Bet.getPayout}, so
 * a win never becomes a net loss.
 */
public class HouseEdgeCurse extends Curse {

    private static final float WIN_FACTOR = 0.9f;

    @Override
    public String getName() {
        return "House Edge";
    }

    @Override
    public String getDescription() {
        return "The house skims " + Math.round((1f - WIN_FACTOR) * 100) + "% of every winning payout.";
    }

    @Override
    public int getTier() {
        return 2;
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new GameEventListener() {
            @Override
            public void onResolveBet(final GameState gameState, final BetResolution resolution) {
                if (resolution.isWin()) {
                    resolution.multiplyTotal(WIN_FACTOR);
                }
            }
        });
    }
}
