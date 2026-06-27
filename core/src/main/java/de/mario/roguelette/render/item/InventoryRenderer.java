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

    // Per-item "discard" (X) buttons, parallel to the draw lists. Only shown/clickable when discarding
    // is relevant (in the shop, or while a boss reward needs a freed slot) so they never compete with
    // the left-click-to-activate-a-chance gesture during normal play.
    private final List<Rectangle> fortuneTrash = new ArrayList<>();
    private final List<Rectangle> chanceTrash = new ArrayList<>();

    public InventoryRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.gameState = gameState;
        this.bounds = bounds;

        float baseHeight = bounds.height / (float) (Math.sqrt(2) + 1);
        float yFortunes = bounds.y + baseHeight + 30;
        this.tooltipRenderer = new TooltipRenderer(shapeRenderer, batch, font);
        this.roundedRectRendererFortunes = new RoundedRectRenderer(shapeRenderer, new Rectangle(bounds.x, yFortunes, bounds.width, baseHeight * (float) Math.sqrt(2) - 10)); // This one bigger
        this.roundedRectRendererChances = new RoundedRectRenderer(shapeRenderer, new Rectangle(bounds.x, bounds.y, bounds.width, baseHeight + 10));

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
            fortuneTrash.add(trashRect(x, y, width, height));
        }
    }

    /** The discard button: a small square pinned to an item's top-right corner. */
    private Rectangle trashRect(float x, float y, float width, float height) {
        float s = width * 0.34f;
        return new Rectangle(x + width - s, y + height - s * 0.7f, s, s);
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
            chanceTrash.add(trashRect(x, y, width, height));
        }
    }

    public void updateItems() {
        fortuneDraws.clear();
        chanceDraws.clear();
        fortuneTrash.clear();
        chanceTrash.clear();
        updateFortuneItems();
        updateChanceItems();
    }

    /** Whether discard (X) buttons are currently active: in the shop, or freeing a slot for a boss reward. */
    private boolean discardActive() {
        GameState.GameStateMode mode = gameState.getCurrentState();
        return mode == GameState.GameStateMode.SHOP_OPEN
            || (mode == GameState.GameStateMode.BOSS_REWARD && gameState.isAwaitingRewardDiscard());
    }

    private void drawTrashButtons() {
        if (!discardActive()) {
            return;
        }
        List<Rectangle> all = new ArrayList<>(fortuneTrash);
        all.addAll(chanceTrash);

        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.75f, 0.12f, 0.12f, 1f);
        for (Rectangle r : all) {
            shapeRenderer.circle(r.x + r.width / 2f, r.y + r.height / 2f, r.width / 2f);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for (Rectangle r : all) {
            float pad = r.width * 0.28f;
            shapeRenderer.line(r.x + pad, r.y + pad, r.x + r.width - pad, r.y + r.height - pad);
            shapeRenderer.line(r.x + pad, r.y + r.height - pad, r.x + r.width - pad, r.y + pad);
        }
        shapeRenderer.end();
    }

    /** @return the inventory index of the fortune whose discard button was clicked, if discarding is active. */
    public Optional<Integer> handleDiscardFortune(float x, float y) {
        if (!discardActive()) {
            return Optional.empty();
        }
        return findInRects(x, y, fortuneTrash);
    }

    /** @return the inventory index of the chance whose discard button was clicked, if discarding is active. */
    public Optional<Integer> handleDiscardChance(float x, float y) {
        if (!discardActive()) {
            return Optional.empty();
        }
        return findInRects(x, y, chanceTrash);
    }

    private Optional<Integer> findInRects(float x, float y, final List<Rectangle> rects) {
        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).contains(x, y)) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
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

        drawTrashButtons();
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
