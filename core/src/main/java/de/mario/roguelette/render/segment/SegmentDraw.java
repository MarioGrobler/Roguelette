package de.mario.roguelette.render.segment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Align;
import de.mario.roguelette.wheel.DevilSegment;
import de.mario.roguelette.wheel.Segment;

import java.text.DecimalFormat;

public class SegmentDraw extends SegmentDrawBase {

    private final Color mulBackground = Color.GOLDENROD;
    private final Color mulBorder = Color.GOLDENROD;
    private final Color mulBackgroundEx = new Color(0xb22222ff);
    private final Color mulBorderEx = Color.GOLDENROD;

    private final DecimalFormat df = new DecimalFormat("0.#");
    private Segment segment;

    private static final Color BOTH_COLOR = new Color(0.6f, 0.2f, 0.7f, 1f); // Purple/magenta for "wild"

    private static final Color DEVIL_COLOR = new Color(0.24f, 0.04f, 0.07f, 1f); // hellish near-black red

    private Color getDrawingColorFromSegment(final Segment segment) {
        if (segment == null) {
            return Color.PINK;
        }
        if (segment instanceof DevilSegment) {
            return DEVIL_COLOR; // colourless like the 0, but must not read as a friendly green
        }

        switch(segment.getCurrentColor()) {
            case RED:
                return Color.RED;
            case BLACK:
                return Color.BLACK;
            case NONE:
                return Color.FOREST;
            case BOTH:
                return BOTH_COLOR;
            default: // should be unreachable
                return Color.PINK;
        }
    }

    public SegmentDraw(final Segment segment, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, float centerX, float centerY, float outerRadius, float innerRadius) {
        super(shapeRenderer, batch, font, centerX, centerY, outerRadius, innerRadius);
        this.segment = segment;
        setColor(getDrawingColorFromSegment(segment));
    }

    @Override
    public void render() {
        // draw slice
        segmentShapeRenderer.render();

        // draw texts
        if (segment != null) {
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
                .translate(x, y, 0)
                .rotate(0, 0, 1, cAngle)
                .translate(0, originY, 0)
            );

            font.draw(batch, layout, 0, 0);
            batch.setTransformMatrix(new Matrix4()); // reset

            // draw multiplier badge if not identity
            if (segment.getCurrentMultiplier() != 1f) {
                batch.end();
                renderMultiplierBadge(cAngle);
                batch.begin();
            }
            batch.end();
        }
    }

    private void renderMultiplierBadge(float cAngle) {
        // Position just above the inner edge of the segment, clear of the center hub
        float mulRadius = getInnerRadius() + (getOuterRadius() - getInnerRadius()) * 0.22f;
        float mulX = getCenterX() + MathUtils.cosDeg(cAngle) * mulRadius;
        float mulY = getCenterY() + MathUtils.sinDeg(cAngle) * mulRadius;

        font.getData().setScale(1.0f);
        String mulText = "×" + df.format(segment.getCurrentMultiplier());
        GlyphLayout mulLayout = new GlyphLayout(font, mulText, Color.WHITE, 0, Align.left, false);

        float paddingX = 5f;
        float paddingY = 3f;
        float badgeWidth = mulLayout.width + 2 * paddingX;
        float badgeHeight = mulLayout.height + 2 * paddingY;
        float cornerRadius = 4f;

        Matrix4 matrix = new Matrix4().idt()
            .translate(mulX, mulY, 0)
            .rotate(0, 0, 1, cAngle);

        // Choose colors - more subtle, less saturated
        Color bgColor = (segment.getMultiplier() == segment.getCurrentMultiplier())
            ? new Color(0.75f, 0.65f, 0.2f, 0.75f)   // Muted gold, more transparent
            : new Color(0.7f, 0.3f, 0.2f, 0.75f);    // Muted red-orange for modified

        // Draw rounded badge background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setTransformMatrix(matrix);
        shapeRenderer.setColor(bgColor);

        // Center rectangle
        float halfW = badgeWidth / 2;
        float halfH = badgeHeight / 2;
        shapeRenderer.rect(-halfW + cornerRadius, -halfH, badgeWidth - 2 * cornerRadius, badgeHeight);
        shapeRenderer.rect(-halfW, -halfH + cornerRadius, badgeWidth, badgeHeight - 2 * cornerRadius);

        // Corner circles
        shapeRenderer.circle(-halfW + cornerRadius, -halfH + cornerRadius, cornerRadius);
        shapeRenderer.circle(halfW - cornerRadius, -halfH + cornerRadius, cornerRadius);
        shapeRenderer.circle(-halfW + cornerRadius, halfH - cornerRadius, cornerRadius);
        shapeRenderer.circle(halfW - cornerRadius, halfH - cornerRadius, cornerRadius);

        shapeRenderer.setTransformMatrix(new Matrix4());
        shapeRenderer.end();

        // Draw text - properly centered
        batch.begin();
        batch.setTransformMatrix(matrix);
        font.draw(batch, mulLayout, -badgeWidth / 2 + paddingX, mulLayout.height / 2);
        batch.setTransformMatrix(new Matrix4());
        batch.end();
    }

    public void setSegment(Segment segment) {
        this.segment = segment;
        setColor(getDrawingColorFromSegment(segment));
    }

    public Segment getSegment() {
        return segment;
    }
}
