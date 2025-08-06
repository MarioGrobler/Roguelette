package de.mario.roguelette.util;

public class MathHelper {
    public static int magnitude(int balance) {
        int l = (int) Math.log10(balance);
        return l <= 2 ? 0 : l - 2;
    }

    public static float normalizeAngle(float angle) {
        angle %= 360f;
        if (angle < 0f) angle += 360f;
        return angle;
    }
}
