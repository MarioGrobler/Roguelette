package de.mario.roguelette.items.chances;

import de.mario.roguelette.GameState;
import de.mario.roguelette.events.GameEventListener;

/**
 * A chance item whose effect lasts for some time after activation. Its effect is expressed
 * through the {@link GameEventListener} hooks (e.g. onResolveBet, onBallLanded).
 */
public abstract class PendingChanceShopItem extends ChanceShopItem implements GameEventListener {

    protected int duration = 1; // duration in rounds

    protected PendingChanceShopItem(ChanceRenderInfo renderInfo) {
        super(renderInfo);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void onActivate(final GameState gameState) {
        gameState.getPendingChanceManager().add(this);
    }

    /**
     * Triggers when the round changes
     */
    public void onTurnChange(final GameState gameState) {
        duration--;
    }
}
