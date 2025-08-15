package de.mario.roguelette.render.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.GameState;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.render.RoundedRectRenderer;
import de.mario.roguelette.render.segment.SegmentDraw;
import de.mario.roguelette.util.ColorHelper;

public class CrystalBallRenderer implements Renderable {
    private final SpriteBatch batch;
    private final Rectangle bounds;

    private final GameState gameState;

    private final Texture crystalBallTexture = new Texture(Gdx.files.internal("icon/crystalBall.png"));
    private final SegmentDraw segmentDraw;
    private final RoundedRectRenderer roundedRectRenderer;

    public CrystalBallRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds) {
        this.batch = batch;
        this.gameState = gameState;
        this.bounds = bounds;

        roundedRectRenderer = new RoundedRectRenderer(shapeRenderer, bounds);
        roundedRectRenderer.setFillColor(Color.WHITE);
        roundedRectRenderer.setBorderColor(ColorHelper.darken(new Color(0.5f, 0.35f, 0.2f, 1)));

        segmentDraw = new SegmentDraw(gameState.getCrystalBallSegment(), shapeRenderer, batch, font, bounds.x + bounds.width/2f, bounds.y + bounds.height/4f, bounds.height/2f, bounds.height/5f);
        segmentDraw.setStartAngle(-10);
        segmentDraw.setSweepAngle(20);
        segmentDraw.setRotation(90);
        segmentDraw.setOutlineColor(Color.WHITE);
    }

    public void updateSegment() {
        segmentDraw.setSegment(gameState.getCrystalBallSegment());
    }

    @Override
    public void render() {
        roundedRectRenderer.render();

        batch.begin();
        batch.draw(crystalBallTexture, bounds.x, bounds.y, bounds.width, bounds.height);
        batch.end();

        segmentDraw.render();
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }


}
