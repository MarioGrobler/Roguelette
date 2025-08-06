package de.mario.roguelette.items;

import de.mario.roguelette.GameState;
import de.mario.roguelette.Player;

public abstract class ShopItem {
    public abstract String getShortDescription();
    public abstract String getDescription();
    public abstract int getCost();
    protected abstract void onBuy(final GameState gameState);

    public boolean canBuy(final Player player) {
        return player.canAfford(getCost());
    }

    public boolean tryBuy(final GameState gameState) {
        if (canBuy(gameState.getPlayer())) {
            gameState.getPlayer().pay(getCost());
            onBuy(gameState);
            return true;
        }
        return false;
    }

}
