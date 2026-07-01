package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.items.Inventory;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.effects.MultiplierEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Stacks explicitly (like {@code PaintItBlackFortune}): only the primary copy runs the effect, keyed
 * off the live copy count {@code k}. Each turn it lights up {@code k} random segments as one-turn
 * multiplier jackpots, and at most once per stage it permanently paints a segment red, up to
 * {@code 2 + k} over the run (3 at one copy, +1 per extra). The permanent recolour is the snowball
 * lever, so it is the part that is bounded; the per-turn jackpot stays the fun, swingy part.
 */
public class ScarletSurgeFortune extends FortuneShopItem {

    private int paints = 0;
    private int lastPaintedStage = 0; // recolour at most once per stage, not once per round

    public ScarletSurgeFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/scarletSurge.png")), Color.GOLDENROD, new Color(0.72f, 0.12f, 0.12f, 1f)));
        this.cost = 8;
    }

    private int maxPaints(int copies) { return 2 + copies; } // 3 at one copy, +1 per extra

    @Override
    public void onTurnChange(final GameState gameState) {
        Inventory inv = gameState.getPlayer().getInventory();
        if (!inv.isPrimaryFortune(this)) {
            return; // only the primary copy applies the (count-scaled) shared effect
        }
        int copies = inv.countFortunes(ScarletSurgeFortune.class);

        // the surge: k random segments become high-multiplier jackpots for this turn (every turn)
        for (int i = 0; i < copies; i++) {
            int rnd = MathUtils.random(gameState.getWheel().size() - 1);
            gameState.getWheel().getSegmentAt(rnd).addEffect(new MultiplierEffect(1, 9));
        }

        // ...and, at most once per STAGE, a random non-red segment is permanently painted red so the
        // wheel tilts red slowly over the run instead of homogenising within a stage or two.
        if (gameState.getCurrentStage() != lastPaintedStage) {
            lastPaintedStage = gameState.getCurrentStage();
            if (paints < maxPaints(copies)) {
                List<Segment> nonReds = new ArrayList<>();
                for (Segment segment : gameState.getWheel().getSegments()) {
                    if (segment.getColor() != Segment.SegmentColor.RED && segment.isRecolorable()) {
                        nonReds.add(segment);
                    }
                }
                if (!nonReds.isEmpty()) {
                    nonReds.get(MathUtils.random(0, nonReds.size() - 1)).setColor(Segment.SegmentColor.RED);
                    paints++;
                }
            }
        }
    }

    @Override
    public String getShortDescription() {
        return "Scarlet Surge";
    }

    @Override
    public String getDescription() {
        return "Every turn, massively boosts a random segment's multiplier for one turn (one per copy"
            + " owned). Each stage, paints a random segment red (up to 3, +1 per extra copy).";
    }
}
