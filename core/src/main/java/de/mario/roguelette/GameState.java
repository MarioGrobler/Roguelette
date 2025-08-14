package de.mario.roguelette;

import de.mario.roguelette.items.Shop;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.PendingChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
import de.mario.roguelette.util.BetManager;
import de.mario.roguelette.util.MathHelper;
import de.mario.roguelette.util.PendingChanceManager;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.Wheel;

import java.util.ArrayDeque;
import java.util.Deque;

public class GameState {
    private final Player player;
    private final Wheel wheel;
    private final BetManager betManager;
    private final Shop shop;

    private final PendingChanceManager pendingChanceManager = new PendingChanceManager();
    private DeleteSegmentShopItem pendingDeleteItem = null;
    private Segment crystalBallSegment = null;


    public enum GameStateMode {
        DEFAULT,
        SPINNING,
        DELETE_SEGMENT_SELECTING,
        SHOW_CRYSTAL_BALL,
        SHOP_OPEN
    }

    private static class TimedState {
        GameStateMode mode;
        float remainingTime; // <= 0 -> no timer
        TimedState(GameStateMode mode) {
            this(mode, -1);
        }

        TimedState(GameStateMode mode, float remainingTime) {
            this.mode = mode;
            this.remainingTime = remainingTime;
        }
    }

    // bottom: "main states", top: "overlay states"
    private final Deque<TimedState> stateStack = new ArrayDeque<>();

    public GameState(final Player player, final Wheel wheel, final BetManager betManager, final Shop shop) {
        this.player = player;
        this.wheel = wheel;
        this.betManager = betManager;
        this.shop = shop;

        this.stateStack.push(new TimedState(GameStateMode.DEFAULT));
    }

    // getters for important objects
    public Player getPlayer() {
        return player;
    }

    public Wheel getWheel() {
        return wheel;
    }

    public BetManager getBetManager() {
        return betManager;
    }

    public Shop getShop() {
        return shop;
    }

    public PendingChanceManager getPendingChanceManager() {
        return pendingChanceManager;
    }

    // state stack stuff
    public void setState(GameStateMode state) {
        stateStack.clear();
        stateStack.push(new TimedState(state, -1));
    }

    public void pushState(GameStateMode state) {
        stateStack.push(new TimedState(state, -1));
    }

    public void pushState(GameStateMode state, float duration) {
        stateStack.push(new TimedState(state, duration));
    }

    public void popState() {
        if (stateStack.size() > 1) {
            stateStack.pop();
        }
    }

    public GameStateMode getCurrentState() {
        assert stateStack.peek() != null;
        return stateStack.peek().mode;
    }

    public boolean isStateInStack(GameStateMode state) {
        return stateStack.stream().anyMatch(s -> s.mode == state);
    }

    public void update(float delta) {
        if(!stateStack.isEmpty()) {
            TimedState state = stateStack.peek();
            if (state.remainingTime > 0) {
                state.remainingTime -= delta;
                if (state.remainingTime <= 0) {
                    popState();
                }
            }
        }
    }

    /**
     * Takes a chance from the inventory and adds it to the game states currently active chances
     * @param index the index of the chance in the players inventory
     */
    public void activateChance(int index) {
        ChanceShopItem chance = player.getInventory().popChanceAtIndex(index);
        chance.onActivate(this);
    }

    /**
     * For every active chance item, applies {@link PendingChanceShopItem#onTurnChange(GameState)}.
     * Similarly, for every fortune, applies {@link FortuneShopItem#onTurnChange(GameState)}.
     * Afterwards, removes all expired chances from the active list.
     */
    public void applyOnTurnChangeEffects() {
        for (FortuneShopItem item : player.getInventory().getFortunes()) {
            item.onTurnChange(this);
        }
        for (PendingChanceShopItem item : pendingChanceManager.getActiveChances()) {
            item.onTurnChange(this);
        }
        pendingChanceManager.removeDeadChances();
    }

    public DeleteSegmentShopItem getPendingDeleteItem() {
        return pendingDeleteItem;
    }

    public void setPendingDeleteItem(DeleteSegmentShopItem pendingDeleteItem) {
        this.pendingDeleteItem = pendingDeleteItem;
    }

    public void activateCrystalBall(final Segment crystalBallSegment) {
        this.crystalBallSegment = crystalBallSegment;
        pushState(GameStateMode.SHOW_CRYSTAL_BALL, 2.5f);
    }

    public Segment getCrystalBallSegment() {
        return crystalBallSegment;
    }

    public void resetCrystalBallSegment() {
        this.crystalBallSegment = null;
    }

    /**
     * @return Balance that is neither currently in hand nor on the bet table
     */
    public int getAvailableBalance() {
        return player.getBalance() - player.getCurrentlyInHand() - betManager.totalAmount();
    }

    public int getBalanceMinusBets() {
        return player.getBalance() - betManager.totalAmount();
    }

    /**
     * @return true if at least one bet is placed; false otherwise
     */
    public boolean betsNotEmpty() {
        return !betManager.getBets().isEmpty();
    }

    /**
     * Computes the return of the bets for the current segment. Clears the bets and pending chances afterwards.
     */
    public void applyReturnOfBets(final Segment segment) {
        player.earn(betManager.computeReturn(segment, this));
        betManager.clear();
    }

    public int magnitudeBalance() {
        return MathHelper.magnitude(player.getBalance());
    }

    public int magnitudeAvailableBalance() {
        return MathHelper.magnitude(getAvailableBalance());
    }

}
