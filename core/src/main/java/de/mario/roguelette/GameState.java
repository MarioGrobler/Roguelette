package de.mario.roguelette;

import de.mario.roguelette.items.Shop;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.PendingChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
import de.mario.roguelette.screens.GameOverScreen;
import de.mario.roguelette.screens.YouWinScreen;
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

    // Progression
    private int currentStage = 1;
    private int currentRound = 1;
    private int roundsInStage = 3;
    private long requiredChips = 500; // Goal for first stage
    private final long finalGoal = 1_000_000;

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
     * Activates all <code>onTurnChange</code> effects.
     * Afterwards, removes all expired chances from the active chance list.
     */
    public void applyOnTurnChangeEffects() {
        getWheel().onTurnChange();
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


    public void endRound(final RougeletteGame game) {
        if (player.isDead()) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        currentRound++;

        if (currentRound > roundsInStage) {
            // stage succeeded
            if (player.getBalance() >= requiredChips) {
                if (player.getBalance() >= finalGoal) {
                    // you win!
                    game.setScreen(new YouWinScreen(game, game.getScreen()));
                    return;
                }
                startShopPhase();
            } else { // game over
                game.setScreen(new GameOverScreen(game));
            }
        }
    }

    private void startShopPhase() {
        setState(GameStateMode.SHOP_OPEN);

        currentStage++;
        currentRound = 1;
        roundsInStage = getRoundsForStage(currentStage);
        requiredChips = getRequiredChipsForStage(currentStage);

        shop.refreshItems();
        shop.updatePrices(getPriceMultiplier(currentStage));
    }

    public void startNextRound() {
        setState(GameStateMode.DEFAULT);
    }

    private int getRoundsForStage(int stage) {
        if (stage < 3) return 3;
        if (stage < 5) return 4;
        return 5;
    }

    private long getRequiredChipsForStage(int stage) {
        switch (stage) {
            case 1: return 500;
            case 2: return 2000;
            case 3: return 8000;
            case 4: return 40000;
            case 5: return 200000;
            default: return finalGoal;
        }
    }

    private int getPriceMultiplier(int stage) {
        return (int) Math.pow(10, stage - 1);
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public int getRoundsInStage() {
        return roundsInStage;
    }

    public long getRequiredChips() {
        return requiredChips;
    }

    public long getFinalGoal() {
        return finalGoal;
    }
}
