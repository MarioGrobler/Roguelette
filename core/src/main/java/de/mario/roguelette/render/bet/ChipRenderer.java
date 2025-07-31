package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class ChipRenderer {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private float centerX;
    private float centerY;
    private float radius;
    private float angleStart;
    private float chipRadius;

    private Chip chipInHand;

    private final List<Chip> chips = new ArrayList<>();
    private final int[] bases = {1, 2, 5, 10, 20, 50, 100, 200, 500};

    private int currentMagnitude = 0;

    private int magnitude(int balance) {
         int l = (int) Math.log10(balance);
         return l <= 2 ? 0 : l - 2;
    }

    private void updateChipInHandColor() {
        int b = (int) (chipInHand.getValue() / Math.pow(10, currentMagnitude));
        chipInHand.color = chipInHand.colorForValue(b);
    }

    private void createChips(int balance) {
        chips.clear();

        currentMagnitude = magnitude(balance);
        for (int i = 0; i < bases.length; i++) {
            float angle = angleStart + i * 10;
            float x = centerX + MathUtils.cosDeg(angle) * radius;
            float y = centerY + MathUtils.sinDeg(angle) * radius;
            Chip chip = new Chip(new Circle(x, y, chipRadius), bases[i], currentMagnitude, shapeRenderer, batch, font);
            chip.setAvailable(chip.getValue() <= balance);
            chips.add(chip);
        }
        updateChipInHandColor();
    }

    public ChipRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        this(shapeRenderer, batch, font, 200, 200, 120, -45, 30);
    }

    public ChipRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, float centerX, float centerY, float radius, float angleStart, float chipRadius) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.angleStart = angleStart;
        this.chipRadius = chipRadius;

        this.chipInHand = new Chip(new Circle(0, 0, chipRadius), 0, 0, shapeRenderer, batch, font);
    }

    public void updateBalance(int balance) {
        createChips(balance);
    }

    public void render() {
        for (Chip chip : chips) {
            chip.render();
        }
    }

    public boolean contains(float x, float y) {
        for (Chip chip : chips) {
            if (chip.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleLeftClick(float x, float y) {
        for (Chip chip : chips) {
            if (chip.contains(x, y)) {
                if (chip.isAvailable()) {
                    chipInHand.setBase(chipInHand.getBase() + chip.getValue()); // getValue is ok as magnitude is always 0
                    updateChipInHandColor();
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public void handleRightClick() {
        chipInHand.setBase(0);
    }

    public int getCurrentAmountInHand() {
        return chipInHand.getValue();
    }

    public Chip getCurrentChipInHand() {
        return chipInHand;
    }

    public int getCurrentMagnitude() {
        return currentMagnitude;
    }
}
