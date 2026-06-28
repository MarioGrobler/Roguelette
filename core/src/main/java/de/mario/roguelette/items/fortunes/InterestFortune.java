package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;

/**
 * Passive: at the end of every round the player earns a flat amount (growing with the stage) plus a
 * small percentage of their balance — but the percentage is taken on the balance <em>capped at the
 * current stage goal</em>. That cap is the key: without it, a rich player earns runaway interest and
 * can clear stages (and even boss gain-goals) by doing nothing. Capping the percentage base at the
 * stage goal makes interest a fixed, stage-scaled trickle regardless of how far you have overshot, so
 * it can never become a passive autowin engine.
 */
public class InterestFortune extends FortuneShopItem {

    private static final float RATE = 0.03f;      // 3% per round, taken on min(balance, stage goal)
    private static final int BASE_PER_STAGE = 10; // flat $ per round, scaling with the current stage

    public InterestFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/interest.png")),
            Color.GOLDENROD, Color.FOREST));
        this.cost = 25;
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        long base = (long) BASE_PER_STAGE * gameState.getCurrentStage();
        // percentage is on the balance CAPPED at the stage goal -> being richer than the goal earns
        // no extra interest (kills the "get rich, then autowin by cashing interest" exploit).
        long pctBase = Math.min(gameState.getPlayer().getBalance(), gameState.getRequiredChips());
        long interest = base + Math.round(pctBase * (double) RATE);
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
        return "At the end of every round, earn a flat $" + BASE_PER_STAGE + " per stage plus "
            + Math.round(RATE * 100) + "% of your balance (counted only up to the stage goal).";
    }
}
