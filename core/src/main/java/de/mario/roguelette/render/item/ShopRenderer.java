package de.mario.roguelette.render.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.GameState;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.segments.AddSegmentShopItem;
import de.mario.roguelette.items.segments.SegmentShopItem;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.render.RoundedRectRenderer;
import de.mario.roguelette.render.TooltipRenderer;
import de.mario.roguelette.render.segment.SegmentDeleteDraw;
import de.mario.roguelette.render.segment.SegmentDraw;
import de.mario.roguelette.render.segment.SegmentDrawBase;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopRenderer implements Renderable {

    // Connects shop item and the render object
    private static class ShopRecord {
        private final Renderable renderable;
        private final ShopItem shopItem;
        private final float x;
        private final float y;

        private ShopRecord(final Renderable renderable, final ShopItem shopItem, float x, float y) {
            this.renderable = renderable;
            this.shopItem = shopItem;
            this.x = x;
            this.y = y;
        }
    }


    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final GameState gameState;

    private final RoundedRectRenderer roundedRectRenderer;
    private final TooltipRenderer tooltipRenderer;
    private final List<ShopRecord> records = new ArrayList<>();

    public ShopRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.gameState = gameState;

        roundedRectRenderer = new RoundedRectRenderer(shapeRenderer, bounds);
        tooltipRenderer = new TooltipRenderer(shapeRenderer, batch, font);
        tooltipRenderer.setMaxWidth(roundedRectRenderer.getBounds().width / 3f);

        updateItems();
    }

    private void drawTooltip(final ShopItem item, float x, float y) {
        tooltipRenderer.setTextHeader(item.getShortDescription());
        tooltipRenderer.setTextBody(item.getDescription());
        tooltipRenderer.setPosition(x, y);
        tooltipRenderer.render();
    }

    private void updateFortuneItems() {
        float width = roundedRectRenderer.getBounds().width / 9f; // / (6*1.5)
        float height = width;
        float x = roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width * 1/36f; // centered along the first third of the first column
        float yMin = roundedRectRenderer.getBounds().y + 20f;
        float yMax = roundedRectRenderer.getBounds().y + roundedRectRenderer.getBounds().height - 1.5f*height - 20f;
        float step = gameState.getShop().getFortunes().size() > 1 ?  //avoid div-by.zero
            (yMax - yMin) / (gameState.getShop().getFortunes().size() - 1) : 0;

        for (int i = 0; i < gameState.getShop().getFortunes().size(); i++) {
            float y = yMin + i * step;
            FortuneShopItem item = gameState.getShop().getFortunes().get(i);
            FortuneDraw fd = new FortuneDraw(item, shapeRenderer, batch, font, new Rectangle(x, y, width, height));
            fd.setOutlineColor(Color.WHITE);

            float midX = roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width * 1/6f; // mid of first col
            float midY = y + roundedRectRenderer.getBounds().width/12f;
            records.add(new ShopRecord(fd, item, midX, midY));
        }
    }

    private void updateChanceItems() {
        float width = roundedRectRenderer.getBounds().width / 6f; // one half of a column
        float height = width;
        float x = roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width * 13/36f; // centered along the first third of the second column
        float yMin = roundedRectRenderer.getBounds().y + 20f;
        float yMax = roundedRectRenderer.getBounds().y + roundedRectRenderer.getBounds().height - height - 20f;
        float step = gameState.getShop().getChances().size() > 1 ?  //avoid div-by.zero
                     (yMax - yMin) / (gameState.getShop().getChances().size() - 1) : 0;

        for (int i = 0; i < gameState.getShop().getChances().size(); i++) {
            float y = yMin + i * step;
            ChanceShopItem item = gameState.getShop().getChances().get(i);
            ChanceDraw cd = new ChanceDraw(item, shapeRenderer, batch, font, new Rectangle(x, y, width, height));

            float midX = roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width * 3/6f; // mid of second col
            float midY = y + height / 2f;
            records.add(new ShopRecord(cd, item, midX, midY));
        }
    }

    private void updateSegmentItems() {
        float x = roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width * 7/9f; // first third of third column
        float outerRadius = (float) Math.sqrt(2) * roundedRectRenderer.getBounds().height / gameState.getShop().getSegments().size();
        float innerRadius = outerRadius / 2f;
        float yMin = roundedRectRenderer.getBounds().y - innerRadius + 20f;
        float yMax = roundedRectRenderer.getBounds().y + roundedRectRenderer.getBounds().height - outerRadius - 20f;
        float step = gameState.getShop().getSegments().size() > 1 ?  //avoid div-by.zero
            (yMax - yMin) / (gameState.getShop().getSegments().size() - 1) : 0;

        for (int i = 0; i < gameState.getShop().getSegments().size(); i++) {
            float y = yMin + i * step;
            SegmentShopItem item = gameState.getShop().getSegments().get(i);
            SegmentDrawBase sd;
            if (item instanceof AddSegmentShopItem) {
                Segment s = ((AddSegmentShopItem) item).getSegment();
                sd = new SegmentDraw(s, shapeRenderer, batch, font, x, y, outerRadius, innerRadius);
            } else { // DeleteSegmentShopItem
                sd = new SegmentDeleteDraw(shapeRenderer, batch, font, x, y, outerRadius, innerRadius);
            }
            sd.setStartAngle(-10);
            sd.setSweepAngle(20);
            sd.setRotation(90);
            sd.setOutlineColor(Color.WHITE);

            float midX = roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width * 5/6f; // mid of third col
            float midY = y + (outerRadius + innerRadius) / 2f;
            records.add(new ShopRecord(sd, item, midX, midY));
        }
    }

    public void updateItems() {
        records.clear();
        updateFortuneItems();
        updateChanceItems();
        updateSegmentItems();
    }

    @Override
    public void render() {
        // rounded rect
        roundedRectRenderer.render();

        // lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(roundedRectRenderer.getBorderColor());
        shapeRenderer.rectLine(roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width/3f, roundedRectRenderer.getBounds().y + 20, roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width/3f, roundedRectRenderer.getBounds().y + roundedRectRenderer.getBounds().height - 20, 3);
        shapeRenderer.rectLine(roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width*2/3f, roundedRectRenderer.getBounds().y + 20, roundedRectRenderer.getBounds().x + roundedRectRenderer.getBounds().width*2/3f, roundedRectRenderer.getBounds().y + roundedRectRenderer.getBounds().height - 20, 3);
        shapeRenderer.end();

        // items
        for (ShopRecord record : records) {
            record.renderable.render();

            // render price
            batch.begin();
            float y = record.y;
            if (record.shopItem.isSold()) {
                float x = record.x;
                font.getData().setScale(4f);
                GlyphLayout layout = new GlyphLayout(font, "SOLD", Color.WHITE, 0, Align.center, false);
                y += layout.height / 2f;

                // rotate the text a bit
                batch.setTransformMatrix(batch.getTransformMatrix().idt()
                    .translate(x, y, 0)
                    .rotate(0, 0, 1, 10)
                );
                font.draw(batch, layout, 0, 0);
                batch.setTransformMatrix(new Matrix4()); // reset
            } else {
                // if the item is still available, render the price
                //TODO fix positioning with a glyph layout
                float x = record.x + 1/18f * roundedRectRenderer.getBounds().width; // shift the x position a bit to the right
                font.getData().setScale(2f);
                font.draw(batch, "$" + record.shopItem.getCost(), x, y);
            }
            batch.end();

        }
    }

    public boolean contains(float x, float y) {
        return roundedRectRenderer.getBounds().contains(x, y);
    }

    private Optional<ShopItem> findShopItem(float x, float y) {
        if (!contains(x, y)) {
            return Optional.empty();
        }

        for (ShopRecord record : records) {
            if (record.renderable.contains(x, y)) {
                return Optional.of(record.shopItem);
            }
        }

        return Optional.empty();
    }

    public void handleHover(float x, float y) {
        findShopItem(x, y).ifPresent(shopItem -> drawTooltip(shopItem, x, y));
    }

    public Optional<ShopItem> handleLeftClick(float x, float y) {
        return findShopItem(x, y);
    }


}
