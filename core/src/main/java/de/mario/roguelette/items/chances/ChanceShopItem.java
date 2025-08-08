package de.mario.roguelette.items.chances;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.items.ShopItem;


public abstract class ChanceShopItem extends ShopItem {

    public static class ChanceRenderInfo {
        private final Texture backgrund;
        private final Color backgrundColor;
        private final Color borderColor1;
        private final Color borderColor2;

        public ChanceRenderInfo(Texture backgrund, Color backgrundColor, Color cornerCorner1, Color cornerCorner2) {
            this.backgrund = backgrund;
            this.backgrundColor = backgrundColor;
            this.borderColor1 = cornerCorner1;
            this.borderColor2 = cornerCorner2;
        }

        public Texture getBackgrund() {
            return backgrund;
        }

        public Color getBackgrundColor() {
            return backgrundColor;
        }

        public Color getBorderColor1() {
            return borderColor1;
        }

        public Color getBorderColor2() {
            return borderColor2;
        }
    }

    // having this here might b look a bit inconvenient as this mixes up rendering and logic
    // however, the flexibility and scalability are a big advantage (and nothing is actually rendered, so it is fine)
    private final ChanceRenderInfo renderInfo;

    protected ChanceShopItem(ChanceRenderInfo renderInfo) {
        this.renderInfo = renderInfo;
    }

    /**
     * Returns an additive factor that is added to the given bets bet type base multiplier.
     */
    public abstract float baseModifer(final Bet bet);

    /**
     * Returns a multiplicative factor that is applied at the end
     */
    public abstract float totalModifer(final Bet bet);

    public ChanceRenderInfo getRenderInfo() {
        return renderInfo;
    }
}
