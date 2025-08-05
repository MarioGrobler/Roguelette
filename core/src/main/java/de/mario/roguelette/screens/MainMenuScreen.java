package de.mario.roguelette.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import de.mario.roguelette.RougeletteGame;

public class MainMenuScreen implements Screen {

    private final Color BACKGROUND_COLOR = new Color(0x110F0Cff);

    private final RougeletteGame game;
    private Texture logoTexture;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private BitmapFont font;


    public MainMenuScreen(RougeletteGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        batch = new SpriteBatch();
        logoTexture = new Texture("logo/banner.png");
        font = new BitmapFont();
        font.getData().setScale(2f);
        layout = new GlyphLayout(font, "Press the ENTER key", Color.WHITE, 0, Align.center, false);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND_COLOR);

        handleInput();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float logoX = centerX - logoTexture.getWidth() / 2f;
        float logoY = 50;

        batch.begin();
        batch.draw(logoTexture, logoX, logoY);
        font.draw(batch, layout, centerX, 100);
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
        batch.dispose();
        logoTexture.dispose();
        font.dispose();
    }

    private void handleInput() {
        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
    }
}
