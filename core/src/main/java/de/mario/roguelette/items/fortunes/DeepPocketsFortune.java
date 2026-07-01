package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * Passive: +2 chance inventory slots while owned. Chance slots only, deliberately — extra fortune
 * slots would let the item enable its own (and every engine's) stacking; chances are consumables,
 * so more of them is tempo, not compounding. No hooks: {@code Inventory.getChanceMaxSize()} counts
 * owned copies live, so discarding the fortune shrinks the capacity again (already-held chances
 * stay, you just can't add beyond the cap).
 */
public class DeepPocketsFortune extends FortuneShopItem {

    public static final int EXTRA_SLOTS_PER_COPY = 2;

    public DeepPocketsFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/deepPockets.png")),
            Color.GOLDENROD, new Color(0.45f, 0.35f, 0.6f, 1f)));
        this.cost = 22;
    }

    @Override
    public String getShortDescription() {
        return "Deep Pockets";
    }

    @Override
    public String getDescription() {
        return "Grants +" + EXTRA_SLOTS_PER_COPY + " chance inventory slots while owned.";
    }
}
