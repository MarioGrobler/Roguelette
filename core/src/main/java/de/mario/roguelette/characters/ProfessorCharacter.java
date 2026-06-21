package de.mario.roguelette.characters;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * A high-variance character: an eccentric quantum physicist who treats every bet as an experiment.
 * Plays with the standard $100 bankroll and a glowing teal "Quantum Ball", but his
 * {@link SuperpositionEffect} makes each spin's winnings collapse to nothing or triple at random.
 */
public class ProfessorCharacter extends Character {

    @Override
    public String getName() {
        return "The Professor";
    }

    @Override
    public String getTitle() {
        return "Master of Uncertainty";
    }

    @Override
    public String getDescription() {
        return "A wild-haired physicist who bets on probability itself.\n\n"
            + "Superposition: at payout, each spin's winnings collapse to either NOTHING or x"
            + SuperpositionEffect.amplifyFactor() + " - a hidden coin, 50/50.\n\n"
            + "Standard $100 bankroll. Pure high-variance gambling.";
    }

    @Override
    public Color getAccentColor() {
        return new Color(0.20f, 0.80f, 0.85f, 1f); // electric teal
    }

    @Override
    public String getPortraitPath() {
        return "characters/professor.png";
    }

    @Override
    public Ball createSignatureBall() {
        return new Ball(new Color(0.25f, 0.85f, 0.88f, 1f), "Quantum Ball"); // glowing teal
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new SuperpositionEffect());
    }
}
