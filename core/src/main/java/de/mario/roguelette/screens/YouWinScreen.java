package de.mario.roguelette.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import de.mario.roguelette.RougeletteGame;

public class YouWinScreen implements Screen {

    private final Color BACKGROUND_COLOR = new Color(0x000000ff);

    private final RougeletteGame game;
    private final Screen gameScreen;

    private Texture logoTexture;
    private SpriteBatch batch;

    public YouWinScreen(final RougeletteGame game, final Screen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        logoTexture = new Texture(Gdx.files.internal("logo/youwin.png"));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND_COLOR);

        handleInput();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float logoX = centerX - logoTexture.getWidth() / 2f;
        float logoY = 0;

        batch.begin();
        batch.draw(logoTexture, logoX, logoY);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

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
        logoTexture.dispose();
        batch.dispose();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(gameScreen);
        }
    }
}
