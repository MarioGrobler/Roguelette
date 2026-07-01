package de.mario.roguelette.items.fortunes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import de.mario.roguelette.GameState;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.items.Inventory;

/**
 * Passive comeback tool: a spin in which <em>every</em> bet lost AND the total stake was a real
 * commitment (&ge; {@link #STAKE_THRESHOLD_FRACTION} of the pre-spin balance) charges the feather;
 * the next winning spin pays double. The stake gate is load-bearing: without it, sacrificing $1 on
 * purpose farms a guaranteed +100% for every second spin. The charge expires when the stage ends,
 * and because it boosts a <em>win</em> (rather than refunding the loss) it can't act as insurance.
 *
 * <p>Stacks sub-linearly via the primary-copy pattern (like {@code PaintItBlackFortune}): only the
 * primary copy runs the charge state machine, with the bonus scaled by the live copy count.
 */
public class PhoenixFeatherFortune extends FortuneShopItem {

    private static final float STAKE_THRESHOLD_FRACTION = 0.25f; // "real loss" = staked >= 25% of pre-spin balance
    private static final float BASE_BONUS = 1.0f;                // +100% on the charged win...
    private static final float BONUS_PER_EXTRA_COPY = 0.5f;      // ...+50% per extra copy owned

    private boolean charged = false;
    private int chargeStage = 0;

    private long balanceAtSpinStart = Long.MAX_VALUE;
    private long stakeAtSpinStart = 0;
    private boolean wonThisTurn = false;
    private boolean spunThisTurn = false;

    public PhoenixFeatherFortune() {
        super(new FortuneRenderInfo(new Texture(Gdx.files.internal("icon/phoenixFeather.png")),
            Color.GOLDENROD, new Color(0.85f, 0.3f, 0.1f, 1f)));
        this.cost = 16;
    }

    private float bonus(int copies) {
        return BASE_BONUS + BONUS_PER_EXTRA_COPY * Math.max(0, copies - 1);
    }

    @Override
    public void onSpinStart(final GameState gameState) {
        if (!gameState.getPlayer().getInventory().isPrimaryFortune(this)) {
            return;
        }
        // the charge does not survive into a new stage
        if (charged && gameState.getCurrentStage() != chargeStage) {
            charged = false;
        }
        balanceAtSpinStart = gameState.getPlayer().getBalance(); // pre-stake, like Comeback Kid
        stakeAtSpinStart = gameState.getBetManager().totalAmount();
        wonThisTurn = false;
        spunThisTurn = true;
    }

    @Override
    public void onResolveBet(final GameState gameState, final BetResolution resolution) {
        if (!resolution.isWin()) {
            return;
        }
        wonThisTurn = true;
        Inventory inv = gameState.getPlayer().getInventory();
        if (charged && inv.isPrimaryFortune(this)) {
            resolution.multiplyTotal(1f + bonus(inv.countFortunes(PhoenixFeatherFortune.class)));
        }
    }

    @Override
    public void onTurnChange(final GameState gameState) {
        if (!spunThisTurn || !gameState.getPlayer().getInventory().isPrimaryFortune(this)) {
            return;
        }
        spunThisTurn = false;
        if (wonThisTurn) {
            charged = false; // the boosted win consumed the charge (or a win simply preceded one)
        } else if (balanceAtSpinStart > 0
            && stakeAtSpinStart >= balanceAtSpinStart * (double) STAKE_THRESHOLD_FRACTION) {
            charged = true; // a real all-loss: rise from the ashes next win
            chargeStage = gameState.getCurrentStage();
        }
    }

    @Override
    public String getShortDescription() {
        return "Phoenix Feather";
    }

    @Override
    public String getDescription() {
        String state = charged ? "CHARGED - your next winning spin pays double." : "Not charged.";
        return "Lose every bet of a spin while staking at least "
            + Math.round(STAKE_THRESHOLD_FRACTION * 100) + "% of your balance, and your next winning"
            + " spin pays +" + Math.round(BASE_BONUS * 100) + "% (+"
            + Math.round(BONUS_PER_EXTRA_COPY * 100) + "% per extra copy). The charge expires at the"
            + " end of the stage.\n" + state;
    }
}
