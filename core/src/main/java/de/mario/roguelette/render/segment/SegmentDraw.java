package de.mario.roguelette.render.segment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.wheel.Segment;

import java.text.DecimalFormat;

public class SegmentDraw extends SegmentDrawBase {

    private final Segment segment;
    private final DecimalFormat df = new DecimalFormat("0.#");

    private Color getDrawingColorFromSegment(final Segment segment) {
        if (segment == null) {
            return Color.PINK;
        }

        switch(segment.getColor()) {
            case RED:
                return Color.RED;
            case BLACK:
                return Color.BLACK;
            case NONE:
                return Color.FOREST;
            case BOTH: //TODO: a more complex pattern here
                return new Color(0.4f, 0f, 0f, 1);
            default: // should be unreachable
                return Color.PINK;
        }
    }

    public SegmentDraw(final Segment segment, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, float centerX, float centerY, float outerRadius, float innerRadius) {
        super(shapeRenderer, batch, font, centerX, centerY, outerRadius, innerRadius);
        this.segment = segment;
        setColor(getDrawingColorFromSegment(segment));
    }

    public void render() {
        // draw slice
        segmentShapeRenderer.render();

        // draw texts
        batch.begin();
        font.getData().setScale(1.5f);

        float cAngle = getStartAngle() + 0.5f * getSweepAngle() + getRotation();
        float textRadius = getOuterRadius() * .95f;

        float x = getCenterX() + MathUtils.cosDeg(cAngle) * textRadius;
        float y = getCenterY() + MathUtils.sinDeg(cAngle) * textRadius;

        String text = segment.getDisplayText();

        // Rotate the text based on current rotation
        GlyphLayout layout = new GlyphLayout(font, text, Color.WHITE, 0, Align.right, false);
        float originY = layout.height / 2;

        batch.setTransformMatrix(batch.getTransformMatrix().idt()
            .translate(x,y,0)
            .rotate(0,0,1, cAngle)
            .translate(0, originY,0)
        );

        font.draw(batch, layout, 0, 0);

        // draw multiplier if it is not the identity
        if (segment.getMultiplier() != 1f) {
            // add a golden shimmer (maybe later)
//            Gdx.gl.glEnable(GL20.GL_BLEND);
//            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.setColor(1, 1, 0, 0.3f);
//            shapeRenderer.arc(centerX, centerY, outerRadius, startAngle + rotation, sweepAngle);
//            shapeRenderer.end();
//            Gdx.gl.glDisable(GL20.GL_BLEND);


            float mulRadius = getInnerRadius() + (getOuterRadius() - getInnerRadius()) * 0.2f;
            float mulX = getCenterX() + MathUtils.cosDeg(cAngle) * mulRadius;
            float mulY = getCenterY() + MathUtils.sinDeg(cAngle) * mulRadius;
            font.getData().setScale(1.2f);
            GlyphLayout mulLayout = new GlyphLayout(font, df.format(segment.getMultiplier()) + "x", Color.BLACK, 0, Align.left, false);

            float padding = 6f;

            Matrix4 matrix = new Matrix4().idt()
                .translate(mulX, mulY, 0)
                .rotate(0, 0, 1, cAngle)
                .translate(0, mulLayout.height / 2, 0);

            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setTransformMatrix(matrix);
            shapeRenderer.setColor(Color.GOLDENROD);
            shapeRenderer.rect(-padding, -mulLayout.height - padding, mulLayout.width + 2 * padding, mulLayout.height + 2 * padding);
            shapeRenderer.setTransformMatrix(shapeRenderer.getTransformMatrix().idt()); // reset matrix
            shapeRenderer.end();

            batch.begin();
            batch.setTransformMatrix(matrix);
            font.draw(batch, mulLayout, 0, 0);
            batch.setTransformMatrix(batch.getTransformMatrix().idt()); // also reset matrix
        }
        batch.end();
    }

    public Segment getSegment() {
        return segment;
    }
}
