package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.LandingContext;
import de.mario.roguelette.wheel.Segment;

/**
 * One-turn pact: if a ball lands on a 0 / colourless segment — the landing that normally loses
 * everything — the devil pays 10x the total stake instead. A lottery ticket solo (~1-2 zeros on a
 * near-classic wheel), a real play combo'd with Freeze on a patch containing a zero. When the
 * curses layer adds the Devil's Segment (never wins, never removable), this pact extends to it.
 *
 * <p>The owed payout is captured in {@link #onBallLanded} (the stake is still on the table there;
 * bets are cleared during resolution) but only credited in {@link #onTurnChange}, after the round
 * has resolved — no payout evaluation happens inside the landing hook.
 */
public class DevilsDueChance extends PendingChanceShopItem {

    private static final int PAYOUT_FACTOR = 10;

    private long owed = 0;

    public DevilsDueChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/devilsDue.png")),
            new Color(0.2f, 0.08f, 0.08f, 1f),  // dark hellish fill
            new Color(0.85f, 0.15f, 0.1f, 1f),  // devil red
            new Color(0.95f, 0.75f, 0.15f, 1f)));
        this.cost = 12;
    }

    @Override
    public void onBallLanded(final GameState gameState, final LandingContext landing) {
        if (landing.getSegment().getCurrentColor() == Segment.SegmentColor.NONE) {
            owed += (long) PAYOUT_FACTOR * gameState.getBetManager().totalAmount();
        }
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        if (owed > 0) {
            gameState.getPlayer().earn(owed);
            owed = 0;
        }
        super.onTurnChange(gameState); // ticks the duration down
    }

    @Override
    public String getShortDescription() {
        return "Devil's Due";
    }

    @Override
    public String getDescription() {
        return "For one turn: if the ball lands on a 0 or other colourless segment, the devil pays you "
            + PAYOUT_FACTOR + "x your total stake. Lasts for one turn.";
    }
}
