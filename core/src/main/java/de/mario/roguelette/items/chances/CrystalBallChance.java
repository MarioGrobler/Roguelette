package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;

public class CrystalBallChance extends ChanceShopItem {
    public CrystalBallChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/crystalBall.png")), Color.WHITE, Color.PURPLE, Color.CYAN));
        this.cost = 25;
    }

    @Override
    public void onActivate(final GameState gameState) {
        // The orb never lies (legendary buff): the shown segment IS the next landing.
        int index = MathUtils.random(gameState.getWheel().size() - 1);
        gameState.setCrystalBallSegment(gameState.getWheel().getSegmentAt(index));
        gameState.pushState(GameState.GameStateMode.SHOW_CRYSTAL_BALL, 2.5f, () -> {});
    }

    @Override
    public String getShortDescription() {
        return "Crystal Ball";
    }

    @Override
    public String getDescription() {
        return "Legendary. Gaze into the orb and see where fate will fall. What the glass shows, the wheel obeys.";
    }
}
