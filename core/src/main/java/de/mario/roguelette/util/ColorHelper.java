package de.mario.roguelette.util;

import com.badlogic.gdx.graphics.Color;

public class ColorHelper {
    public static Color darken(final Color color) {
        return darken(color, 0.3f);
    }

    public static Color darken(final Color color, float amount) {
        float factor = 1f - amount;
        return new Color(color.r * factor, color.g * factor, color.b * factor, color.a);
    }

    public static Color lighten(final Color color) {
        return lighten(color, 0.3f);
    }

    public static Color lighten(final Color color, float amount) {
        float diffR = 1f - color.r;
        float diffG = 1f - color.g;
        float diffB = 1f - color.b;

        return new Color(
            color.r + diffR * amount,
            color.g + diffG * amount,
            color.b + diffB * amount,
            color.a
        );
    }

    public static Color grayscale(final Color color) {
        float gray = color.r * 0.299f + color.g * 0.587f + color.b * 0.114f;
        return new Color(gray, gray, gray, 1f);
    }

    public static Color mix(final Color color1, final Color color2) {
        return new Color( (color1.r + color2.r) / 2f, (color1.g + color2.g) / 2f, (color1.b + color2.b) / 2f, (color1.a + color2.a) / 2f);
    }
}
