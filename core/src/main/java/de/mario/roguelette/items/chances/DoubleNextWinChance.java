package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;


public class DoubleNextWinChance extends PendingChanceShopItem {

    public DoubleNextWinChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/doubleNextWin.png")), Color.BLACK, Color.GOLDENROD, Color.BLACK));
        this.cost = 20;
    }

    @Override
    public String getShortDescription() {
        return "Double Trouble";
    }

    @Override
    public String getDescription() {
        return "Doubles the next win.";
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin()) {
            resolution.multiplyTotal(2f);
        }
    }
}
