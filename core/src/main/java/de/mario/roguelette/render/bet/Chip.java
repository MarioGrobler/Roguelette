package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public class Chip {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final Circle bounds;
    private float fontScale = 1.2f;

    // actual value = base * 10^magnitude
    private int base;
    private int magnitude;
    private boolean available = true;

    Color color;

    Color colorForValue(int base) {
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

    public Chip(Circle bounds, int base, int magnitude, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        this.bounds = bounds;
        this.base = base;
        this.magnitude = magnitude;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        this.color = colorForValue(base);
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public void render() {
        Color darkColor = new Color(color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, color.a);

        // to grayscale if unavailable
        if (!available) {
            //they should rename "mul" to "fuck up your graphics
            //color = color.mul(0.299f, 0.587f, 0.114f, 1f);
            float gray = color.r * 0.299f + color.g * 0.587f + color.b * 0.114f;
            color = new Color(gray, gray, gray, 1f);

            float darkGray = darkColor.r * 0.299f + darkColor.g * 0.587f + darkColor.b * 0.114f;
            darkColor = new Color(darkGray, darkGray, darkGray, 1f);
        }

        // border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int alt = 12;
        float anglePerAlt = 360f / alt;
        float thickness = 5;
        for (int i = 0; i < alt; i++) {
            float angle = anglePerAlt * i;
            float x1 = bounds.x + MathUtils.cosDeg(angle) * bounds.radius;
            float y1 = bounds.y + MathUtils.sinDeg(angle) * bounds.radius;
            float x2 = bounds.x + MathUtils.cosDeg(angle) * (bounds.radius - thickness);
            float y2 = bounds.y + MathUtils.sinDeg(angle) * (bounds.radius - thickness);

            shapeRenderer.setColor(i % 2 == 0 ? Color.WHITE : darkColor);
            shapeRenderer.arc(bounds.x, bounds.y, bounds.radius, angle, anglePerAlt, 5);
        }

        // middle part
        shapeRenderer.setColor(color);
        shapeRenderer.circle(bounds.x, bounds.y, bounds.radius - thickness);
        shapeRenderer.end();
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.circle(bounds.x, bounds.y, bounds.radius);
//        //shapeRenderer.circle(bounds.x, bounds.y, bounds.radius - thickness);
//        shapeRenderer.end();

        // value
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        batch.begin();
        font.getData().setScale(fontScale);
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, getLabel());
        font.draw(batch, layout, bounds.x - layout.width / 2, bounds.y + layout.height / 2);
        batch.end();
        shapeRenderer.end();
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
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

    public int getValue() {
        return base * (int) Math.pow(10, magnitude);
    }

    public float getFontScale() {
        return fontScale;
    }

    public void setFontScale(float fontScale) {
        this.fontScale = fontScale;
    }

    public String getLabel() {
        int value = getValue();
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
