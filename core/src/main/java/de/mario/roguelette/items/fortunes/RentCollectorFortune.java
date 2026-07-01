package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;

/**
 * Passive: at the end of every round, earn $1 per stage per segment on the wheel. Strong in the
 * brutal early stages (a classic 37-segment wheel pays ~$37/round at stage 1) and deliberately
 * irrelevant late (a few hundred $ against six-figure goals) — a front-loaded trickle like the
 * reworked Interest, obeying the no-uncapped-passive-income rule.
 *
 * <p>Design double-duty: rent scales with the wheel, so it rewards add-segment builds and gently
 * punishes pruning — a small economic counterweight to the remove-everything meta.
 */
public class RentCollectorFortune extends FortuneShopItem {

    private static final int RENT_PER_SEGMENT_PER_STAGE = 1;

    public RentCollectorFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/rentCollector.png")),
            Color.GOLDENROD, new Color(0.3f, 0.45f, 0.75f, 1f)));
        this.cost = 15;
    }

    private long rentPerRound(final GameState gameState) {
        return (long) RENT_PER_SEGMENT_PER_STAGE * gameState.getCurrentStage()
            * gameState.getWheel().size();
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        long rent = rentPerRound(gameState);
        if (rent > 0) {
            gameState.getPlayer().earn(rent);
        }
    }

    @Override
    public String getShortDescription() {
        return "Rent Collector";
    }

    @Override
    public String getDescription() {
        return "At the end of every round, earn $" + RENT_PER_SEGMENT_PER_STAGE
            + " per stage for every segment on the wheel. A bigger wheel pays more rent.";
    }
}
