package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.betting.ColorBet;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.items.Inventory;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Stacks <em>explicitly</em>: one copy is a modest synergy piece, and extra copies scale it
 * sub-linearly rather than the naive "two listeners = double everything" that turned a black colour
 * bet into a guaranteed high-multiplier machine. With {@code k} copies owned:
 * <ul>
 *   <li>paints up to {@code 2 + k} segments black over the run (3 at k=1, +1 per extra copy),</li>
 *   <li>raises the black colour-bet payout by {@code 0.5 * k} each turn,</li>
 *   <li>up to a cap of {@code 1 + k} (+2 at k=1, +1 per extra copy).</li>
 * </ul>
 * Only the <em>primary</em> (first-owned) copy runs the effect, keyed off the live copy count, so the
 * other copies don't multiply it.
 */
public class PaintItBlackFortune extends FortuneShopItem {

    private float blackModifier = 0f;
    private int paints = 0;
    private int lastPaintedStage = 0; // recolour at most once per stage, not once per round

    public PaintItBlackFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/paintItBlack.png")), Color.GOLDENROD, new Color(0.12f, 0.12f, 0.15f, 1f)));
        this.cost = 20;
    }

    private int maxPaints(int copies)     { return 2 + copies; }       // 3 at one copy, +1 per extra
    private float maxModifier(int copies)  { return 1f + copies; }      // +2 at one copy, +1 per extra
    private float perTurnStep(int copies)  { return 0.5f * copies; }    // +0.5/turn per copy

    @Override
    public void onTurnChange(final GameState gameState) {
        Inventory inv = gameState.getPlayer().getInventory();
        if (!inv.isPrimaryFortune(this)) {
            return; // only the primary copy applies the (count-scaled) shared effect
        }
        int copies = inv.countFortunes(PaintItBlackFortune.class);

        // Recolour at most once per STAGE (not every round): the wheel tilts black slowly over the run.
        if (gameState.getCurrentStage() != lastPaintedStage) {
            lastPaintedStage = gameState.getCurrentStage();
            if (paints < maxPaints(copies)) {
                List<Segment> nonBlacks = new ArrayList<>();
                for (Segment segment : gameState.getWheel().getSegments()) {
                    if (segment.getColor() != Segment.SegmentColor.BLACK) {
                        nonBlacks.add(segment);
                    }
                }
                if (!nonBlacks.isEmpty()) {
                    int rnd = MathUtils.random(0, nonBlacks.size() - 1);
                    nonBlacks.get(rnd).setColor(Segment.SegmentColor.BLACK);
                    paints++;
                }
            }
        }

        blackModifier = Math.min(maxModifier(copies), blackModifier + perTurnStep(copies));
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        if (!gameState.getPlayer().getInventory().isPrimaryFortune(this)) {
            return;
        }
        if (resolution.getBet().getBetType() instanceof ColorBet
            && ((ColorBet) resolution.getBet().getBetType()).getSegmentColor() == Segment.SegmentColor.BLACK) {
            resolution.addBase(blackModifier);
        }
    }

    @Override
    public String getShortDescription() {
        return "Paint It Black";
    }

    @Override
    public String getDescription() {
        return "Each stage, paints a random segment black (up to 3, +1 per extra copy owned). Every turn,"
            + " raises the payout of black color bets by 0.5 per copy, up to +2 (+1 per extra copy)."
            + "\nCurrent bonus: +" + blackModifier;
    }
}
