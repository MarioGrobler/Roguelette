package de.mario.roguelette.characters;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * The default, approachable character: a charismatic gambler. Plays with the standard ball and the
 * standard $100 bankroll, with one gentle upside ({@link SilverTongueEffect}) and no downside —
 * the right pick to learn the game with.
 */
public class GamblerCharacter extends Character {

    @Override
    public String getName() {
        return "The Gambler";
    }

    @Override
    public String getTitle() {
        return "Charmer of Fortune";
    }

    @Override
    public String getDescription() {
        return "A silver-tongued regular who always talks the table up.\n\n"
            + "Silver Tongue: every win pays +" + SilverTongueEffect.bonusPercent() + "%.\n\n"
            + "Standard $100 bankroll. No downside - the friendly way in.";
    }

    @Override
    public Color getAccentColor() {
        return new Color(0.85f, 0.68f, 0.28f, 1f); // warm gold
    }

    @Override
    public String getPortraitPath() {
        return "characters/gambler.png";
    }

    @Override
    public Ball createSignatureBall() {
        return Ball.defaultBall();
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new SilverTongueEffect());
    }
}
