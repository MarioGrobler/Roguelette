package de.mario.roguelette.items;

import de.mario.roguelette.GameState;
import de.mario.roguelette.Player;

public abstract class ShopItem {
    protected int cost;
    protected boolean sold = false;

    public abstract String getShortDescription();
    public abstract String getDescription();
    protected abstract void onBuy(final GameState gameState);

    public boolean canBuy(final Player player) {
        return player.canAfford(getCost());
    }

    public boolean tryBuy(final GameState gameState) {
        if (!sold && canBuy(gameState.getPlayer())) {
            gameState.getPlayer().pay(getCost());
            onBuy(gameState);
            sold = true;
            return true;
        }
        return false;
    }

    public int getCost() {
        return cost;
    }

    public boolean isSold() {
        return sold;
    }

}
