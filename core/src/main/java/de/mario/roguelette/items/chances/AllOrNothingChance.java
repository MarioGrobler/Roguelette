package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;

/**
 * One turn of pure aggression: every winning payout is tripled, but losing bets cannot be
 * refunded — Insurance, Safety Net, La Partage and friends are void this turn
 * (via {@link BetResolution#suppressRefunds()}, so listener order doesn't matter). Pairs with a
 * known landing (Freeze, Crystal Ball); punishes a hail-mary.
 */
public class AllOrNothingChance extends PendingChanceShopItem {

    private static final float WIN_MULTIPLIER = 3f;

    public AllOrNothingChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/allOrNothing.png")),
            new Color(0.16f, 0.14f, 0.18f, 1f),  // near-black fill: this is the dangerous one
            new Color(0.95f, 0.75f, 0.15f, 1f),  // gold
            new Color(0.75f, 0.1f, 0.1f, 1f)));  // blood red
        this.cost = 18;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (resolution.isWin()) {
            resolution.multiplyTotal(WIN_MULTIPLIER);
        } else {
            resolution.suppressRefunds();
        }
    }

    @Override
    public String getShortDescription() {
        return "All or Nothing";
    }

    @Override
    public String getDescription() {
        return "For one turn, all winnings are tripled - but losing bets cannot be refunded by anything.";
    }
}
