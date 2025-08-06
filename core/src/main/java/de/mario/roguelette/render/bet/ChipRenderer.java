package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChipRenderer {
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final GameState gameState;

    // the height value is not used
    private final Rectangle bounds;

    private final float chipRadius;

    private final Chip chipInHand;

    private final List<Chip> chips = new ArrayList<>();
    private final int[] bases = {1, 2, 5, 10, 20, 50, 100, 200, 500};

    private void updateChipInHand() {
        this.chipInHand.setBase(gameState.getPlayer().getCurrentlyInHand());
        int b = (int) (gameState.getPlayer().getCurrentlyInHand() / Math.pow(10, gameState.magnitudeAvailableBalance()));
        chipInHand.color = chipInHand.colorForValue(b);
    }

    public ChipRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final GameState gameState, final Rectangle bounds, float chipRadius) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.gameState = gameState;

        this.bounds = bounds;
        this.chipRadius = chipRadius;

        this.chipInHand = new Chip(new Circle(0, 0, chipRadius), 0, 0, shapeRenderer, batch, font);
        createChips();
    }

    public void createChips() {
        chips.clear();

//        for (int i = 0; i < bases.length; i++) {
//            float angle = angleStart + i * 10;
//            float x = centerX + MathUtils.cosDeg(angle) * radius;
//            float y = centerY + MathUtils.sinDeg(angle) * radius;
//            Chip chip = new Chip(new Circle(x, y, chipRadius), bases[i], gameState.magnitudeAvailableBalance(), shapeRenderer, batch, font);
//            chip.setAvailable(chip.getValue() <= gameState.getAvailableBalance());
//            chips.add(chip);
//        }

        float minX = bounds.x + chipRadius;
        float maxX = bounds.x + bounds.width - chipRadius;
        float step = (bases.length < 2) ? 0f : (maxX - minX) / (bases.length - 1);
        for (int i = 0; i < bases.length; i++) {
            float x = minX + i * step;
            Chip chip = new Chip(new Circle(x, bounds.y, chipRadius), bases[i], gameState.magnitudeAvailableBalance(), shapeRenderer, batch, font);
            chip.setAvailable(chip.getValue() <= gameState.getAvailableBalance());
            chips.add(chip);
        }
        updateChipInHand();
    }

    public void render() {
        for (Chip chip : chips) {
            chip.render();
        }
    }

    // only renders chip if the amount in hand is > 0
    public void renderChipInHand(float x, float y) {
        if (gameState.getPlayer().getCurrentlyInHand() > 0) {
            chipInHand.setPosition(x, y);
            chipInHand.render();
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

    public Optional<Integer> handleLeftClick(float x, float y) {
        for (Chip chip : chips) {
            if (chip.contains(x, y)) {
                if (chip.isAvailable()) {
                    return Optional.of(chip.getValue());
                }
                break;
            }
        }
        return Optional.empty();
    }

}
