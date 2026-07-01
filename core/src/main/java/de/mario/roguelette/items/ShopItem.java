package de.mario.roguelette.items;

import de.mario.roguelette.GameState;
import de.mario.roguelette.Player;

public abstract class ShopItem {
    protected int cost;
    protected boolean sold = false;
    protected Rarity rarity = Rarity.COMMON; // items override in their constructor, like cost

    public abstract String getShortDescription();
    public abstract String getDescription();
    protected abstract void onBuy(final GameState gameState);

    public boolean canBuy(final Player player) {
        return player.canAfford(getCost(player));
    }

    public boolean tryBuy(final GameState gameState) {
        if (!sold && canBuy(gameState.getPlayer())) {
            gameState.getPlayer().pay(getCost(gameState.getPlayer()));
            onBuy(gameState);
            sold = true;
            return true;
        }
        return false;
    }

    public int getCost() {
        return cost;
    }

    /**
     * The price this player actually pays: the base cost with any player-side discount applied
     * (Bargain Hunter). Every purchase and price display goes through here so discounts take
     * effect the moment the fortune is owned, mid-shop included.
     */
    public int getCost(final Player player) {
        int base = getCost();
        if (base <= 0) {
            return base; // boss rewards etc. stay free
        }
        float factor = de.mario.roguelette.items.fortunes.BargainHunterFortune.discountFactor(player.getInventory());
        return Math.max(1, Math.round(base * factor));
    }

    public boolean isSold() {
        return sold;
    }

    public Rarity getRarity() {
        return rarity;
    }

}
