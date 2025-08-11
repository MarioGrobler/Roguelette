package de.mario.roguelette.render.shop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import de.mario.roguelette.render.segment.SegmentDeleteDraw;
import de.mario.roguelette.render.segment.SegmentDraw;
import de.mario.roguelette.render.segment.SegmentDrawBase;
import de.mario.roguelette.util.ColorHelper;
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
    private final Rectangle bounds;

    private final GameState gameState;

    private final Color color = new Color(0.5f, 0.35f, 0.2f, 1);
    private final float thickness = 5f;

    private final List<ShopRecord> records = new ArrayList<>();

    public ShopRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.gameState = gameState;
        this.bounds = bounds;

        updateItems();
    }

    private void drawRoundedRect(final Rectangle rect, float radius) {
        // middle
        shapeRenderer.rect(rect.x + radius, rect.y + radius, rect.width - 2 * radius, rect.height - 2 * radius);

        // sides
        shapeRenderer.rect(rect.x + radius, rect.y, rect.width - 2 * radius, radius); // down
        shapeRenderer.rect(rect.x + radius, rect.y + rect.height - radius, rect.width - 2 * radius, radius); // up
        shapeRenderer.rect(rect.x, rect.y + radius, radius, rect.height - 2 * radius); // left
        shapeRenderer.rect(rect.x + rect.width - radius, rect.y + radius, radius, rect.height - 2 * radius); // right

        // rounded corners
        shapeRenderer.circle(rect.x + radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + radius, rect.y + rect.height - radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + rect.height - radius, radius);
    }

    private void drawTooltip(final ShopItem item, float x, float y) {
        String title = item.getShortDescription();
        String description = item.getDescription();

        // for automatic line breaks
        float maxWidth = bounds.width / 3f;

        // header
        font.getData().setScale(1.25f);
        GlyphLayout layoutHeader = new GlyphLayout(font, title, Color.WHITE, maxWidth, Align.left, true);

        // footer
        font.getData().setScale(1f);
        GlyphLayout layoutDescription = new GlyphLayout(font, description, Color.WHITE, maxWidth, Align.left, true);

        float padding = 8;
        float tooltipWidth = Math.max(layoutHeader.width, layoutDescription.width) + padding * 2;
        float tooltipHeight = layoutHeader.height + layoutDescription.height + padding * 4;


        // black background with a bit of transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.85f));
        shapeRenderer.rect(x, y, tooltipWidth, tooltipHeight);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // white border + white split line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y, tooltipWidth, tooltipHeight);
        shapeRenderer.line(x, y + layoutDescription.height + 2*padding, x + tooltipWidth, y + layoutDescription.height + 2*padding);
        shapeRenderer.end();

        // text
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.25f);
        font.draw(batch, layoutHeader, x + padding, y + tooltipHeight - padding);
        font.getData().setScale(1f);
        font.draw(batch, layoutDescription, x + padding, y + tooltipHeight - layoutHeader.height - 3*padding);
        batch.end();
    }

    private void updateFortuneItems() {
        float width = bounds.width / 9f; // / (6*1.5)
        float height = width;
        float x = bounds.x + bounds.width * 1/36f; // centered along the first third of the first column
        float yMin = bounds.y + 20f;
        float yMax = bounds.y + bounds.height - 1.5f*height - 20f;
        float step = gameState.getShop().getFortunes().size() > 1 ?  //avoid div-by.zero
            (yMax - yMin) / (gameState.getShop().getFortunes().size() - 1) : 0;

        for (int i = 0; i < gameState.getShop().getFortunes().size(); i++) {
            float y = yMin + i * step;
            FortuneShopItem item = gameState.getShop().getFortunes().get(i);
            FortuneDraw fd = new FortuneDraw(item, shapeRenderer, batch, font, new Rectangle(x, y, width, height));
            fd.setOutlineColor(Color.WHITE);

            float midX = bounds.x + bounds.width * 1/6f; // mid of first col
            float midY = y + bounds.width/12f;
            records.add(new ShopRecord(fd, item, midX, midY));
        }
    }

    private void updateChanceItems() {
        float width = bounds.width / 6f; // one half of a column
        float height = width;
        float x = bounds.x + bounds.width * 13/36f; // centered along the first third of the second column
        float yMin = bounds.y + 20f;
        float yMax = bounds.y + bounds.height - height - 20f;
        float step = gameState.getShop().getChances().size() > 1 ?  //avoid div-by.zero
                     (yMax - yMin) / (gameState.getShop().getChances().size() - 1) : 0;

        for (int i = 0; i < gameState.getShop().getChances().size(); i++) {
            float y = yMin + i * step;
            ChanceShopItem item = gameState.getShop().getChances().get(i);
            ChanceDraw cd = new ChanceDraw(item, shapeRenderer, batch, font, new Rectangle(x, y, width, height));

            float midX = bounds.x + bounds.width * 3/6f; // mid of second col
            float midY = y + height / 2f;
            records.add(new ShopRecord(cd, item, midX, midY));
        }
    }

    private void updateSegmentItems() {
        float x = bounds.x + bounds.width * 7/9f; // first third of third column
        float outerRadius = (float) Math.sqrt(2) * bounds.height / gameState.getShop().getSegments().size();
        float innerRadius = outerRadius / 2f;
        float yMin = bounds.y - innerRadius + 20f;
        float yMax = bounds.y + bounds.height - outerRadius - 20f;
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

            float midX = bounds.x + bounds.width * 5/6f; // mid of third col
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
        // outer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        drawRoundedRect(bounds, 10f);

        // inner
        shapeRenderer.setColor(ColorHelper.darker(color));
        Rectangle inner = new Rectangle(bounds.x + thickness, bounds.y + thickness, bounds.width - 2*thickness, bounds.height - 2*thickness);
        drawRoundedRect(inner, 10f);

        // lines
        shapeRenderer.setColor(color);
        shapeRenderer.rectLine(bounds.x + bounds.width/3f, bounds.y + 20, bounds.x + bounds.width/3f, bounds.y + bounds.height - 20, 3);
        shapeRenderer.rectLine(bounds.x + bounds.width*2/3f, bounds.y + 20, bounds.x + bounds.width*2/3f, bounds.y + bounds.height - 20, 3);
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
                float x = record.x + 1/18f * bounds.width; // shift the x position a bit to the right
                font.getData().setScale(2f);
                font.draw(batch, "$" + record.shopItem.getCost(), x, y);
            }
            batch.end();

        }
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
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
