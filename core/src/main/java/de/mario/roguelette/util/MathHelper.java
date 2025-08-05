package de.mario.roguelette.util;

public class MathHelper {
    public static int magnitude(int balance) {
        int l = (int) Math.log10(balance);
        return l <= 2 ? 0 : l - 2;
    }
}
