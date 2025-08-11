package de.mario.roguelette.items.chances;

import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;

/**
 * A chance item whose effect lasts for some time after activation
 */
public abstract class PendingChanceShopItem extends ChanceShopItem {

    protected int duration = 1; // duration in rounds

    protected PendingChanceShopItem(ChanceRenderInfo renderInfo) {
        super(renderInfo);
    }

    /**
     * Returns an additive factor that is added to the given bets bet type base multiplier.
     */
    public abstract float baseModifier(final Bet bet);

    /**
     * Returns a multiplicative factor that is applied at the end
     */
    public abstract float totalModifier(final Bet bet);

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
