package de.mario.roguelette.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

public class TooltipRenderer implements Renderable {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private String textHeader;
    private String textBody;
    private float maxWidth = 300f;
    private float x;
    private float y;

    public TooltipRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
    }

    @Override
    public void render() {
        // header
        font.getData().setScale(1.25f);
        GlyphLayout layoutHeader = new GlyphLayout(font, textHeader, Color.WHITE, maxWidth, Align.left, true);

        // footer
        font.getData().setScale(1f);
        GlyphLayout layoutDescription = new GlyphLayout(font, textBody, Color.WHITE, maxWidth, Align.left, true);

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

    /**
     * This will always return false
     */
    @Override
    public boolean contains(float x, float y) {
        return false;
    }

    public String getTextHeader() {
        return textHeader;
    }

    public void setTextHeader(String textHeader) {
        this.textHeader = textHeader;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
