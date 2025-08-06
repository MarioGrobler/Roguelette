package de.mario.roguelette.render.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.GameState;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.segments.AddSegmentShopItem;
import de.mario.roguelette.items.segments.SegmentShopItem;
import de.mario.roguelette.render.SegmentDraw;
import de.mario.roguelette.util.ColorHelper;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShopRenderer {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Rectangle bounds;

    private final GameState gameState;

    private final Color color = new Color(0.5f, 0.35f, 0.2f, 1);
    private final float thickness = 5f;

    private final List<SegmentDraw> segmentDraws = new ArrayList<>();

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
        shapeRenderer.rect(rect.x + radius, rect.y, rect.width - 2 * radius, radius); // unten
        shapeRenderer.rect(rect.x + radius, rect.y + rect.height - radius, rect.width - 2 * radius, radius); // oben
        shapeRenderer.rect(rect.x, rect.y + radius, radius, rect.height - 2 * radius); // links
        shapeRenderer.rect(rect.x + rect.width - radius, rect.y + radius, radius, rect.height - 2 * radius); // rechts

        // rounded corners
        shapeRenderer.circle(rect.x + radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + radius, radius);
        shapeRenderer.circle(rect.x + radius, rect.y + rect.height - radius, radius);
        shapeRenderer.circle(rect.x + rect.width - radius, rect.y + rect.height - radius, radius);
    }


    public void updateItems() {
        segmentDraws.clear();

        float x = bounds.x + bounds.width * 7/9f; // first third of third column
        float outerRadius = 1.5f*bounds.height / gameState.getShop().getSegments().size();
        float innerRadius = outerRadius / 2f;
        float yMin = bounds.y - innerRadius + 20f;
        float yMax = bounds.y + bounds.height - innerRadius - 10f;
        float step = (yMax - yMin) / gameState.getShop().getSegments().size();
        for (int i = 0; i < gameState.getShop().getSegments().size(); i++) {
            float y = yMin + i * step;
            SegmentShopItem item = gameState.getShop().getSegments().get(i);
            if (item instanceof AddSegmentShopItem) {
               Segment s = ((AddSegmentShopItem) item).getSegment();
               SegmentDraw sd = new SegmentDraw(s, shapeRenderer, batch, font, x, y, outerRadius, innerRadius);
               sd.setStartAngle(-10);
               sd.setSweepAngle(20);
               sd.setRotation(90);
               segmentDraws.add(sd);
            } else {
                // TODO...
            }
        }
    }

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
        float x = bounds.x + bounds.width * 8/9f;
        for (int i = 0; i < gameState.getShop().getSegments().size(); i++) {
            SegmentDraw sd = segmentDraws.get(i);
            sd.render();
            float y = sd.getCenterY() + (sd.getOuterRadius() + sd.getInnerRadius()) / 2f;
            batch.begin();
            batch.setTransformMatrix(batch.getTransformMatrix().idt());
            font.getData().setScale(2f);
            font.draw(batch, "$" + gameState.getShop().getSegments().get(i).getCost(), x, y);
            batch.end();
        }
    }

    public Optional<ShopItem> handleLeftClick(float x, float y) {
        for (int i = 0; i < segmentDraws.size(); i++) {
            //TODO this is really not elegant
            if (segmentDraws.get(i).contains(x, y)) {
                return Optional.of(gameState.getShop().getSegments().get(i));
            }
        }
        return Optional.empty();
    }


}
