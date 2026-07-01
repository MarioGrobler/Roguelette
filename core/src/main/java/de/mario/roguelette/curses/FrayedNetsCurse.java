package de.mario.roguelette.curses;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * Sub-curse (tier 2): every refund effect (Insurance, Safety Net, La Partage, ...) returns only
 * half. Uses {@link BetResolution#multiplyRefund} so it applies regardless of listener order and
 * however many refund sources stack.
 */
public class FrayedNetsCurse extends Curse {

    private static final float REFUND_FACTOR = 0.5f;

    @Override
    public String getName() {
        return "Frayed Nets";
    }

    @Override
    public String getDescription() {
        return "All refunds on losing bets (Insurance, Safety Net, La Partage, ...) are halved.";
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
                if (!resolution.isWin()) {
                    resolution.multiplyRefund(REFUND_FACTOR);
                }
            }
        });
    }
}
