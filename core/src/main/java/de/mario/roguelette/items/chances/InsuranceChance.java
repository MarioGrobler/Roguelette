package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * While active, refunds the full stake of any losing bet.
 */
public class InsuranceChance extends PendingChanceShopItem {
    public InsuranceChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/insurance.png")),
            Color.WHITE, new Color(0.22f, 0.48f, 0.78f, 1f), new Color(0.8f, 0.9f, 1f, 1f)));
        this.cost = 15;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            resolution.addRefund(1f); // full stake back on a loss
        }
    }

    @Override
    public String getShortDescription() {
        return "Insurance";
    }

    @Override
    public String getDescription() {
        return "Refunds the full stake of any losing bet for one turn.";
    }
}
