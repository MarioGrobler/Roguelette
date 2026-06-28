package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;

/**
 * Passive: at the end of every round the player earns a flat amount (which grows with the stage) plus
 * a small percentage of their balance. Front-loaded by design — the flat base makes it pay for itself
 * within a stage or two early on, while the small percentage means it fades to near-irrelevance late
 * (when a balance percentage would otherwise be a runaway compounding engine).
 */
public class InterestFortune extends FortuneShopItem {

    private static final float RATE = 0.03f;      // 3% of balance per round (small: weak late on purpose)
    private static final int BASE_PER_STAGE = 10; // flat $ per round, scaling with the current stage

    public InterestFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/interest.png")),
            Color.GOLDENROD, Color.FOREST));
        this.cost = 25;
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        long base = (long) BASE_PER_STAGE * gameState.getCurrentStage();
        long interest = base + Math.round(gameState.getPlayer().getBalance() * (double) RATE);
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
        return "At the end of every round, earn a flat $" + BASE_PER_STAGE + " per stage (more in later"
            + " stages) plus " + Math.round(RATE * 100) + "% of your balance.";
    }
}
