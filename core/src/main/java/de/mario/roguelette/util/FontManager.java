package de.mario.roguelette.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Disposable;

public class FontManager implements Disposable {

    private static final String FONT_PATH = "fonts/Montserrat-Bold.ttf";

    private FreeTypeFontGenerator generator;

    private BitmapFont smallFont;      // 18px - tooltips, small labels
    private BitmapFont defaultFont;    // 24px - general UI
    private BitmapFont mediumFont;     // 32px - buttons, prices
    private BitmapFont largeFont;      // 48px - balance, goals
    private BitmapFont titleFont;      // 64px - titles, big numbers

    public FontManager() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));

        smallFont = generateFont(12);
        defaultFont = generateFont(15);
        mediumFont = generateFont(20);
        largeFont = generateFont(30);
        titleFont = generateFont(40);
    }

    private BitmapFont generateFont(int size) {
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = new Color(0, 0, 0, 0.5f);
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = new Color(0, 0, 0, 0.3f);
        return generator.generateFont(parameter);
    }

    public BitmapFont getSmall() {
        return smallFont;
    }

    public BitmapFont getDefault() {
        return defaultFont;
    }

    public BitmapFont getMedium() {
        return mediumFont;
    }

    public BitmapFont getLarge() {
        return largeFont;
    }

    public BitmapFont getTitle() {
        return titleFont;
    }

    @Override
    public void dispose() {
        if (generator != null) generator.dispose();
        if (smallFont != null) smallFont.dispose();
        if (defaultFont != null) defaultFont.dispose();
        if (mediumFont != null) mediumFont.dispose();
        if (largeFont != null) largeFont.dispose();
        if (titleFont != null) titleFont.dispose();
    }
}
