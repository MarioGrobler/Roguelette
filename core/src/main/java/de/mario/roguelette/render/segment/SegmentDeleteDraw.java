package de.mario.roguelette.render.segment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class SegmentDeleteDraw extends SegmentDrawBase {
    private final Texture trashIcon = new Texture(Gdx.files.internal("icon/trash.png"));

    public SegmentDeleteDraw(final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, float centerX, float centerY, float outerRadius, float innerRadius) {
        super(shapeRenderer, batch, font, centerX, centerY, outerRadius, innerRadius);
        setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
    }

    @Override
    public void render() {
        segmentShapeRenderer.render();

        // draw icon, rotated
        batch.begin();

        // compute bounds for icon
        float iconRadiusLower = getOuterRadius() * .6f;
        float iconRadiusUpper = getOuterRadius() * .9f;

        // stretch the width of the bin a bit
        float lAngle = getStartAngle() + -0.2f * getSweepAngle() + getRotation();
        float rAngle = getStartAngle() +  1.2f * getSweepAngle() + getRotation();
        float cAngle = getStartAngle() + 0.5f * getSweepAngle() + getRotation();

        float xLeft = getCenterX() + MathUtils.cosDeg(lAngle) * iconRadiusLower;
        float xRight = getCenterX() + MathUtils.cosDeg(rAngle) * iconRadiusLower;
        float width = Math.abs(xRight - xLeft);

        float yDown = getCenterY() + MathUtils.sinDeg(lAngle) * iconRadiusLower;
        float yUp = getCenterY() + MathUtils.sinDeg(rAngle) * iconRadiusUpper;
        float height = Math.abs(yUp - yDown);

        batch.setTransformMatrix(batch.getTransformMatrix().idt()
            .translate(xRight, yDown,0)
            .rotate(0,0,1, cAngle - 90) // -90 to enforce that the bin is actually standing
            .translate(0, height/5f,0) // do not ask why there is a /5f
        );
        batch.draw(trashIcon, 0, 0, width, height);
        batch.setTransformMatrix(new Matrix4()); // reset
        batch.end();
    }
}
