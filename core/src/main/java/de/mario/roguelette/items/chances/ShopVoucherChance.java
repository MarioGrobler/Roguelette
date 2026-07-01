package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;

/**
 * The long-wanted shop reroll: activated <em>while the shop is open</em>, every restock for the
 * rest of that visit is free — and doesn't advance the escalating restock price counter. Expires
 * when the shop closes ({@code GameState.startNextRound} / the next shop stage).
 *
 * <p>Activated anywhere else, the voucher politely returns itself to the inventory instead of
 * being consumed (the activation flow pops the item first; putting it back mirrors the
 * cancel path of the segment-select chances).
 */
public class ShopVoucherChance extends ChanceShopItem {

    public ShopVoucherChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/shopVoucher.png")),
            Color.WHITE, new Color(0.2f, 0.6f, 0.3f, 1f), new Color(0.95f, 0.75f, 0.15f, 1f)));
        this.cost = 6;
    }

    @Override
    public void onActivate(final GameState gameState) {
        if (gameState.getCurrentState() != GameState.GameStateMode.SHOP_OPEN) {
            // only redeemable at the counter: hand it back instead of burning it
            gameState.getPlayer().getInventory().addChance(this);
            return;
        }
        gameState.getShop().setFreeRestocks(true);
    }

    @Override
    public String getShortDescription() {
        return "Shop Voucher";
    }

    @Override
    public String getDescription() {
        return "Activate while the shop is open: restocking the shop is free until it closes."
            + " (Free restocks don't raise the future restock price either.)";
    }
}
