package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.items.Inventory;

/**
 * Passive: all shop prices are halved while owned. No listener hooks — the discount is applied
 * centrally in {@code ShopItem.getCost(Player)} (purchase and price display both go through it),
 * so freshly-stocked items, mid-shop purchases and the Segment Remover all honour it immediately.
 * Extra copies deepen the discount sub-linearly, capped well below free.
 */
public class BargainHunterFortune extends FortuneShopItem {

    private static final float BASE_DISCOUNT = 0.5f;      // first copy: -50%
    private static final float EXTRA_PER_COPY = 0.1f;     // each further copy: -10 percentage points
    private static final float MAX_DISCOUNT = 0.7f;       // never beyond -70%

    public BargainHunterFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/bargainHunter.png")),
            Color.GOLDENROD, new Color(0.2f, 0.6f, 0.55f, 1f)));
        this.cost = 20;
    }

    /**
     * @return the price factor (1 = full price) given the player's owned copies. Static so the
     * shop pricing path can query it without holding an item reference.
     */
    public static float discountFactor(final Inventory inventory) {
        int copies = inventory.countFortunes(BargainHunterFortune.class);
        if (copies <= 0) {
            return 1f;
        }
        float discount = Math.min(MAX_DISCOUNT, BASE_DISCOUNT + EXTRA_PER_COPY * (copies - 1));
        return 1f - discount;
    }

    @Override
    public String getShortDescription() {
        return "Bargain Hunter";
    }

    @Override
    public String getDescription() {
        return "All shop prices are reduced by " + Math.round(BASE_DISCOUNT * 100) + "% (+"
            + Math.round(EXTRA_PER_COPY * 100) + "% per extra copy, up to "
            + Math.round(MAX_DISCOUNT * 100) + "%).";
    }
}
