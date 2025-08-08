package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.Bet;


public class DoubleNextWinChance extends ChanceShopItem {

    public DoubleNextWinChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/doubleNextWin.png")), Color.BLACK, Color.GOLDENROD, Color.BLACK));
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
    protected void onBuy(GameState gameState) {
        gameState.getPlayer().getInventory().addChance(this);

        //TODO for now, simply activate them directly
        //TODO items with pending and non-pending effects should probably be handled differently
        gameState.activeChance(0);
    }

    @Override
    public float baseModifer(Bet bet) {
        return 0;
    }

    @Override
    public float totalModifer(Bet bet) {
        return 2;
    }
}
