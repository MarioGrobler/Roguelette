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

        // draw current segment
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(3f);
        font.draw(batch, "$" + gameState.getBalanceMinusBets(), 50, Gdx.graphics.getHeight() - 50);

        handleInput();
        batch.end();

        wheelRenderer.update(delta);

        wheelRenderer.render();
        bettingAreaRenderer.render();
        chipRenderer.render();
        shopRenderer.render();

        // render chip
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        chipRenderer.renderChipInHand(mousePos.x, mousePos.y);
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

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (wheelRenderer.contains(touchPos.x, touchPos.y) && !wheelRenderer.isSpinning()) {
                //wheelRenderer.spinWheelToTarget(0);
                //wheelRenderer.infiniteBallSpin(300);
                //wheelRenderer.spinBallToTarget(400, 0);

                float selectAngle = MathUtils.random(0f, 360f); // segment at this angle will be selected
                float targetAngle = MathUtils.random(0f, 360f); // rotation where this segment is going to be at the end
                float currentAngle = wheelRenderer.getCurrentRotation();

                Segment segment = wheelRenderer.getCurrentSegment(selectAngle);
                float center = wheelRenderer.getCurrentSegmentStartAngle(selectAngle); // taking the start angle (in base rotation) seems to work very well, dunno why tbh
                System.out.println("select: " + selectAngle + ", target: " + targetAngle + ", current: " + currentAngle + ", s: " + segment.getDisplayText());

                wheelRenderer.spinWheelToTarget(targetAngle - center);
                wheelRenderer.spinBallToTarget(1000f, targetAngle);
                wheelRenderer.setBallListener(() -> {
                    gameState.getPlayer().pay(gameState.getBetManager().totalAmount());
                    gameState.applyReturnOfBets(segment);
                    if (gameState.getPlayer().isDead()) {
                        game.setScreen(new GameOverScreen(game));
                    }
                    chipRenderer.createChips();
                    bettingAreaRenderer.updateBetValues();
                });
            }

            // TODO probably disable input while wheel is spinning
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
                   if (gameState.getWheel().size() < MAX_SEGMENTS) {
                       shopItem.tryBuy(gameState);
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
    }

}
