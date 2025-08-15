package de.mario.roguelette.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import de.mario.roguelette.GameState;
import de.mario.roguelette.Player;
import de.mario.roguelette.RougeletteGame;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.BetType;
import de.mario.roguelette.items.Inventory;
import de.mario.roguelette.items.Shop;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.segments.AddSegmentShopItem;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
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

import java.util.Optional;

public class GameScreen implements Screen {

    private static final float MIN_SEGMENTS = 12;
    private static final float MAX_SEGMENTS = 60;

    private final RougeletteGame game;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private WheelRenderer wheelRenderer;
    private BettingAreaRenderer bettingAreaRenderer;
    private ChipRenderer chipRenderer;
    private ShopRenderer shopRenderer;
    private InventoryRenderer inventoryRenderer;
    private ActiveChanceEffectsRenderer activeChanceEffectsRenderer;
    private CrystalBallRenderer crystalBallRenderer;

    private GameState gameState;

    public GameScreen(RougeletteGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        Inventory inventory = new Inventory();
        Player player = new Player(inventory, 100, "Yannik");
        Wheel wheel = WheelFactory.createClassicWheel();
        BetManager betManager = new BetManager();
        Shop shop = new Shop();
        gameState = new GameState(player, wheel, betManager, shop);
        gameState.setState(GameState.GameStateMode.SHOP_OPEN); // start with open shop

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
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

        float shopStartX = betStartX;
        float shopStartY = 20f;
        float shopWidth = betWidth;
        float shopHeight = Gdx.graphics.getHeight() / 2f;
        shopRenderer = new ShopRenderer(shapeRenderer, batch, font, gameState, new Rectangle(shopStartX, shopStartY, shopWidth, shopHeight));

        float inventoryStartX = betStartX;
        float inventoryStartY = Gdx.graphics.getHeight() / 5f * 3;
        float inventoryWidth = betWidth;
        float inventoryHeight = Gdx.graphics.getHeight() / 5f * 2 - 20;
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
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        font.getData().setScale(1.5f);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // update everything
        gameState.update(delta);
        wheelRenderer.update(delta);

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
        font.draw(batch, "Balance: $" + gameState.getBalanceMinusBets(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Goal: $" + gameState.getRequiredChips(), 444, Gdx.graphics.getHeight() - 20);
        font.getData().setScale(2f);
        String s = gameState.isStateInStack(GameState.GameStateMode.SHOP_OPEN) ? "Shopping Time!" : String.format("Stage: %d, Round %d/%d", gameState.getCurrentStage(), gameState.getCurrentRound(), gameState.getRoundsInStage());
        font.draw(batch, s, 20, Gdx.graphics.getHeight() - 70);


        // draw delete segment instructions
        if (gameState.getCurrentState() == GameState.GameStateMode.DELETE_SEGMENT_SELECTING) {
            // for now, give a hint as written text
            font.draw(batch, "Select segment to delete", Gdx.graphics.getWidth() / 2f + 35, Gdx.graphics.getHeight() / 2f + 60);
        }
        batch.end();

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
        font.dispose();
    }

    private void handleInputDeleteSegmentSelecting() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            // right click to cancel
            gameState.popState();
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (wheelRenderer.contains(touchPos.x, touchPos.y)) {
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

    private void handleInputDefault() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        if (wheelRenderer.contains(touchPos.x, touchPos.y)
            && gameState.betsNotEmpty()) {
            gameState.getPlayer().resetHand();

            float selectAngle = MathUtils.random(0f, 360f); // segment at this angle will be selected
            float targetAngle = MathUtils.random(0f, 360f); // rotation where this segment is going to be at the end

            // if the crystal ball item is active and still available, use this segment instead
            if (gameState.getCrystalBallSegment() != null) {
                Optional<Float> optStartAngle = wheelRenderer.findStartAngleForSegment(gameState.getCrystalBallSegment());
                if (optStartAngle.isPresent()) {
                    selectAngle = optStartAngle.get() + 1f; // +1 because checking exactly on the border can lead to selecting a neighbor
                }
            }
            Segment segment = wheelRenderer.getCurrentSegment(selectAngle);


            float halfSweepAngle = 360f / (2*gameState.getWheel().size());
            float center = wheelRenderer.getCurrentSegmentStartAngle(selectAngle) + halfSweepAngle; // center angle of segment we are targeting
            System.out.println("select: " + selectAngle + ", target: " + targetAngle + ", s: " + segment.getShortDescription());

            gameState.setState(GameState.GameStateMode.SPINNING);
            wheelRenderer.spinWheelToTarget(targetAngle - center);
            wheelRenderer.spinBallToTarget(1000f, targetAngle);
            wheelRenderer.setBallListener(() -> {
                // turn change
                gameState.setState(GameState.GameStateMode.DEFAULT);
                gameState.getPlayer().pay(gameState.getBetManager().totalAmount());
                gameState.applyReturnOfBets(segment);
                gameState.endRound(game);

                gameState.applyOnTurnChangeEffects();

                wheelRenderer.updateWheel();
                chipRenderer.updateChips();
                bettingAreaRenderer.updateBetValues();
                activeChanceEffectsRenderer.updateChances();

                gameState.resetCrystalBallSegment();
                shopRenderer.updateItems();
            });
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Optional<Integer> optChipValue = chipRenderer.handleLeftClick(touchPos.x, touchPos.y);
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

    private void handleInputShop() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
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
                case SHOP_OPEN:
                    handleInputShop();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            && gameState.isStateInStack(GameState.GameStateMode.SHOP_OPEN)) {

            gameState.startNextRound();
        }

//        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
//            switch (gameState.getCurrentState()) {
//                case DEFAULT:
//                    gameState.setState(GameState.GameStateMode.SHOP_OPEN);
//                    break;
//                case SHOP_OPEN:
//                    gameState.setState(GameState.GameStateMode.DEFAULT);
//            }
//        }
    }

    private void handleHoverShop() {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        shopRenderer.handleHover(mousePos.x,  mousePos.y);
        inventoryRenderer.handleHover(mousePos.x, mousePos.y);
    }

    private void handleHoverDefault() {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        inventoryRenderer.handleHover(mousePos.x,  mousePos.y);
    }

    private void handleHover() {
        switch (gameState.getCurrentState()) {
            case SHOP_OPEN:
                handleHoverShop();
                break;
            case SPINNING:
            case DEFAULT:
                handleHoverDefault();
                break;
            case DELETE_SEGMENT_SELECTING:
        }
    }

}
