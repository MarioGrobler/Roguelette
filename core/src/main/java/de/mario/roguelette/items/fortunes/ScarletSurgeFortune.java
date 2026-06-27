package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.effects.MultiplierEffect;

public class ScarletSurgeFortune extends FortuneShopItem{

    // The per-turn multiplier jackpot stays unbounded (it's the fun), but the PERMANENT recolour is
    // capped to match Paint It Black: an uncapped red-paint-every-turn drifts the whole wheel red,
    // turning a red colour bet into a near-guaranteed machine (the all-red no-brainer board).
    private static final int MAX_PAINTS = 6;

    private int paints = 0;
    private int lastPaintedStage = 0; // recolour at most once per stage, not once per round

    public ScarletSurgeFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/scarletSurge.png")), Color.GOLDENROD, new Color(0.72f, 0.12f, 0.12f, 1f)));
        this.cost = 8;
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        int rnd = MathUtils.random(gameState.getWheel().size() - 1);
        Segment segment = gameState.getWheel().getSegmentAt(rnd);
        // the surge: a random segment becomes a high-multiplier jackpot for this turn (every turn)
        segment.addEffect(new MultiplierEffect(1, 9));
        // ...and, at most once per STAGE (not per round), is permanently painted red so the wheel
        // tilts red slowly over the run instead of homogenising within a stage or two
        if (gameState.getCurrentStage() != lastPaintedStage) {
            lastPaintedStage = gameState.getCurrentStage();
            if (paints < MAX_PAINTS && segment.getColor() != Segment.SegmentColor.RED) {
                segment.setColor(Segment.SegmentColor.RED);
                paints++;
            }
        }
    }

    @Override
    public String getShortDescription() {
        return "Scarlet Surge";
    }

    @Override
    public String getDescription() {
        return "Every turn, massively boosts a random segment's multiplier for one turn. Each stage,"
            + " paints a random segment red (up to " + MAX_PAINTS + " over the run).";
    }
}
