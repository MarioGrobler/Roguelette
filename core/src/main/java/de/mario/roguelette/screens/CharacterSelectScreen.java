package de.mario.roguelette.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import de.mario.roguelette.RougeletteGame;
import de.mario.roguelette.characters.Character;
import de.mario.roguelette.characters.Characters;
import de.mario.roguelette.render.RoundedRectRenderer;
import de.mario.roguelette.util.ColorHelper;

import java.util.List;

/**
 * The run-start character pick (Balatro-style), shown between the main menu and the game. Each
 * character is a card whose hero element is the character illustration; the name sits at the top
 * and the signature ball is shown as a small chip in the corner. A shared info panel below the
 * cards shows the focused character's epithet, starting bankroll, signature ball and mechanic.
 * Hover or arrow-keys to focus, click or ENTER to start a run with that character.
 */
public class CharacterSelectScreen implements Screen {

    private final Color BACKGROUND_COLOR = new Color(0x110F0Cff);
    private final Color CARD_FILL = new Color(0.16f, 0.14f, 0.12f, 1f);
    private final Color PANEL_FILL = new Color(0.13f, 0.12f, 0.11f, 1f);
    private final Color CHIP_FILL = new Color(0.10f, 0.09f, 0.08f, 1f);

    private final RougeletteGame game;
    private final List<Character> characters;
    private Texture[] portraits;

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private GlyphLayout layout;

    private BitmapFont titleFont;
    private BitmapFont nameFont;
    private BitmapFont epithetFont;
    private BitmapFont bodyFont;

    private RoundedRectRenderer[] cards;
    private RoundedRectRenderer[] chipBoxes;
    private Rectangle[] cardBounds;
    private Rectangle[] illoRegions;
    private RoundedRectRenderer infoPanel;
    private Rectangle infoBounds;
    private int focused = 0;

    private static final float CHIP_SIZE = 70f;
    private static final float NAME_BAND = 60f;

    public CharacterSelectScreen(RougeletteGame game) {
        this.game = game;
        this.characters = Characters.all();
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        layout = new GlyphLayout();

        titleFont = game.getFontManager().getTitle();
        nameFont = game.getFontManager().getLarge();
        epithetFont = game.getFontManager().getMedium();
        bodyFont = game.getFontManager().getDefault();

        portraits = new Texture[characters.size()];
        for (int i = 0; i < characters.size(); i++) {
            Texture t = new Texture(Gdx.files.internal(characters.get(i).getPortraitPath()));
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            portraits[i] = t;
        }

        layoutCards();
    }

    private void layoutCards() {
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        int n = characters.size();
        float gap = w * 0.05f;
        float cardH = h * 0.58f;
        float cardW = cardH * 0.74f; // portrait-ish, matches the 700x900 illustrations
        float totalWidth = n * cardW + (n - 1) * gap;
        float startX = (w - totalWidth) / 2f;
        float cardY = h * 0.30f;

        cards = new RoundedRectRenderer[n];
        chipBoxes = new RoundedRectRenderer[n];
        cardBounds = new Rectangle[n];
        illoRegions = new Rectangle[n];

        for (int i = 0; i < n; i++) {
            Color accent = characters.get(i).getAccentColor();
            float x = startX + i * (cardW + gap);
            Rectangle bounds = new Rectangle(x, cardY, cardW, cardH);
            cardBounds[i] = bounds;

            RoundedRectRenderer card = new RoundedRectRenderer(shapeRenderer, bounds);
            card.setRadius(18f);
            card.setThickness(8f);
            card.setFillColor(CARD_FILL);
            card.setHighlightFillColor(ColorHelper.lighten(CARD_FILL, 0.12f));
            card.setBorderColor(accent);
            cards[i] = card;

            float pad = 16f;
            illoRegions[i] = new Rectangle(x + pad, cardY + pad, cardW - 2 * pad, cardH - NAME_BAND - 2 * pad);

            Rectangle chipBounds = new Rectangle(x + cardW - CHIP_SIZE - 14f, cardY + cardH - CHIP_SIZE - 14f, CHIP_SIZE, CHIP_SIZE);
            RoundedRectRenderer chip = new RoundedRectRenderer(shapeRenderer, chipBounds);
            chip.setRadius(12f);
            chip.setThickness(5f);
            chip.setFillColor(CHIP_FILL);
            chip.setBorderColor(ColorHelper.lighten(accent, 0.1f));
            chip.setShadowEnabled(false);
            chipBoxes[i] = chip;
        }

        float panelW = totalWidth;
        float panelH = h * 0.20f;
        infoBounds = new Rectangle(startX, h * 0.06f, panelW, panelH);
        infoPanel = new RoundedRectRenderer(shapeRenderer, infoBounds);
        infoPanel.setRadius(16f);
        infoPanel.setThickness(6f);
        infoPanel.setFillColor(PANEL_FILL);
        infoPanel.setHighlightFillColor(PANEL_FILL);
        infoPanel.setBorderColor(ColorHelper.lighten(PANEL_FILL, 0.18f));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(BACKGROUND_COLOR);

        handleInput();
        game.getMusicManager().update(delta);

        // fonts are shared singletons; other screens leave their scale mutated -> reset
        titleFont.getData().setScale(1f);
        nameFont.getData().setScale(1f);
        epithetFont.getData().setScale(1f);
        bodyFont.getData().setScale(1f);

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // --- shape pass 1: card + panel backgrounds ---
        for (int i = 0; i < cards.length; i++) {
            cards[i].setHighlight(i == focused);
            cards[i].render();
        }
        infoPanel.render();

        // --- batch pass 1: title, illustrations, names ---
        batch.begin();
        layout.setText(titleFont, "Choose Your Character", Color.WHITE, w, Align.center, false);
        titleFont.draw(batch, layout, 0, h - h * 0.045f);

        for (int i = 0; i < characters.size(); i++) {
            drawIllustration(i);
            drawCardName(characters.get(i), cardBounds[i], i == focused);
        }
        batch.end();

        // --- shape pass 2: signature-ball chips (over the illustration) ---
        for (int i = 0; i < chipBoxes.length; i++) {
            chipBoxes[i].render();
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < chipBoxes.length; i++) {
            Rectangle cb = chipBoxes[i].getBounds();
            drawBall(cb.x + cb.width / 2f, cb.y + cb.height / 2f, CHIP_SIZE * 0.26f, characters.get(i).createSignatureBall().getTint());
        }
        shapeRenderer.end();

        // --- batch pass 2: info panel + hint ---
        batch.begin();
        drawInfoPanel(characters.get(focused));
        layout.setText(bodyFont, "Click a card, or use LEFT / RIGHT and ENTER", new Color(1, 1, 1, 0.55f), w, Align.center, false);
        bodyFont.draw(batch, layout, 0, infoBounds.y - 14f);
        batch.end();
    }

    private void drawIllustration(int i) {
        Texture tex = portraits[i];
        Rectangle r = illoRegions[i];
        float scale = Math.min(r.width / tex.getWidth(), r.height / tex.getHeight());
        float dw = tex.getWidth() * scale;
        float dh = tex.getHeight() * scale;
        float dx = r.x + (r.width - dw) / 2f;
        float dy = r.y; // bottom-anchored so the figure sits in the card
        batch.draw(tex, dx, dy, dw, dh);
    }

    private void drawCardName(Character character, Rectangle b, boolean isFocused) {
        Color c = isFocused ? Color.WHITE : new Color(0.9f, 0.9f, 0.9f, 1f);
        layout.setText(nameFont, character.getName(), c, b.width - 28, Align.center, true);
        nameFont.draw(batch, layout, b.x + 14, b.y + b.height - 16);
    }

    private void drawInfoPanel(Character character) {
        Rectangle b = infoBounds;
        float pad = 26f;
        float x = b.x + pad;
        float y = b.y + b.height - pad;
        float textW = b.width - 2 * pad;
        Color accent = character.getAccentColor();

        // epithet
        layout.setText(epithetFont, character.getTitle(), accent, textW, Align.left, false);
        epithetFont.draw(batch, layout, x, y);
        // starting bankroll on the same row, right-aligned
        layout.setText(bodyFont, "Starts with $" + character.getStartingBalance()
            + "    Signature ball: " + character.createSignatureBall().getName(),
            new Color(0.85f, 0.85f, 0.62f, 1f), textW, Align.right, false);
        bodyFont.draw(batch, layout, x, y - 4);

        y -= layout.height + 22;

        // description (mechanic), wrapped
        layout.setText(bodyFont, character.getDescription(), new Color(0.88f, 0.88f, 0.88f, 1f), textW, Align.left, true);
        bodyFont.draw(batch, layout, x, y);
    }

    /** Draws a ball matching the in-game look: dark outline ring, tinted body, white highlight. */
    private void drawBall(float cx, float cy, float r, Color tint) {
        shapeRenderer.setColor(0f, 0f, 0f, 0.55f);
        shapeRenderer.circle(cx, cy, r);
        float body = r - 1.5f;
        for (int i = 5; i >= 0; i--) {
            float t = i / 5f;
            float c = 1f - 0.15f * t;
            shapeRenderer.setColor(tint.r * c, tint.g * c, tint.b * c, 1f);
            shapeRenderer.circle(cx, cy, body * (1f - t * 0.2f));
        }
        shapeRenderer.setColor(1f, 1f, 1f, 0.9f);
        shapeRenderer.circle(cx - body * 0.3f, cy + body * 0.3f, body * 0.25f);
    }

    private void handleInput() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        for (int i = 0; i < cardBounds.length; i++) {
            if (cardBounds[i].contains(mouse.x, mouse.y)) {
                focused = i;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            focused = (focused + 1) % characters.size();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            focused = (focused - 1 + characters.size()) % characters.size();
        }
        for (int i = 0; i < characters.size() && i < 9; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                focused = i;
                confirm();
                return;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            confirm();
            return;
        }
        if (Gdx.input.justTouched()) {
            for (int i = 0; i < cardBounds.length; i++) {
                if (cardBounds[i].contains(mouse.x, mouse.y)) {
                    focused = i;
                    confirm();
                    return;
                }
            }
        }
    }

    private void confirm() {
        game.setScreen(new GameScreen(game, characters.get(focused)));
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        layoutCards();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        if (portraits != null) {
            for (Texture t : portraits) {
                if (t != null) t.dispose();
            }
        }
        // fonts are managed by RougeletteGame.fontManager
    }
}
