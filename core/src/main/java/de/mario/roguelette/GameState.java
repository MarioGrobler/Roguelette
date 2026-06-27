package de.mario.roguelette;

import de.mario.roguelette.boss.Boss;
import de.mario.roguelette.boss.BossRoster;
import de.mario.roguelette.events.BetResolution;
import de.mario.roguelette.events.GameEventListener;
import de.mario.roguelette.events.LandingContext;
import de.mario.roguelette.events.SpinContext;
import de.mario.roguelette.items.LegendaryPool;
import de.mario.roguelette.items.Shop;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.WheelSelectChance;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
import de.mario.roguelette.screens.GameOverScreen;
import de.mario.roguelette.screens.YouWinScreen;
import de.mario.roguelette.util.BetManager;
import de.mario.roguelette.util.MathHelper;
import de.mario.roguelette.util.MusicManager;
import de.mario.roguelette.util.PendingChanceManager;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.Wheel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class GameState {
    private final Player player;
    private final Wheel wheel;
    private final BetManager betManager;
    private final Shop shop;
    private final MusicManager musicManager;

    private final PendingChanceManager pendingChanceManager = new PendingChanceManager();
    private DeleteSegmentShopItem pendingDeleteItem = null;
    private WheelSelectChance pendingChanceItem = null;
    private Segment crystalBallSegment = null;

    // Boss fight state. A boss gates the end of stages 2/4/6/8: after clearing the stage's normal
    // rounds the player must, within a few spins, GAIN bossGoal chips while the boss's debuff is
    // active. currentBoss is set for the whole encounter (intro -> fight -> reward); bossListeners is
    // non-null only while the fight itself is in progress (so the stage-clearing spin and the intro
    // screen are not affected by the debuff). The win condition is a gain (not an absolute total), so
    // doing nothing fails by default and the boss can't be skipped by arriving over target.
    private Boss currentBoss = null;
    private List<GameEventListener> bossListeners = null;
    private int bossSpinsRemaining = 0;
    private long bossStartBalance = 0;
    private long bossGoal = 0;
    private List<ShopItem> bossRewardOffer = null;
    private ShopItem pendingReward = null; // a chosen reward waiting for the player to free an inventory slot

    // Progression curve. Stage S (1-indexed) must reach STAGE_TARGETS[S-1] by the end of its
    // STAGE_ROUNDS[S-1] spins; clearing the last stage (== finalGoal) wins the run. Stage 1 is a
    // gentle setup stage (no brutal luck leap) and the goal ramps smoothly (~3.2x/stage) to $1M,
    // with more spins in the later, harder stages. More, smaller stages => more shop visits =>
    // more chances to build item synergies.
    private static final long[] STAGE_TARGETS = {150, 500, 1500, 5000, 16000, 55000, 200000, 1_000_000};
    private static final int[]  STAGE_ROUNDS  = {  4,   4,    4,    5,     5,     5,      6,         6};

    // Progression
    private int currentStage = 1;
    private int currentRound = 1;
    private int roundsInStage = STAGE_ROUNDS[0];
    private long requiredChips = STAGE_TARGETS[0];
    private final long finalGoal = STAGE_TARGETS[STAGE_TARGETS.length - 1];

    public enum GameStateMode {
        DEFAULT,
        SPINNING,
        DELETE_SEGMENT_SELECTING,
        CHANCE_SEGMENT_SELECTING,
        SHOW_CRYSTAL_BALL,
        SHOP_OPEN,
        BOSS_INTRO,   // the boss is revealed; click to begin the fight
        BOSS_FIGHT,   // betting/spinning against the boss (like DEFAULT, but the debuff is live)
        BOSS_REWARD   // boss defeated; pick one of the offered legendaries
    }

    public interface TimeoutListener {
        void onTimeout();
    }

    private static class TimedState {
        GameStateMode mode;
        float remainingTime; // <= 0 -> no timer
        TimeoutListener timeoutListener;

        TimedState(GameStateMode mode) {
            this(mode, -1);
        }

        TimedState(GameStateMode mode, float remainingTime) {
            this(mode, remainingTime, null);
        }

        TimedState(GameStateMode mode, float remainingTime, TimeoutListener timeoutListener) {
            this.mode = mode;
            this.remainingTime = remainingTime;
            this.timeoutListener = timeoutListener;
        }
    }


    // bottom: "main states", top: "overlay states"
    private final Deque<TimedState> stateStack = new ArrayDeque<>();

    public GameState(final Player player, final Wheel wheel, final BetManager betManager, final Shop shop, final MusicManager musicManager) {
        this.player = player;
        this.wheel = wheel;
        this.betManager = betManager;
        this.shop = shop;
        this.musicManager = musicManager;

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

    private void updateMusic() {
        switch (getCurrentState()) {
            case DEFAULT:
                musicManager.setDefaultMode();
                break;
            case SHOP_OPEN:
                musicManager.setShopMode();
                break;
            case SHOW_CRYSTAL_BALL:
                musicManager.setCrystalBallMode(2.5f); // be fast here, as the crystal ball only appears for 2.5s
                break;
        }
    }

    // state stack stuff
    public void setState(final GameStateMode state) {
        stateStack.clear();
        stateStack.push(new TimedState(state, -1));
        updateMusic();
    }

    public void pushState(final GameStateMode state) {
        pushState(state, -1);
    }

    public void pushState(final GameStateMode state, float duration) {
        pushState(state, duration, null);
    }

    public void pushState(final GameStateMode state, float duration, final TimeoutListener timeoutListener) {
        stateStack.push(new TimedState(state, duration, timeoutListener));
        updateMusic();
    }

    public void popState() {
        if (stateStack.size() > 1) {
            stateStack.pop();
        }
        updateMusic();
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
                    if (state.timeoutListener != null) {
                        state.timeoutListener.onTimeout();
                    }
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
     * Collects all currently active event listeners: the player's character passives first, then
     * the owned fortunes, then the active pending chances. The character is the most fundamental
     * run identity so its effects resolve before items.
     */
    private List<GameEventListener> collectListeners() {
        List<GameEventListener> listeners = new ArrayList<>();
        listeners.addAll(player.getCharacterListeners());
        listeners.addAll(player.getInventory().getFortunes());
        listeners.addAll(pendingChanceManager.getActiveChances());
        // the active boss's debuff resolves last, on top of the player's own engine
        if (bossListeners != null) {
            listeners.addAll(bossListeners);
        }
        return listeners;
    }

    /** Fired right before the wheel and ball start spinning. */
    public void dispatchSpinStart() {
        for (GameEventListener listener : collectListeners()) {
            listener.onSpinStart(this);
        }
    }

    /** Fired while assembling the balls for a spin; listeners may add balls via the context. */
    public void dispatchPrepareSpin(final SpinContext spin) {
        for (GameEventListener listener : collectListeners()) {
            listener.onPrepareSpin(this, spin);
        }
    }

    /** Fired once the landing has been rolled; listeners may override it via the context. */
    public void dispatchBallLanded(final LandingContext landing) {
        for (GameEventListener listener : collectListeners()) {
            listener.onBallLanded(this, landing);
        }
    }

    /** Fired for a single bet during resolution; listeners contribute payout modifiers/refunds. */
    public void dispatchResolveBet(final BetResolution resolution) {
        for (GameEventListener listener : collectListeners()) {
            listener.onResolveBet(this, resolution);
        }
    }

    /**
     * Activates all <code>onTurnChange</code> effects.
     * Afterwards, removes all expired chances from the active chance list.
     */
    public void applyOnTurnChangeEffects() {
        getWheel().onTurnChange();
        for (GameEventListener listener : collectListeners()) {
            listener.onTurnChange(this);
        }
        pendingChanceManager.removeDeadChances();
    }

    public DeleteSegmentShopItem getPendingDeleteItem() {
        return pendingDeleteItem;
    }

    public void setPendingDeleteItem(final DeleteSegmentShopItem pendingDeleteItem) {
        this.pendingDeleteItem = pendingDeleteItem;
    }

    public WheelSelectChance getPendingChanceItem() {
        return pendingChanceItem;
    }

    public void setPendingChanceItem(WheelSelectChance pendingChanceItem) {
        this.pendingChanceItem = pendingChanceItem;
    }

    public void setCrystalBallSegment(Segment crystalBallSegment) {
        this.crystalBallSegment = crystalBallSegment;
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
     * Computes the return of the bets for the current segment. Clears the bets afterwards.
     */
    public void applyReturnOfBets(final Segment segment) {
        applyReturnOfBets(Collections.singletonList(segment));
    }

    /**
     * Computes the combined return of the bets across every ball's landing segment (each ball
     * resolves the bets independently and the payouts sum), then clears the bets. With a single
     * ball this is equivalent to {@link #applyReturnOfBets(Segment)}.
     */
    public void applyReturnOfBets(final List<Segment> segments) {
        player.earn(betManager.computeReturn(segments, this));
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
                if (BossRoster.hasBoss(currentStage)) {
                    // a boss gates this stage: defer the win/shop transition until it is beaten
                    startBossRound();
                } else {
                    advancePastStage(game);
                }
            } else { // game over
                game.setScreen(new GameOverScreen(game));
            }
        }
    }

    /** Win the run (final goal cleared) or open the shop for the next stage. */
    private void advancePastStage(final RougeletteGame game) {
        if (player.getBalance() >= finalGoal) {
            game.setScreen(new YouWinScreen(game, game.getScreen()));
        } else {
            startShopPhase();
        }
    }

    // ----- Boss encounter -----

    /**
     * Begins a boss encounter for the just-cleared stage: rolls the boss, snapshots the balance and
     * computes the gain goal, and shows the intro. The debuff is <em>not</em> activated yet (see
     * {@link #bossListeners}) so the stage-clearing spin and the intro screen play clean.
     */
    private void startBossRound() {
        currentBoss = BossRoster.forStage(currentStage);
        bossStartBalance = player.getBalance();
        bossGoal = Math.max(1, Math.round(bossStartBalance * currentBoss.getGoalFraction()));
        bossSpinsRemaining = currentBoss.getSpinCount();
        bossListeners = null;
        setState(GameStateMode.BOSS_INTRO);
    }

    /** Dismisses the intro and starts the fight proper: the debuff and any wheel mutation go live. */
    public void beginBossFight() {
        if (currentBoss == null) {
            return;
        }
        bossListeners = currentBoss.createListeners();
        currentBoss.applyWheelMutation(wheel);
        setState(GameStateMode.BOSS_FIGHT);
    }

    /** True only while a boss fight is actually in progress (debuff live). */
    public boolean isBossFightActive() {
        return bossListeners != null;
    }

    /**
     * Resolves one spin of the boss fight, called after the spin's payout and turn-change effects.
     * Decrements the remaining spins and checks the gain goal: reaching it defeats the boss (offer
     * the reward), running out of spins (or going broke) loses the run. Otherwise the fight continues.
     */
    public void resolveBossSpin(final RougeletteGame game) {
        if (player.isDead()) {
            endBossFight();
            game.setScreen(new GameOverScreen(game));
            return;
        }
        bossSpinsRemaining--;
        long gained = player.getBalance() - bossStartBalance;
        if (gained >= bossGoal) {
            // boss defeated -> offer the legendary reward
            endBossFight();
            bossRewardOffer = LegendaryPool.drawOffer();
            setState(GameStateMode.BOSS_REWARD);
        } else if (bossSpinsRemaining <= 0) {
            // out of spins, goal not met -> the boss wins
            endBossFight();
            game.setScreen(new GameOverScreen(game));
        }
        // otherwise: stay in the fight (caller restores BOSS_FIGHT)
    }

    /** Tears down the live debuff and reverts any wheel mutation (keeps currentBoss for the reward UI). */
    private void endBossFight() {
        if (currentBoss != null) {
            currentBoss.revertWheelMutation(wheel);
        }
        bossListeners = null;
    }

    /**
     * Picks a boss reward. If the chosen item's inventory section has room it is granted and the run
     * advances; otherwise the choice is held as {@link #pendingReward} and the player must discard an
     * item to free a slot (see {@link #tryClaimPendingReward}).
     */
    public void chooseBossReward(final RougeletteGame game, final int index) {
        if (bossRewardOffer == null || pendingReward != null || index < 0 || index >= bossRewardOffer.size()) {
            return;
        }
        ShopItem chosen = bossRewardOffer.get(index);
        if (sectionFull(chosen)) {
            pendingReward = chosen; // wait for the player to free a slot
            return;
        }
        LegendaryPool.grant(this, chosen);
        finishBossReward(game);
    }

    /** After a discard frees a slot, grants the held reward (if any) and advances the run. */
    public void tryClaimPendingReward(final RougeletteGame game) {
        if (pendingReward != null && !sectionFull(pendingReward)) {
            LegendaryPool.grant(this, pendingReward);
            finishBossReward(game);
        }
    }

    private void finishBossReward(final RougeletteGame game) {
        bossRewardOffer = null;
        pendingReward = null;
        currentBoss = null;
        advancePastStage(game);
    }

    private boolean sectionFull(final ShopItem item) {
        if (item instanceof de.mario.roguelette.items.fortunes.FortuneShopItem) {
            return player.getInventory().fortunesFull();
        }
        if (item instanceof de.mario.roguelette.items.chances.ChanceShopItem) {
            return player.getInventory().chancesFull();
        }
        return false;
    }

    /** Whether a chosen boss reward is waiting on the player to discard an item to make room. */
    public boolean isAwaitingRewardDiscard() {
        return pendingReward != null;
    }

    public ShopItem getPendingReward() {
        return pendingReward;
    }

    /** Discards (no refund) the fortune at the given inventory index. */
    public void discardFortune(final int index) {
        player.getInventory().popFortuneAtIndex(index);
    }

    /** Discards (no refund) the chance at the given inventory index. */
    public void discardChance(final int index) {
        player.getInventory().popChanceAtIndex(index);
    }

    public Boss getCurrentBoss() {
        return currentBoss;
    }

    public int getBossSpinsRemaining() {
        return bossSpinsRemaining;
    }

    public long getBossGoal() {
        return bossGoal;
    }

    /** Chips gained so far this boss fight (can be negative if the player is down). */
    public long getBossGained() {
        return player.getBalance() - bossStartBalance;
    }

    public List<ShopItem> getBossRewardOffer() {
        return bossRewardOffer;
    }

    private void startShopPhase() {
        setState(GameStateMode.SHOP_OPEN);

        currentStage++;
        currentRound = 1;
        roundsInStage = getRoundsForStage(currentStage);
        requiredChips = getRequiredChipsForStage(currentStage);

        shop.startStage(currentStage, getPriceMultiplier(currentStage));
    }

    public int getScaledRestockPrice() {
        return shop.getRestockPrice(getPriceMultiplier(currentStage));
    }

    public void startNextRound() {
        setState(GameStateMode.DEFAULT);
    }

    private int getRoundsForStage(int stage) {
        return STAGE_ROUNDS[Math.min(stage, STAGE_ROUNDS.length) - 1];
    }

    private long getRequiredChipsForStage(int stage) {
        return STAGE_TARGETS[Math.min(stage, STAGE_TARGETS.length) - 1];
    }

    /**
     * Prices scale with the target curve so an item stays a roughly constant fraction of your
     * bankroll across the whole run, instead of the old runaway 10x/stage that priced you out of
     * the shop in the late game. The opening shop (stage 1) uses base prices; every later shop
     * opens having just cleared the previous stage, so we scale by that stage's target.
     */
    private int getPriceMultiplier(int stage) {
        if (stage <= 1) return 1;
        return Math.max(1, Math.round(STAGE_TARGETS[stage - 2] / 100f));
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
