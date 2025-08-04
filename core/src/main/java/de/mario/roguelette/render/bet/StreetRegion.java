package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.StreetBet;

public class StreetRegion extends InsideBetRegion {
    private final StreetBet.Street street;

    protected StreetRegion(StreetBet.Street street, Circle bounds, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(bounds, shapeRenderer, batch, font);
        this.street = street;
    }

    @Override
    public Bet createBet(int amount) {
        return new Bet(new StreetBet(street), amount);
    }

    @Override
    public String getLabel() {
        return "";
    }

    public StreetBet.Street getStreet() {
        return street;
    }
}
