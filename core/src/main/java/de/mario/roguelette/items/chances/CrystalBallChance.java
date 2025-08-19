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
        int index = MathUtils.random(gameState.getWheel().size() - 1);
        gameState.setCrystalBallSegment(gameState.getWheel().getSegmentAt(index));
        gameState.pushState(GameState.GameStateMode.SHOW_CRYSTAL_BALL, 2.5f, () -> {
            // 20% chance of lie!
            if (MathUtils.random() < 0.2) {
                gameState.resetCrystalBallSegment();
            }
        });

        System.out.println(gameState.getWheel().getSegmentAt(index).getDescription());
    }

    @Override
    public String getShortDescription() {
        return "Crystal Ball";
    }

    @Override
    public String getDescription() {
        return "Get a glimpse of what fade may hold. But beware: truth and illusion often dance as one within its glow...";
    }
}
