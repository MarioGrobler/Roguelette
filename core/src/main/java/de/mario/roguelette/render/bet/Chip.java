package de.mario.roguelette.render.bet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import de.mario.roguelette.render.Renderable;
import de.mario.roguelette.util.ColorHelper;

public class Chip implements Renderable {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final Circle bounds;
    private float fontScale = 1.2f;

    // actual value = base * 10^magnitude
    private long base;
    private int magnitude;
    private boolean available = true;

    Color color;

    Color colorForValue(long base) {
        if (base < 2) return Color.BROWN;
        if (base < 5) return Color.GOLDENROD;
        if (base < 10) return Color.ORANGE;
        if (base < 20) return Color.RED;
        if (base < 50) return Color.PURPLE;
        if (base < 100) return Color.BLUE;
        if (base < 200) return Color.TEAL;
        if (base < 500) return new Color(0x208020ff);
        return new Color(0x2f2f2fff);
    }

    public Chip(Circle bounds, long base, int magnitude, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        this.bounds = bounds;
        this.base = base;
        this.magnitude = magnitude;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        this.color = colorForValue(base);
    }

    @Override
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    @Override
    public void render() {
        // to grayscale if unavailable
        Color renderColor = available ? color : ColorHelper.grayscale(color);
        Color darkColor = ColorHelper.darken(renderColor);

        // Drop shadow
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.3f));
        shapeRenderer.circle(bounds.x + 2, bounds.y - 2, bounds.radius);
        shapeRenderer.end();

        // border - thick alternating segments (poker chip look)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int alt = 12;
        float anglePerAlt = 360f / alt;
        float thickness = 5;
        for (int i = 0; i < alt; i++) {
            float angle = anglePerAlt * i;
            shapeRenderer.setColor(i % 2 == 0 ? Color.WHITE : darkColor);
            shapeRenderer.arc(bounds.x, bounds.y, bounds.radius, angle, anglePerAlt, 5);
        }

        // middle part with subtle radial gradient
        float innerRadius = bounds.radius - thickness;
        Color edgeColor = ColorHelper.darken(renderColor, 0.1f);
        Color centerColor = ColorHelper.lighten(renderColor, 0.05f);

        int layers = 6;
        for (int i = 0; i < layers; i++) {
            float t = (float) i / (layers - 1);
            Color layerColor = new Color(
                lerp(edgeColor.r, centerColor.r, t),
                lerp(edgeColor.g, centerColor.g, t),
                lerp(edgeColor.b, centerColor.b, t),
                1f
            );
            shapeRenderer.setColor(layerColor);
            float radius = lerp(innerRadius, innerRadius * 0.2f, t);
            shapeRenderer.circle(bounds.x, bounds.y, radius);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // value
        batch.begin();
        font.getData().setScale(fontScale);
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, getLabel());
        font.draw(batch, layout, bounds.x - layout.width / 2, bounds.y + layout.height / 2);
        batch.end();
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public long getBase() {
        return base;
    }

    public void setBase(long base) {
        this.base = base;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(int magnitude) {
        this.magnitude = magnitude;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long getValue() {
        return base * (long) Math.pow(10, magnitude);
    }

    public float getFontScale() {
        return fontScale;
    }

    public void setFontScale(float fontScale) {
        this.fontScale = fontScale;
    }

    public String getLabel() {
        long value = getValue();
        if (value % 1000000 == 0) return (value / 1000000) + "M";
        if (value > 1000000 && value % 100000 == 0) return String.format("%.1fM", value / 1000000f);
        if (value % 1000 == 0) return (value / 1000) + "K";
        if (value > 1000 && value % 100 == 0) return String.format("%.1fK", value / 1000f);
        return String.valueOf(value);
    }

    public void setPosition(float x, float y) {
        this.bounds.x = x;
        this.bounds.y = y;
    }

}
