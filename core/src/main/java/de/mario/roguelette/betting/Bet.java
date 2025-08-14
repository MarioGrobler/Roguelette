package de.mario.roguelette.betting;

import de.mario.roguelette.GameState;
import de.mario.roguelette.items.chances.PendingChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.wheel.Segment;

public class Bet {
    private final BetType betType;
    private int amount;

    public Bet(BetType betType, int amount) {
        this.betType = betType;
        this.amount = amount;
    }

    public boolean isWin(final Segment landed) {
        return betType.isWinningSegment(landed);
    }

    /**
     * @return the payout for this bet according to the following formula:
     * amount * (base multiplier + chance base modifiers) * segment multiplier * chance total modifiers
     */
    public float getPayout(final Segment landed, final GameState gameState) {
        if (isWin(landed)) {
            // compute all payout modifiers
            float base = betType.getPayoutMultiplier();
            float totalMultiplier = 1f;
            for (FortuneShopItem fortune : gameState.getPlayer().getInventory().getFortunes()) {
                base += fortune.baseModifier(this);
                totalMultiplier *= fortune.totalModifier(this);
            }
            for (PendingChanceShopItem chance : gameState.getPendingChanceManager().getActiveChances()) {
                base += chance.baseModifier(this);
                totalMultiplier *= chance.totalModifier(this);
            }

            return amount * base * landed.getMultiplier() * totalMultiplier;
        }
        return 0;
    }

    public BetType getBetType() {
        return betType;
    }

    public int getAmount() {
        return amount;
    }

    public void increaseAmount(int by) {
        this.amount += by;
    }


}
