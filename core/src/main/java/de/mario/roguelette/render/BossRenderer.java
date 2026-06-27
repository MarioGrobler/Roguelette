package de.mario.roguelette.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.GameState;
import de.mario.roguelette.boss.Boss;
import de.mario.roguelette.items.ShopItem;
import de.mario.roguelette.items.chances.ChanceShopItem;
import de.mario.roguelette.items.fortunes.FortuneShopItem;

import java.util.List;

/**
 * Renders the three boss-encounter overlays: the intro card (reveal + "click to fight"), the in-fight
 * HUD (boss, spins left, gain progress), and the victory reward picker (choose one legendary). Hit
 * detection for the reward cards is exposed via {@link #getRewardIndexAt(float, float)}; the reward
 * card layout is computed in one place so rendering and clicking stay in sync.
 */
public class BossRenderer implements Renderable {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final GameState gameState;

    public BossRenderer(final ShapeRenderer shapeRenderer, final SpriteBatch batch,
                        final BitmapFont font, final GameState gameState) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.gameState = gameState;
    }

    private float sw() { return Gdx.graphics.getWidth(); }
    private float sh() { return Gdx.graphics.getHeight(); }

    private void dimBackground(float alpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, alpha);
        shapeRenderer.rect(0, 0, sw(), sh());
        shapeRenderer.end();
    }

    private void filledRoundedPanel(final Rectangle r, final Color fill, final Color border) {
        RoundedRectRenderer rr = new RoundedRectRenderer(shapeRenderer, r);
        rr.setShadowEnabled(true);
        rr.setRadius(18f);
        rr.setThickness(8f);
        rr.setFillColor(fill);
        rr.setBorderColor(border);
        rr.render();
    }

    // ---------------- Intro ----------------

    public void renderIntro() {
        Boss boss = gameState.getCurrentBoss();
        if (boss == null) {
            return;
        }
        dimBackground(0.72f);

        float pw = Math.min(820f, sw() * 0.6f);
        float ph = Math.min(520f, sh() * 0.7f);
        float px = (sw() - pw) / 2f;
        float py = (sh() - ph) / 2f;
        Color accent = boss.getAccentColor();
        filledRoundedPanel(new Rectangle(px, py, pw, ph), darken(accent, 0.55f), accent);

        batch.begin();
        font.setColor(new Color(0.95f, 0.85f, 0.55f, 1f));
        font.getData().setScale(2.2f);
        drawCentered("BOSS FIGHT", px, py + ph - 50, pw);

        font.setColor(Color.WHITE);
        font.getData().setScale(3.4f);
        drawCentered(boss.getName(), px, py + ph - 120, pw);

        font.setColor(new Color(0.85f, 0.85f, 0.9f, 1f));
        font.getData().setScale(1.8f);
        drawCentered("\"" + boss.getTitle() + "\"", px, py + ph - 200, pw);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.6f);
        font.draw(batch, boss.getDescription(), px + 50, py + ph - 250, pw - 100, Align.center, true);

        font.setColor(new Color(0.95f, 0.85f, 0.55f, 1f));
        font.getData().setScale(1.7f);
        drawCentered("Click anywhere to face the boss", px, py + 60, pw);
        batch.end();
    }

    // ---------------- HUD ----------------

    public void renderHud() {
        Boss boss = gameState.getCurrentBoss();
        if (boss == null) {
            return;
        }
        float bw = Math.min(560f, sw() * 0.4f);
        float bh = 120f;
        float bx = (sw() - bw) / 2f;
        float by = sh() - bh - 20f;
        Color accent = boss.getAccentColor();
        filledRoundedPanel(new Rectangle(bx, by, bw, bh), darken(accent, 0.5f), accent);

        long gained = gameState.getBossGained();
        long goal = gameState.getBossGoal();
        float progress = goal <= 0 ? 1f : Math.max(0f, Math.min(1f, gained / (float) goal));

        // progress bar
        float barPad = 20f;
        float barX = bx + barPad;
        float barW = bw - 2 * barPad;
        float barY = by + 18f;
        float barH = 18f;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(barX, barY, barW, barH);
        shapeRenderer.setColor(0.35f, 0.85f, 0.45f, 1f);
        shapeRenderer.rect(barX, barY, barW * progress, barH);
        shapeRenderer.end();

        batch.begin();
        int spinsLeft = gameState.getBossSpinsRemaining();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.7f);
        drawCentered(boss.getName() + "  -  " + spinsLeft + (spinsLeft == 1 ? " spin left" : " spins left"),
            bx, by + bh - 18, bw);
        font.setColor(new Color(0.9f, 0.95f, 0.9f, 1f));
        font.getData().setScale(1.3f);
        drawCentered("Gain $" + gained + " / $" + goal, bx, by + 78, bw);
        batch.end();
    }

    // ---------------- Reward ----------------

    private Rectangle rewardCardRect(int index, int count) {
        float cw = Math.min(280f, (sw() * 0.8f) / count - 24f);
        float ch = Math.min(420f, sh() * 0.55f);
        float gap = 32f;
        float totalW = count * cw + (count - 1) * gap;
        float startX = (sw() - totalW) / 2f;
        float y = (sh() - ch) / 2f - 20f;
        float x = startX + index * (cw + gap);
        return new Rectangle(x, y, cw, ch);
    }

    public void renderReward() {
        List<ShopItem> offer = gameState.getBossRewardOffer();
        if (offer == null) {
            return;
        }
        dimBackground(0.78f);

        // Overflow: a reward is chosen but the inventory section is full. Prompt a discard instead of
        // the cards (GameScreen re-draws the inventory above the dim so its X buttons are usable).
        if (gameState.isAwaitingRewardDiscard()) {
            // centred (clear of the inventory, which GameScreen redraws above the dim near the top)
            batch.begin();
            font.setColor(new Color(0.95f, 0.82f, 0.35f, 1f));
            font.getData().setScale(2.2f);
            drawCentered("INVENTORY FULL", 0, sh() / 2f + 30, sw());
            font.setColor(Color.WHITE);
            font.getData().setScale(1.5f);
            drawCentered("Click the red X on an item to discard it and claim "
                + gameState.getPendingReward().getShortDescription(), 0, sh() / 2f - 30, sw());
            batch.end();
            return;
        }

        batch.begin();
        font.setColor(new Color(0.95f, 0.82f, 0.35f, 1f));
        font.getData().setScale(2.8f);
        drawCentered("VICTORY", 0, sh() - 60, sw());
        font.setColor(Color.WHITE);
        font.getData().setScale(1.7f);
        drawCentered("Claim your legendary reward", 0, sh() - 130, sw());
        batch.end();

        for (int i = 0; i < offer.size(); i++) {
            ShopItem item = offer.get(i);
            Rectangle r = rewardCardRect(i, offer.size());
            filledRoundedPanel(r, new Color(0.16f, 0.13f, 0.22f, 1f), borderOf(item));

            Texture icon = iconOf(item);
            float iconSize = r.width * 0.55f;
            float iconX = r.x + (r.width - iconSize) / 2f;
            float iconY = r.y + r.height - iconSize - 30f;
            batch.begin();
            if (icon != null) {
                batch.setColor(Color.WHITE);
                batch.draw(icon, iconX, iconY, iconSize, iconSize);
            }
            font.setColor(new Color(0.95f, 0.82f, 0.35f, 1f));
            font.getData().setScale(1.5f);
            drawCentered(item.getShortDescription(), r.x, iconY - 16, r.width);
            font.setColor(new Color(0.85f, 0.85f, 0.9f, 1f));
            font.getData().setScale(1.0f);
            font.draw(batch, item.getDescription(), r.x + 18, iconY - 56, r.width - 36, Align.center, true);
            batch.end();
        }
    }

    /** @return the index of the reward card under the point, or -1. */
    public int getRewardIndexAt(float x, float y) {
        List<ShopItem> offer = gameState.getBossRewardOffer();
        if (offer == null) {
            return -1;
        }
        for (int i = 0; i < offer.size(); i++) {
            if (rewardCardRect(i, offer.size()).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    // ---------------- helpers ----------------

    private void drawCentered(final String text, float x, float y, float width) {
        font.draw(batch, text, x, y, width, Align.center, false);
    }

    private static Texture iconOf(final ShopItem item) {
        if (item instanceof FortuneShopItem) {
            return ((FortuneShopItem) item).getRenderInfo().getBackgrund();
        }
        if (item instanceof ChanceShopItem) {
            return ((ChanceShopItem) item).getRenderInfo().getBackgrund();
        }
        return null;
    }

    private static Color borderOf(final ShopItem item) {
        if (item instanceof FortuneShopItem) {
            return ((FortuneShopItem) item).getRenderInfo().getBorderColor();
        }
        if (item instanceof ChanceShopItem) {
            return ((ChanceShopItem) item).getRenderInfo().getBorderColor1();
        }
        return Color.GOLD;
    }

    private static Color darken(final Color c, float amount) {
        return new Color(c.r * (1 - amount), c.g * (1 - amount), c.b * (1 - amount), 1f);
    }

    @Override
    public void render() {
        switch (gameState.getCurrentState()) {
            case BOSS_INTRO:  renderIntro();  break;
            case BOSS_FIGHT:  renderHud();    break;
            case BOSS_REWARD: renderReward(); break;
            default: break;
        }
    }

    @Override
    public boolean contains(float x, float y) {
        return true;
    }
}
