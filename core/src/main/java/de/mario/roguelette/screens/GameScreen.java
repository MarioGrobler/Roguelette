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
import de.mario.roguelette.RougeletteGame;
import de.mario.roguelette.animator.WheelAnimator;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.BetType;
import de.mario.roguelette.render.bet.BettingAreaRenderer;
import de.mario.roguelette.render.bet.Chip;
import de.mario.roguelette.render.bet.ChipRenderer;
import de.mario.roguelette.render.wheel.SegmentAngle;
import de.mario.roguelette.render.wheel.WheelRenderer;
import de.mario.roguelette.util.BetManager;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.WheelFactory;

import java.util.Optional;

public class GameScreen implements Screen {

    private final RougeletteGame game;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private WheelRenderer wheelRenderer;
    private BettingAreaRenderer bettingAreaRenderer;
    private ChipRenderer chipRenderer;

    private int totalBalance;
    private int balance; // totalBalance - amount currently in Hand

    private final BetManager betManager;

    public GameScreen(RougeletteGame game) {
        this.game = game;
        this.totalBalance = 1000000;
        this.balance = this.totalBalance;
        this.betManager = new BetManager();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(1.5f);

        float radius = Gdx.graphics.getHeight() / 3f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        wheelRenderer = new WheelRenderer(WheelFactory.createClassicWheel(), shapeRenderer, batch, font, radius, radius*0.6f, radius*1.2f, radius*1.2f + 50, centerY);

        float startX = Gdx.graphics.getWidth() / 2f + 25;
        float w = Gdx.graphics.getWidth() * 2f / 5f;
        float h = w / 4;
        float startY = Gdx.graphics.getHeight() - h - 20;
        bettingAreaRenderer = new BettingAreaRenderer(betManager, shapeRenderer, batch, font, new Rectangle(startX, startY, w, h));

        float centerX = startX + w / 2f;
        centerY = Gdx.graphics.getHeight() / 5f;
        chipRenderer = new ChipRenderer(shapeRenderer, batch, font, centerX, centerY, radius*1.3f,  50, 30);
        chipRenderer.updateBalance(totalBalance);
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
        font.draw(batch, "$" + balance, 50, Gdx.graphics.getHeight() - 50);

        handleInput();
        batch.end();

        wheelRenderer.update(delta);

        wheelRenderer.render();
        bettingAreaRenderer.render();
        chipRenderer.render();

        // render chip in hand if value is not null
        Chip chipInHand = chipRenderer.getCurrentChipInHand();
        if (chipInHand.getBase() != 0) {
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos);
            chipInHand.setPosition(mousePos.x, mousePos.y);
            chipInHand.render();
        }
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

    private void updateChips() {
        balance = totalBalance - betManager.totalAmount();
        chipRenderer.updateBalance(balance);
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (wheelRenderer.contains(touchPos.x, touchPos.y) && !wheelRenderer.isSpinning()) {
                //wheelRenderer.spinWheelToTarget(0);
                //wheelRenderer.infiniteBallSpin(300);
                //wheelRenderer.spinBallToTarget(400, 0);
                totalBalance -= betManager.totalAmount();

                float selectAngle = MathUtils.random(0f, 360f); // segment at this angle will be selected
                float targetAngle = MathUtils.random(0f, 360f); // rotation where this segment is going to be at the end
                float currentAngle = wheelRenderer.getCurrentRotation();

                SegmentAngle s = wheelRenderer.getCurrentSegmentAngle(selectAngle);
                float center = s.getStartAngle(); // taking the start angle (in base rotation) seems to work very well, dunno why tbh
                System.out.println("select: " + selectAngle + ", target: " + targetAngle + ", current: " + currentAngle + ", s: " + s.getSegment().getDisplayText());

                wheelRenderer.spinWheelToTarget(targetAngle - center);
                wheelRenderer.spinBallToTarget(1000f, targetAngle);
                wheelRenderer.setBallListener(new WheelAnimator.Listener() {
                    @Override
                    public void onSpinEnd() {
                        totalBalance += betManager.computeReturn(s.getSegment());
                        betManager.clear();
                        updateChips();
                        bettingAreaRenderer.updateBetValues(chipRenderer.getCurrentMagnitude());
                    }
                });
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (chipRenderer.handleLeftClick(touchPos.x, touchPos.y)) {
                    updateChips();
                } else {
                    Optional<Bet> optBet = bettingAreaRenderer.handleLeftClick(touchPos.x, touchPos.y, chipRenderer.getCurrentAmountInHand());
                    optBet.ifPresent(bet -> {
                        betManager.addBet(bet);
                        bettingAreaRenderer.updateBetValues(chipRenderer.getCurrentMagnitude());
                        if (totalBalance - betManager.totalAmount() < chipRenderer.getCurrentAmountInHand()) {
                           chipRenderer.handleRightClick(); // reset chip in hand
                        }
                        updateChips();
                    });
                }
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                if (chipRenderer.getCurrentAmountInHand() > 0) {
                    chipRenderer.handleRightClick();
                    updateChips();
                } else {
                    Optional<BetType> optBetType = bettingAreaRenderer.handleRightClick(touchPos.x, touchPos.y);
                    optBetType.ifPresent(betType -> {
                       betManager.removeBet(betType);
                       bettingAreaRenderer.updateBetValues(chipRenderer.getCurrentMagnitude());
                       updateChips();
                    });
                }
            }

        }
    }

}
