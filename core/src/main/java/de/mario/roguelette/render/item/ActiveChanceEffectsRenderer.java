package de.mario.roguelette.render.item;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.GameState;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.render.Renderable;

import java.util.ArrayList;
import java.util.List;

public class ActiveChanceEffectsRenderer implements Renderable {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle bounds;

    private final GameState gameState;

    private final List<ChanceDraw> chanceDraws = new ArrayList<>();

    float drawLength;

    public ActiveChanceEffectsRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.bounds = bounds;
        this.gameState = gameState;

        this.drawLength = bounds.height;
        updateChances();
    }

    public void updateChances() {
        chanceDraws.clear();
        for (int i = 0; i < gameState.getPendingChanceManager().getActiveChances().size(); i++) {
            ChanceShopItem chance = gameState.getPendingChanceManager().getActiveChances().get(i);
            Rectangle rect = new Rectangle(bounds.x + 1.2f * i * drawLength, bounds.y, drawLength, drawLength);
            ChanceDraw cd = new ChanceDraw(chance, shapeRenderer, batch, font,rect);
            cd.setDrawDuration(true);
            chanceDraws.add(cd);
        }
    }

    @Override
    public void render() {
        for (ChanceDraw cd : chanceDraws) {
            cd.render();
        }
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public float getDrawLength() {
        return drawLength;
    }

    public void setDrawLength(float drawLength) {
        this.drawLength = drawLength;
    }
}
