package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.ColorBet;
import de.mario.roguelette.wheel.Segment;

public class ColorRegion extends RectRegion {

    private final Segment.SegmentColor segmentColor;

    protected ColorRegion(Segment.SegmentColor color, Rectangle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.segmentColor = color;
    }

    @Override
    public void render() {
        super.render();

        Rectangle shrink = new Rectangle(bounds.x + bounds.getWidth() * 0.05f, bounds.y + bounds.height * 0.05f, bounds.width * 0.9f, bounds.height * 0.9f);
        float[] verts = {
            shrink.x, shrink.y + shrink.height / 2,
            shrink.x + shrink.width / 2, shrink.y,
            shrink.x + shrink.width, shrink.y + shrink.height / 2,
            shrink.x + shrink.width / 2, shrink.y + shrink.height
        };

        // Color as diamond
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(segmentColor == Segment.SegmentColor.RED ? Color.RED : Color.BLACK);
        shapeRenderer.triangle(verts[0], verts[1], verts[2], verts[3], verts[4], verts[5]);
        shapeRenderer.triangle(verts[0], verts[1], verts[6], verts[7], verts[4], verts[5]);
        shapeRenderer.end();

        // border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.polygon(verts);
        shapeRenderer.end();

        // redraw chip
        if (chip != null) {
            chip.render();
        }
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new ColorBet(segmentColor), amount);
    }

    @Override
    public String getLabel() {
        return "";
    }

    public Segment.SegmentColor getSegmentColor() {
        return segmentColor;
    }
}
