package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;

/**
 * Passive: at the end of every round the player earns a percentage of their current balance as
 * interest. Exercises the {@link de.mario.roguelette.events.GameEventListener#onTurnChange} hook
 * on the fortune side.
 */
public class InterestFortune extends FortuneShopItem {

    private static final float RATE = 0.05f; // 5% of balance per round

    public InterestFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/interest.png")),
            Color.GOLDENROD, Color.FOREST));
        this.cost = 25;
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        int interest = Math.round(gameState.getPlayer().getBalance() * RATE);
        if (interest > 0) {
            gameState.getPlayer().earn(interest);
        }
    }

    @Override
    public String getShortDescription() {
        return "Interest";
    }

    @Override
    public String getDescription() {
        return "At the end of every round, earn " + Math.round(RATE * 100) + "% of your balance as interest.";
    }
}
