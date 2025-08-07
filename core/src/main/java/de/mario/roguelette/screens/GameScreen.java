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
import de.mario.roguelette.items.Shop;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.segments.AddSegmentShopItem;
import de.mario.roguelette.items.segments.DeleteSegmentShopItem;
import de.mario.roguelette.render.bet.BettingAreaRenderer;
import de.mario.roguelette.render.bet.ChipRenderer;
import de.mario.roguelette.render.shop.ShopRenderer;
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

    private GameState gameState;

    public GameScreen(RougeletteGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        Player player = new Player(100, "Yannik");
        Wheel wheel = WheelFactory.createClassicWheel();
        BetManager betManager = new BetManager();
        Shop shop = new Shop();
        gameState = new GameState(player, wheel, betManager, shop);

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

        float betStartX = Gdx.graphics.getWidth() / 2f + 25;
        float betWidth = Gdx.graphics.getWidth() * 2f / 5f;
        float betHeight = betWidth / 4;
        float betStartY = Gdx.graphics.getHeight() - betHeight - 20;
        bettingAreaRenderer = new BettingAreaRenderer(shapeRenderer, batch, font, gameState, new Rectangle(betStartX, betStartY, betWidth, betHeight));

        float chipStartX = betStartX;
        float chipWidth = betWidth;
        float chipStartY = betStartY - betHeight;
        float chipRadius = 30;
        chipRenderer = new ChipRenderer(shapeRenderer, batch, font, gameState, new Rectangle(chipStartX, chipStartY, chipWidth,0) , chipRadius);

        float shopStartX = betStartX;
        float shopStartY = 20f;
        float shopWidth = betWidth;
        float shopHeight = Gdx.graphics.getHeight() / 2f;
        shopRenderer = new ShopRenderer(shapeRenderer, batch, font, gameState, new Rectangle(shopStartX, shopStartY, shopWidth, shopHeight));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        font.getData().setScale(1.5f);
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        // update everything
        wheelRenderer.update(delta);

        // render everything
        wheelRenderer.render();
        bettingAreaRenderer.render();
        chipRenderer.render();
        shopRenderer.render();

        // render chip
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        chipRenderer.renderChipInHand(mousePos.x, mousePos.y);

        // draw current balance
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3f);
        font.draw(batch, "$" + gameState.getBalanceMinusBets(), 50, Gdx.graphics.getHeight() - 50);

        // draw delete segment instructions
        if (gameState.getMode() == GameState.GameStateMode.DELETE_SEGMENT_SELECTING) {
            // for now, give a hint as written text
            font.draw(batch, "Select segment to delete", 180, Gdx.graphics.getHeight() - 50);
        }
        batch.end();

        // handle input
        handleInput();
        handleHover();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);

        // resize wheel
        float radius = Gdx.graphics.getHeight() / 3f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        wheelRenderer.setRadii(radius, radius*0.6f, radius*1.2f);
        wheelRenderer.setCenter(radius*1.2f + 50, centerY);

        // TODO: resize the rest
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
            gameState.setMode(GameState.GameStateMode.DEFAULT);
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
                            chipRenderer.createChips();
                        }
                    }
                });
            }
        }
    }

    private void handleInputDefault() {
        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        if (wheelRenderer.contains(touchPos.x, touchPos.y)
            && gameState.betsNotEmpty()) {

            float selectAngle = MathUtils.random(0f, 360f); // segment at this angle will be selected
            float targetAngle = MathUtils.random(0f, 360f); // rotation where this segment is going to be at the end
            float currentAngle = wheelRenderer.getCurrentRotation();

            Segment segment = wheelRenderer.getCurrentSegment(selectAngle);
            float center = wheelRenderer.getCurrentSegmentStartAngle(selectAngle); // taking the start angle (in base rotation) seems to work very well, dunno why tbh
            System.out.println("select: " + selectAngle + ", target: " + targetAngle + ", current: " + currentAngle + ", s: " + segment.getDisplayText());

            gameState.setMode(GameState.GameStateMode.SPINNING);
            wheelRenderer.spinWheelToTarget(targetAngle - center);
            wheelRenderer.spinBallToTarget(1000f, targetAngle);
            wheelRenderer.setBallListener(() -> {
                gameState.setMode(GameState.GameStateMode.DEFAULT);
                gameState.getPlayer().pay(gameState.getBetManager().totalAmount());
                gameState.applyReturnOfBets(segment);
                if (gameState.getPlayer().isDead()) {
                    game.setScreen(new GameOverScreen(game));
                }
                chipRenderer.createChips();
                bettingAreaRenderer.updateBetValues();

                // also reset shop (?)
                gameState.getShop().refreshItems();
                shopRenderer.updateItems();
            });
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Optional<Integer> optChipValue = chipRenderer.handleLeftClick(touchPos.x, touchPos.y);
            optChipValue.ifPresent(value -> {
                gameState.getPlayer().increaseHandBy(value);
                chipRenderer.createChips();
            });

            Optional<Bet> optBet = bettingAreaRenderer.handleLeftClick(touchPos.x, touchPos.y);
            optBet.ifPresent(bet -> {
                gameState.getBetManager().addBet(bet);
                bettingAreaRenderer.updateBetValues();
                if (gameState.getBalanceMinusBets() < gameState.getPlayer().getCurrentlyInHand()) {
                    gameState.getPlayer().resetHand();
                }
                chipRenderer.createChips();
            });

            Optional<ShopItem> optShopItem = shopRenderer.handleLeftClick(touchPos.x, touchPos.y);
            optShopItem.ifPresent(shopItem -> {
                // make sure that the number of wheel segments stays within the specified range
                if ((shopItem instanceof AddSegmentShopItem && gameState.getWheel().size() >= MAX_SEGMENTS) ||
                    (shopItem instanceof DeleteSegmentShopItem && gameState.getWheel().size() <= MIN_SEGMENTS)) {
                    return;
                }
                if (shopItem.tryBuy(gameState)) {
                    wheelRenderer.updateWheel();
                    chipRenderer.createChips();
                }
            });
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            if (gameState.getPlayer().getCurrentlyInHand() > 0) {
                gameState.getPlayer().resetHand();
                chipRenderer.createChips();
            } else {
                Optional<BetType> optBetType = bettingAreaRenderer.handleRightClick(touchPos.x, touchPos.y);
                optBetType.ifPresent(betType -> {
                    gameState.getBetManager().removeBet(betType);
                    bettingAreaRenderer.updateBetValues();
                    chipRenderer.createChips();
                });
            }
        }
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            switch (gameState.getMode()) {
                case DEFAULT:
                    handleInputDefault();
                    break;
                case SPINNING:
                    // ignore all input while roulette is spinning
                    break;
                case DELETE_SEGMENT_SELECTING:
                    handleInputDeleteSegmentSelecting();
            }
        }
    }

    private void handleHoverDefault() {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        shopRenderer.handleHover(mousePos.x,  mousePos.y);
    }

    private void handleHover() {
        switch (gameState.getMode()) {
            case DEFAULT:
            case SPINNING:
                handleHoverDefault();
                break;
            case DELETE_SEGMENT_SELECTING:

        }
    }

}
