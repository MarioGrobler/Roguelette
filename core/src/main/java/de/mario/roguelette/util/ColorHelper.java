package de.mario.roguelette.util;

import com.badlogic.gdx.graphics.Color;

public class ColorHelper {
    public static Color darker(final Color color) {
        return new Color(color.r * 0.7f, color.g * 0.7f, color.b * 0.7f, color.a);
    }

    public static Color grayscale(final Color color) {
        float gray = color.r * 0.299f + color.g * 0.587f + color.b * 0.114f;
        return new Color(gray, gray, gray, 1f);
    }
}
