package de.mario.roguelette.render.item;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.GameState;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.render.TooltipRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActiveChanceEffectsRenderer implements Renderable {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle bounds;

    private final TooltipRenderer tooltipRenderer;

    private final GameState gameState;

    private final List<ChanceDraw> chanceDraws = new ArrayList<>();

    float drawLength;

    public ActiveChanceEffectsRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.bounds = bounds;
        this.gameState = gameState;

        this.tooltipRenderer = new TooltipRenderer(shapeRenderer, batch, font);
        this.tooltipRenderer.setMaxWidth(bounds.width / 3f);

        this.drawLength = bounds.height;
        updateChances();
    }

    private void drawTooltip(final ChanceShopItem chance, float x, float y) {
        tooltipRenderer.setTextHeader(chance.getShortDescription());
        tooltipRenderer.setTextBody(chance.getDescription());
        tooltipRenderer.setPosition(x, y);
        tooltipRenderer.render();
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

    private Optional<ChanceShopItem> findChance(float x, float y) {
        if (!contains(x, y)) {
            return Optional.empty();
        }

        for (ChanceDraw cd : chanceDraws) {
            if (cd.contains(x, y)) {
                return Optional.of(cd.getItem());
            }
        }

        return Optional.empty();
    }

    public void handleHover(float x, float y) {
        findChance(x, y).ifPresent(chance -> drawTooltip(chance, x, y));
    }

    public float getDrawLength() {
        return drawLength;
    }

    public void setDrawLength(float drawLength) {
        this.drawLength = drawLength;
    }
}
