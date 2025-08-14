package de.mario.roguelette.render.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.GameState;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.render.RoundedRectRenderer;
import de.mario.roguelette.render.TooltipRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryRenderer implements Renderable {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final GameState gameState;

    private final TooltipRenderer tooltipRenderer;
    private final RoundedRectRenderer roundedRectRendererFortunes;
    private final RoundedRectRenderer roundedRectRendererChances;
    private final Rectangle bounds;

    private final List<FortuneDraw> fortuneDraws = new ArrayList<>();
    private final List<ChanceDraw> chanceDraws = new ArrayList<>();

    public InventoryRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.gameState = gameState;
        this.bounds = bounds;

        float heightPerBox = bounds.height / 2f - 10;
        float yFortunes = bounds.y + bounds.height - heightPerBox;
        this.tooltipRenderer = new TooltipRenderer(shapeRenderer, batch, font);
        this.roundedRectRendererFortunes = new RoundedRectRenderer(shapeRenderer, new Rectangle(bounds.x, yFortunes, bounds.width, heightPerBox));
        this.roundedRectRendererChances = new RoundedRectRenderer(shapeRenderer, new Rectangle(bounds.x, bounds.y, bounds.width, heightPerBox));

        updateItems();
    }

    private void updateFortuneItems() {
        float height = roundedRectRendererFortunes.getBounds().height / (float) Math.sqrt(2) - 40;
        float width = height;
        float xMin = roundedRectRendererFortunes.getBounds().x + 20;
        float xMax = roundedRectRendererFortunes.getBounds().x + roundedRectRendererFortunes.getBounds().width - width - 20;
        float y = roundedRectRendererFortunes.getBounds().y + 20;
        float step = gameState.getPlayer().getInventory().getFortuneMaxSize() > 1 ?
            (xMax - xMin) / (gameState.getPlayer().getInventory().getFortuneMaxSize() - 1) : 0;

        for (int i = 0; i < gameState.getPlayer().getInventory().getFortunes().size(); i++) {
            float x = xMin + i * step;
            FortuneShopItem fortune = gameState.getPlayer().getInventory().getFortunes().get(i);
            FortuneDraw fd = new FortuneDraw(fortune, shapeRenderer, batch, font, new Rectangle(x, y, width, height));
            fd.setOutlineColor(Color.WHITE);
            fortuneDraws.add(fd);
        }
    }

    private void updateChanceItems() {
        float height = roundedRectRendererChances.getBounds().height - 40; // /3f*2 bc the roof height has factor 1.5
        float width = height;
        float xMin = roundedRectRendererChances.getBounds().x + 20;
        float xMax = roundedRectRendererChances.getBounds().x + roundedRectRendererChances.getBounds().width - width - 20;
        float y = roundedRectRendererChances.getBounds().y + 20;
        float step = gameState.getPlayer().getInventory().getChanceMaxSize() > 1 ?
            (xMax - xMin) / (gameState.getPlayer().getInventory().getChanceMaxSize() - 1) : 0;

        for (int i = 0; i < gameState.getPlayer().getInventory().getChances().size(); i++) {
            float x = xMin + i * step;
            ChanceShopItem chance = gameState.getPlayer().getInventory().getChances().get(i);
            chanceDraws.add(new ChanceDraw(chance, shapeRenderer, batch, font, new Rectangle(x, y, width, height)));
        }
    }

    public void updateItems() {
        fortuneDraws.clear();
        chanceDraws.clear();
        updateFortuneItems();
        updateChanceItems();
    }


    @Override
    public void render() {
        roundedRectRendererFortunes.render();
        roundedRectRendererChances.render();

        for (FortuneDraw fd : fortuneDraws) {
            fd.render();
        }

        for (ChanceDraw cd : chanceDraws) {
            cd.render();
        }
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    private Optional<Integer> findInList(float x, float y, final List<? extends Renderable> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(x, y)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    /**
     * @return the index of the chance clicked on (based on the players inventory), given that the click hit a chance
     */
    public Optional<Integer> handleLeftClickChance(float x, float y) {
        if (!roundedRectRendererChances.contains(x, y)) {
            return Optional.empty();
        }

        return findInList(x, y, chanceDraws);
    }

    public Optional<Integer> handleLeftClickFortune(float x, float y) {
        if (!roundedRectRendererFortunes.contains(x, y)) {
            return Optional.empty();
        }

        return findInList(x, y, fortuneDraws);
    }

    private void drawTooltip(final ShopItem item, float x, float y) {
        tooltipRenderer.setTextHeader(item.getShortDescription());
        tooltipRenderer.setTextBody(item.getDescription());
        tooltipRenderer.setPosition(x, y);
        tooltipRenderer.render();
    }

    public void handleHover(float x, float y) {
        findInList(x, y, fortuneDraws).ifPresent(fortuneIndex -> {
            ShopItem fortune = gameState.getPlayer().getInventory().getFortunes().get(fortuneIndex);
            drawTooltip(fortune, x, y);
        });
        findInList(x, y, chanceDraws).ifPresent(chanceIndex -> {
            ShopItem chance = gameState.getPlayer().getInventory().getChances().get(chanceIndex);
            drawTooltip(chance, x, y);
        });
    }
}
