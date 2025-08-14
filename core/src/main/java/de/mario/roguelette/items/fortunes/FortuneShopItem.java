package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.items.ShopItem;

public abstract class FortuneShopItem extends ShopItem {

    public static class FortuneRenderInfo {
        private final Texture backgrund;
        private final Color backgrundColor;
        private final Color borderColor;


        public FortuneRenderInfo(Texture backgrund, Color backgrundColor, Color borderColor) {
            this.backgrund = backgrund;
            this.backgrundColor = backgrundColor;
            this.borderColor = borderColor;
        }

        public Texture getBackgrund() {
            return backgrund;
        }

        public Color getBackgrundColor() {
            return backgrundColor;
        }

        public Color getBorderColor() {
            return borderColor;
        }
    }

    private final FortuneRenderInfo renderInfo;

    public FortuneShopItem(FortuneRenderInfo renderInfo) {
        this.renderInfo = renderInfo;
    }

    public FortuneRenderInfo getRenderInfo() {
        return renderInfo;
    }

    @Override
    public boolean tryBuy(final GameState gameState) {
        if (!sold && canBuy(gameState.getPlayer()) && !gameState.getPlayer().getInventory().fortunesFull()) {
            gameState.getPlayer().pay(getCost());
            onBuy(gameState);
            sold = true;
            return true;
        }
        return false;
    }

    @Override
    protected void onBuy(final GameState gameState) {
        gameState.getPlayer().getInventory().addFortune(this);
    }

    public abstract void onTurnChange(final GameState gameState);

    /**
     * Returns an additive factor that is added to the given bets bet type base multiplier.
     */
    public abstract float baseModifier(final Bet bet);

    /**
     * Returns a multiplicative factor that is applied at the end
     */
    public abstract float totalModifier(final Bet bet);
}
