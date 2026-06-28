package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.wheel.NumberSegment;

/**
 * While active, landing on any number containing a 7 (7, 17, 27) triples all payouts. Broadened from
 * just the single 7 so the chance is far less situational.
 */
public class LuckySevenChance extends PendingChanceShopItem {
    public LuckySevenChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/luckySeven.png")),
            Color.WHITE, Color.GOLDENROD, new Color(0.6f, 0.15f, 0.12f, 1f)));
        this.cost = 12;
    }

    private boolean isLuckySeven(final NumberSegment segment) {
        int n = segment.getCurrentNumber();
        return n == 7 || n == 17 || n == 27;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin()
            && resolution.getLanded() instanceof NumberSegment
            && isLuckySeven((NumberSegment) resolution.getLanded())) {
            resolution.multiplyTotal(3f);
        }
    }

    @Override
    public String getShortDescription() {
        return "Lucky Seven";
    }

    @Override
    public String getDescription() {
        return "While active, landing on a 7, 17 or 27 triples all winning payouts. Lasts for one turn.";
    }
}
