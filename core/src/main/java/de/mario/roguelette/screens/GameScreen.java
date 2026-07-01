package de.mario.roguelette.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.Player;
import de.mario.roguelette.RougeletteGame;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.config.RunConfig;
import de.mario.roguelette.characters.Character;
import de.mario.roguelette.betting.BetType;
import de.mario.roguelette.events.LandingContext;
import de.mario.roguelette.events.SpinContext;
import de.mario.roguelette.items.Inventory;
import de.mario.roguelette.items.Shop;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.chances.WheelSelectChance;
import de.mario.roguelette.items.segments.AddSegmentShopItem;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
import de.mario.roguelette.render.BackgroundRenderer;
import de.mario.roguelette.render.BossRenderer;
import de.mario.roguelette.render.bet.BettingAreaRenderer;
import de.mario.roguelette.render.bet.ChipRenderer;
import de.mario.roguelette.render.item.ActiveChanceEffectsRenderer;
import de.mario.roguelette.render.item.CrystalBallRenderer;
import de.mario.roguelette.render.item.InventoryRenderer;
import de.mario.roguelette.render.item.ShopRenderer;
import de.mario.roguelette.render.wheel.WheelRenderer;
import de.mario.roguelette.util.BetManager;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.Wheel;
import de.mario.roguelette.wheel.WheelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameScreen implements Screen {

    private static final float MIN_SEGMENTS = 12;
    private static final float MAX_SEGMENTS = 60;

    private final RougeletteGame game;
    private final Character character;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private BackgroundRenderer backgroundRenderer;
    private WheelRenderer wheelRenderer;
    private BettingAreaRenderer bettingAreaRenderer;
    private ChipRenderer chipRenderer;
    private ShopRenderer shopRenderer;
    private InventoryRenderer inventoryRenderer;
    private ActiveChanceEffectsRenderer activeChanceEffectsRenderer;
    private CrystalBallRenderer crystalBallRenderer;
    private BossRenderer bossRenderer;

    private GameState gameState;

    public GameScreen(RougeletteGame game, Character character) {
        this.game = game;
        this.character = character;
    }


    @Override
    public void show() {
        // The run's rule set: baseline defaults seeded from the character. Casino Curses will
        // apply their modifiers here, between baseline() and the run construction below.
        RunConfig runConfig = RunConfig.baseline(character);

        Inventory inventory = new Inventory();
        Player player = new Player(inventory, character, runConfig.getStartingBalance());
        Wheel wheel = WheelFactory.createClassicWheel();
        BetManager betManager = new BetManager();
        Shop shop = new Shop(runConfig);
        gameState = new GameState(player, wheel, betManager, shop, game.getMusicManager(), runConfig);
        gameState.setState(GameState.GameStateMode.SHOP_OPEN); // start with open shop

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        backgroundRenderer = new BackgroundRenderer();
        font = game.getFontManager().getDefault();
        font.getData().setScale(1.5f);

        float wheelRadius = Gdx.graphics.getHeight() / 3f;
        float wheelInnerRadius = wheelRadius * 0.6f;
        float wheelOuterRadius = wheelRadius * 1.2f;
        float wheelCenterX = wheelRadius * 1.2f + 50;
        float wheelCenterY = Gdx.graphics.getHeight() / 2f;
        wheelRenderer = new WheelRenderer(shapeRenderer, batch, font, gameState, wheelRadius, wheelInnerRadius, wheelOuterRadius, wheelCenterX, wheelCenterY);

        float betStartX = Gdx.graphics.getWidth() / 2f + 35;
        float betWidth = Gdx.graphics.getWidth() * 4f / 9f - 20;
        float betHeight = betWidth / 4;
        float betStartY = betHeight + 35;
        bettingAreaRenderer = new BettingAreaRenderer(shapeRenderer, batch, font, gameState, new Rectangle(betStartX, betStartY, betWidth, betHeight));

        float chipStartX = betStartX;
        float chipWidth = betWidth;
        float chipStartY = betStartY - betHeight + 20;
        float chipRadius = 30;
        chipRenderer = new ChipRenderer(shapeRenderer, batch, font, gameState, new Rectangle(chipStartX, chipStartY, chipWidth,0) , chipRadius);

        float shopStartX = betStartX - 3; // a little bit to the left
        float shopStartY = 20f;
        float shopWidth = betWidth * 13f/12 + 6; // and a little bit to the right
        float shopHeight = Gdx.graphics.getHeight() / 2f * 1.2f;
        shopRenderer = new ShopRenderer(shapeRenderer, batch, font, gameState, new Rectangle(shopStartX, shopStartY, shopWidth, shopHeight));
        shopRenderer.setListenerContinue(() -> gameState.startNextRound());
        shopRenderer.setListenerRestock(() -> {
            int price = gameState.getScaledRestockPrice();
            if (player.canAfford(price)) {
                player.pay(price);
                shop.restockItems();
                shopRenderer.updateItems();
            }
        });

        float inventoryStartX = shopStartX;
        float inventoryStartY = Gdx.graphics.getHeight() / 7f *5f-40;
        float inventoryWidth = shopWidth;
        float inventoryHeight = Gdx.graphics.getHeight() / 3.5f;
        inventoryRenderer = new InventoryRenderer(shapeRenderer, batch, font, gameState, new Rectangle(inventoryStartX, inventoryStartY, inventoryWidth, inventoryHeight));

        float chanceStartX = 50;
        float chanceStartY = 20;
        float chanceWidth = Gdx.graphics.getWidth() / 3f;
        float chanceHeight = Gdx.graphics.getHeight() / 15f;
        activeChanceEffectsRenderer = new ActiveChanceEffectsRenderer(shapeRenderer, batch, font, gameState, new Rectangle(chanceStartX, chanceStartY, chanceWidth, chanceHeight));

        float crystalHeight = Gdx.graphics.getHeight() / 2f;
        float crystalWidth = crystalHeight;
        float crystalStartX = Gdx.graphics.getWidth() / 2f - crystalWidth / 2f;
        float crystalStartY = Gdx.graphics.getHeight() / 2f - crystalHeight / 2f;
        crystalBallRenderer = new CrystalBallRenderer(shapeRenderer, batch, font, gameState, new Rectangle(crystalStartX, crystalStartY, crystalWidth, crystalHeight));

        bossRenderer = new BossRenderer(shapeRenderer, batch, font, gameState);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        font.getData().setScale(1.5f);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // render gradient background
        backgroundRenderer.render(batch);

        // update everything
        gameState.update(delta);
        wheelRenderer.update(delta);
        game.getMusicManager().update(delta);

        // render everything
        wheelRenderer.render();
        bettingAreaRenderer.render();
        chipRenderer.render();
        activeChanceEffectsRenderer.render();
        inventoryRenderer.render();

        if (gameState.isStateInStack(GameState.GameStateMode.SHOP_OPEN)) {
            shopRenderer.render();
        }
        if (gameState.getCurrentState()  == GameState.GameStateMode.SHOW_CRYSTAL_BALL) {
            crystalBallRenderer.render();
        }


        // render chip
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        chipRenderer.renderChipInHand(mousePos.x, mousePos.y);

        // draw progression info
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3f);
        String balanceText = "Balance: $" + gameState.getBalanceMinusBets();
        font.draw(batch, balanceText, 20, Gdx.graphics.getHeight() - 20);
        // Position Goal after Balance with spacing
        GlyphLayout balanceLayout = new GlyphLayout(font, balanceText);
        float goalX = 20 + balanceLayout.width + 40; // 40px spacing
        font.draw(batch, "Goal: $" + gameState.getRequiredChips(), goalX, Gdx.graphics.getHeight() - 20);
        font.getData().setScale(2f);
        String s;
        if (gameState.getCurrentBoss() != null) {
            // covers the whole encounter (intro/fight/reward) incl. the SPINNING frames mid-fight,
            // which otherwise fell through to a stale "Round n+1/n"
            s = "Boss Fight!";
        } else if (gameState.isStateInStack(GameState.GameStateMode.SHOP_OPEN)) {
            s = "Shopping Time!";
        } else {
            s = String.format("Stage: %d, Round %d/%d", gameState.getCurrentStage(), gameState.getCurrentRound(), gameState.getRoundsInStage());
        }
        font.draw(batch, s, 20, Gdx.graphics.getHeight() - 70);


        // draw select segment instructions
        if (gameState.getCurrentState() == GameState.GameStateMode.DELETE_SEGMENT_SELECTING) {
            font.draw(batch, "Select segment to delete", Gdx.graphics.getWidth() / 2f + 35, Gdx.graphics.getHeight() / 2f * 1.2f + 60);
        }
        if (gameState.getCurrentState() == GameState.GameStateMode.CHANCE_SEGMENT_SELECTING) {
            font.draw(batch, "Select segment to activate chance", Gdx.graphics.getWidth() / 2f + 35, Gdx.graphics.getHeight() / 2f * 1.2f + 60);
        }
        batch.end();

        // boss overlays (intro card / in-fight HUD / reward picker)
        bossRenderer.render();

        // during a boss-reward overflow, redraw the inventory above the dim so its discard buttons work
        if (gameState.getCurrentState() == GameState.GameStateMode.BOSS_REWARD
            && gameState.isAwaitingRewardDiscard()) {
            inventoryRenderer.render();
        }

        // handle input
        handleInput();
        handleHover();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);

        // TODO: resize everything
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        backgroundRenderer.dispose();
        // font is managed by RougeletteGame.fontManager
    }

    private void handleInputChanceSegmentSelecting() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            // right click to cancel
            gameState.popState();
            //TODO this is really not elegant due to various reasons but for now it works
            gameState.getPlayer().getInventory().addChance((ChanceShopItem) gameState.getPendingChanceItem());
            gameState.setPendingChanceItem(null);
            inventoryRenderer.updateItems();
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            Optional<Integer> optSegmentIndex = wheelRenderer.getSegmentIndexAt(touchPos.x, touchPos.y);
            optSegmentIndex.ifPresent(index -> {
                WheelSelectChance chance = gameState.getPendingChanceItem();
                if (chance != null) { // should always be true...
                    chance.onActivate(gameState, index);
                    wheelRenderer.updateWheel();
                    activeChanceEffectsRenderer.updateChances();
                }
            });
        }

    }

    private void handleInputDeleteSegmentSelecting() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            // right click to cancel
            gameState.popState();
            gameState.setPendingDeleteItem(null);
            return;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            // Just going by the index has two advantages
            // first, we do not need equals for Segments
            // second, there might be equivalent segments in the wheel
            Optional<Integer> optSegmentIndex = wheelRenderer.getSegmentIndexAt(touchPos.x, touchPos.y);
            optSegmentIndex.ifPresent(index -> {
                DeleteSegmentShopItem dsi = gameState.getPendingDeleteItem();
                if (dsi != null) { // this should always be the case ...
                    if (dsi.tryBuy(gameState, index)) {
                        wheelRenderer.updateWheel();
                        chipRenderer.updateChips();
                    }
                }
            });
        }
    }

    private void handleInventory(float x, float y) {
        Optional<Integer> optChanceIndex = inventoryRenderer.handleLeftClickChance(x, y);
        optChanceIndex.ifPresent(chanceIndex -> {
            gameState.getPlayer().getInventory().popChanceAtIndex(chanceIndex).onActivate(gameState);
            activeChanceEffectsRenderer.updateChances();
            inventoryRenderer.updateItems();
            crystalBallRenderer.updateSegment();
        });
    }

    /**
     * Resolves the bets and spins the wheel. The landings (one per ball; usually just the default
     * ball, more if a ball modifier such as Double Ball is active) are decided up front via the
     * event layer, then the balls are animated to them and the payout applied when all have stopped.
     */
    private void startSpin() {
        gameState.getPlayer().resetHand();

        // whether this spin counts toward an active boss fight (captured before resolution can end it)
        final boolean bossFight = gameState.isBossFightActive();

        float selectAngle = MathUtils.random(0f, 360f); // segment at this angle will be selected
        float targetAngle = MathUtils.random(0f, 360f); // rotation where this segment is going to be at the end

        // if the crystal ball item is active and still available, use this segment instead
        if (gameState.getCrystalBallSegment() != null) {
            Optional<Float> optStartAngle = wheelRenderer.findStartAngleForSegment(gameState.getCrystalBallSegment());
            if (optStartAngle.isPresent()) {
                selectAngle = optStartAngle.get() + 1f; // +1 because checking exactly on the border can lead to selecting a neighbor
            }
        }

        // event layer: notify listeners of the spin and let them override where the ball lands
        // (e.g. Freeze forces a segment, Ricochet bounces off zero, ball modifiers, ...)
        gameState.dispatchSpinStart();

        // primary ball: roll its landing and let listeners retarget it
        LandingContext landing = new LandingContext(gameState.getWheel(), wheelRenderer.getCurrentSegmentIndex(selectAngle));
        gameState.dispatchBallLanded(landing);
        if (landing.wasChanged()) {
            selectAngle = wheelRenderer.getSelectAngleForIndex(landing.getSegmentIndex());
        }
        int primaryIndex = landing.getSegmentIndex();

        // the wheel rotation is fixed by the primary ball; every ball lands relative to it
        float halfSweepAngle = 360f / (2 * gameState.getWheel().size());
        float center = wheelRenderer.getCurrentSegmentStartAngle(selectAngle) + halfSweepAngle; // center angle of the primary segment
        float wheelRotation = targetAngle - center;

        // assemble the balls for this spin: the player's default ball plus any added by listeners
        SpinContext spin = new SpinContext();
        spin.addBall(gameState.getPlayer().getCharacter().createSignatureBall());
        gameState.dispatchPrepareSpin(spin);

        // assign each ball a landing (the primary keeps its rolled index; extras roll their own,
        // also subject to landing listeners), then resolve screen targets against the fixed rotation
        List<Segment> landedSegments = new ArrayList<>();
        List<Ball> landedBalls = new ArrayList<>(); // parallel to landedSegments (per-ball payout factor)
        List<Float> ballTargets = new ArrayList<>();
        List<Color> ballTints = new ArrayList<>();
        for (int b = 0; b < spin.ballCount(); b++) {
            int idx;
            if (b == 0) {
                idx = primaryIndex;
            } else {
                LandingContext extra = new LandingContext(gameState.getWheel(), MathUtils.random(gameState.getWheel().size() - 1));
                gameState.dispatchBallLanded(extra);
                idx = extra.getSegmentIndex();
            }
            landedSegments.add(gameState.getWheel().getSegmentAt(idx));
            landedBalls.add(spin.getBalls().get(b));
            ballTargets.add(wheelRenderer.getSegmentCenterBaseAngle(idx) + wheelRotation);
            ballTints.add(spin.getBalls().get(b).getTint());
        }

        gameState.setState(GameState.GameStateMode.SPINNING);
        wheelRenderer.spinWheelToTarget(wheelRotation);
        wheelRenderer.spinBalls(ballTargets, ballTints, () -> {
            // turn change
            gameState.getPlayer().pay(gameState.getBetManager().totalAmount());
            gameState.applyReturnOfBets(landedSegments, landedBalls);

            if (bossFight) {
                // the boss debuff's turn effects (e.g. the Leech's tithe) apply first, then we check
                // whether this spin met the gain goal / ran out of spins
                gameState.applyOnTurnChangeEffects();
                gameState.resolveBossSpin(game);
                if (gameState.isBossFightActive()) {
                    gameState.setState(GameState.GameStateMode.BOSS_FIGHT); // fight continues
                }
            } else {
                gameState.setState(GameState.GameStateMode.DEFAULT);
                gameState.endRound(game); // may start a boss round (-> BOSS_INTRO) or open the shop
                gameState.applyOnTurnChangeEffects();
            }

            wheelRenderer.updateWheel();
            chipRenderer.updateChips();
            bettingAreaRenderer.updateBetValues();
            activeChanceEffectsRenderer.updateChances();

            gameState.resetCrystalBallSegment();
            shopRenderer.updateItems();
        });
    }

    private void handleInputDefault() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        if (wheelRenderer.contains(touchPos.x, touchPos.y) && gameState.betsNotEmpty()) {
            startSpin();
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Optional<Long> optChipValue = chipRenderer.handleLeftClick(touchPos.x, touchPos.y);
            optChipValue.ifPresent(value -> {
                gameState.getPlayer().increaseHandBy(value);
                chipRenderer.updateChips();
            });

            Optional<Bet> optBet = bettingAreaRenderer.handleLeftClick(touchPos.x, touchPos.y);
            optBet.ifPresent(bet -> {
                gameState.getBetManager().addBet(bet);
                bettingAreaRenderer.updateBetValues();
                if (gameState.getBalanceMinusBets() < gameState.getPlayer().getCurrentlyInHand()) {
                    gameState.getPlayer().resetHand();
                }
                chipRenderer.updateChips();
            });

            handleInventory(touchPos.x, touchPos.y);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (gameState.getPlayer().getCurrentlyInHand() > 0) {
                gameState.getPlayer().resetHand();
                chipRenderer.updateChips();
            } else {
                Optional<BetType> optBetType = bettingAreaRenderer.handleRightClick(touchPos.x, touchPos.y);
                optBetType.ifPresent(betType -> {
                    gameState.getBetManager().removeBet(betType);
                    bettingAreaRenderer.updateBetValues();
                    chipRenderer.updateChips();
                });
            }
        }
    }

    /** @return true if a click hit an inventory discard (X) button and an item was discarded. */
    private boolean handleInventoryDiscard(float x, float y) {
        Optional<Integer> optFortune = inventoryRenderer.handleDiscardFortune(x, y);
        if (optFortune.isPresent()) {
            gameState.discardFortune(optFortune.get());
            inventoryRenderer.updateItems();
            activeChanceEffectsRenderer.updateChances();
            return true;
        }
        Optional<Integer> optChance = inventoryRenderer.handleDiscardChance(x, y);
        if (optChance.isPresent()) {
            gameState.discardChance(optChance.get());
            inventoryRenderer.updateItems();
            activeChanceEffectsRenderer.updateChances();
            return true;
        }
        return false;
    }

    private void handleInputShop() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (handleInventoryDiscard(touchPos.x, touchPos.y)) {
                return; // discarding an item takes precedence over buying/activating
            }
            Optional<ShopItem> optShopItem = shopRenderer.handleLeftClick(touchPos.x, touchPos.y);
            optShopItem.ifPresent(shopItem -> {
                // make sure that the number of wheel segments stays within the specified range
                if ((shopItem instanceof AddSegmentShopItem && gameState.getWheel().size() >= MAX_SEGMENTS) ||
                    (shopItem instanceof DeleteSegmentShopItem && gameState.getWheel().size() <= MIN_SEGMENTS)) {
                    return;
                }
                if (shopItem.tryBuy(gameState)) {
                    activeChanceEffectsRenderer.updateChances(); //TODO move this when there is an inventory
                    crystalBallRenderer.updateSegment();
                    wheelRenderer.updateWheel();
                    chipRenderer.updateChips();
                    inventoryRenderer.updateItems();
                }
            });

            handleInventory(touchPos.x, touchPos.y);
        }
    }

    private void handleInputBossIntro() {
        // any click begins the fight
        gameState.beginBossFight();
    }

    private void handleInputBossReward() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if (gameState.isAwaitingRewardDiscard()) {
                // a reward is chosen but the inventory is full: a discard frees the slot and claims it
                if (handleInventoryDiscard(touchPos.x, touchPos.y)) {
                    gameState.tryClaimPendingReward(game);
                    inventoryRenderer.updateItems();
                    shopRenderer.updateItems();
                }
                return;
            }
            int index = bossRenderer.getRewardIndexAt(touchPos.x, touchPos.y);
            if (index >= 0) {
                gameState.chooseBossReward(game, index);
                inventoryRenderer.updateItems();
                activeChanceEffectsRenderer.updateChances();
                // claiming the reward advances to the next stage's freshly-stocked shop; resync the
                // shop renderer so it doesn't keep showing the previous stage's (partly sold-out) stock
                shopRenderer.updateItems();
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            switch (gameState.getCurrentState()) {
                case DEFAULT:
                    handleInputDefault();
                    break;
                case SPINNING:
                    // ignore all input while roulette is spinning
                    break;
                case DELETE_SEGMENT_SELECTING:
                    handleInputDeleteSegmentSelecting();
                    break;
                case CHANCE_SEGMENT_SELECTING:
                    handleInputChanceSegmentSelecting();
                    break;
                case SHOP_OPEN:
                    handleInputShop();
                    break;
                case BOSS_INTRO:
                    handleInputBossIntro();
                    break;
                case BOSS_FIGHT:
                    // identical betting/spinning to a normal round; the boss debuff lives in the event layer
                    handleInputDefault();
                    break;
                case BOSS_REWARD:
                    handleInputBossReward();
                    break;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            && gameState.isStateInStack(GameState.GameStateMode.SHOP_OPEN)) {

            gameState.startNextRound();
        }

//        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
//            game.getMusicManager().setShopMode();
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
//            game.getMusicManager().setDefaultMode();
//        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
//            game.getMusicManager().setCrystalBallMode();
//        }
    }

    private void handleHoverShop() {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        shopRenderer.handleHover(mousePos.x,  mousePos.y);
        inventoryRenderer.handleHover(mousePos.x, mousePos.y);
        activeChanceEffectsRenderer.handleHover(mousePos.x, mousePos.y);
    }

    private void handleHoverDefault() {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        inventoryRenderer.handleHover(mousePos.x,  mousePos.y);
        activeChanceEffectsRenderer.handleHover(mousePos.x, mousePos.y);
    }

    private void handleHover() {
        switch (gameState.getCurrentState()) {
            case SHOP_OPEN:
                handleHoverShop();
                break;
            // default behavior for all these should be fine
            case SPINNING:
            case DELETE_SEGMENT_SELECTING:
            case CHANCE_SEGMENT_SELECTING:
            case DEFAULT:
            case BOSS_FIGHT:
                handleHoverDefault();
                break;

            default:
                break;
        }
    }

}
