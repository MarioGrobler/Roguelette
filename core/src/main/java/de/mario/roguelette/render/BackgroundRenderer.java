package de.mario.roguelette.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class BackgroundRenderer implements Disposable {

    private Texture gradientTexture;
    private final int textureSize = 512; // Power of 2 for efficiency

    // Colors for the gradient
    private static final Color CENTER_COLOR = new Color(0.18f, 0.18f, 0.25f, 1f);
    private static final Color EDGE_COLOR = new Color(0.06f, 0.06f, 0.10f, 1f);

    public BackgroundRenderer() {
        createGradientTexture();
    }

    private void createGradientTexture() {
        Pixmap pixmap = new Pixmap(textureSize, textureSize, Pixmap.Format.RGBA8888);

        float centerX = textureSize / 2f;
        float centerY = textureSize / 2f;
        float maxDist = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        for (int y = 0; y < textureSize; y++) {
            for (int x = 0; x < textureSize; x++) {
                float dx = x - centerX;
                float dy = y - centerY;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                float t = Math.min(dist / maxDist, 1f);

                // Ease the gradient for a smoother look
                t = t * t;

                float r = lerp(CENTER_COLOR.r, EDGE_COLOR.r, t);
                float g = lerp(CENTER_COLOR.g, EDGE_COLOR.g, t);
                float b = lerp(CENTER_COLOR.b, EDGE_COLOR.b, t);

                pixmap.setColor(r, g, b, 1f);
                pixmap.drawPixel(x, y);
            }
        }

        gradientTexture = new Texture(pixmap);
        gradientTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(gradientTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void dispose() {
        if (gradientTexture != null) {
            gradientTexture.dispose();
        }
    }
}
