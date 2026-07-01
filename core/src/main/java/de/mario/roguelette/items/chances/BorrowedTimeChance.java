package de.mario.roguelette.items.chances;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;

/**
 * The current round doesn't count toward the stage's round limit — with the flat 4 rounds per
 * stage, effectively a 5th spin. Blocked during boss fights (bosses have their own spin budget
 * and are the run's difficulty spine) and while another Borrowed Time is already pending; in both
 * cases the item returns itself to the inventory instead of being consumed.
 *
 * <p>The actual skip lives in {@code GameState.endRound} (the round counter and the stage-clear
 * check are deferred together); this item just sets the flag and shows up as an active effect for
 * the turn.
 */
public class BorrowedTimeChance extends PendingChanceShopItem {

    public BorrowedTimeChance() {
        super(new ChanceRenderInfo(new Texture(Gdx.files.internal("icon/borrowedTime.png")),
            Color.WHITE, new Color(0.35f, 0.55f, 0.85f, 1f), new Color(0.95f, 0.75f, 0.15f, 1f)));
        this.cost = 20;
    }

    @Override
    public void onActivate(final GameState gameState) {
        if (gameState.isBossFightActive() || gameState.isBorrowedTimeActive()) {
            // no borrowed time against a boss, and no banking several rounds at once
            gameState.getPlayer().getInventory().addChance(this);
            return;
        }
        gameState.activateBorrowedTime();
        // show as an active one-turn effect (duration ticks away with the skipped round)
        gameState.getPendingChanceManager().add(this);
    }

    @Override
    public String getShortDescription() {
        return "Borrowed Time";
    }

    @Override
    public String getDescription() {
        return "This round doesn't count toward the stage's round limit - an extra spin to reach"
            + " the goal. Can't be used during a boss fight.";
    }
}
