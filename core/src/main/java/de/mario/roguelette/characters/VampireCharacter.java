package de.mario.roguelette.characters;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.events.GameEventListener;

import java.util.Collections;
import java.util.List;

/**
 * A high-risk character: an old-blood count who feeds on winning streaks. Starts with a thin $75
 * bankroll and plays with a dark-red ball. Its {@link BloodlustEffect} snowballs payouts hard while
 * winning but resets and bleeds balance on a loss — a glass cannon for players who want the swing.
 */
public class VampireCharacter extends Character {

    @Override
    public String getName() {
        return "The Count";
    }

    @Override
    public String getTitle() {
        return "Old Blood";
    }

    @Override
    public String getDescription() {
        return "An ancient gambler who feeds on victory.\n\n"
            + "Bloodlust: each winning spin grants a permanent +" + BloodlustEffect.bonusPercent()
            + "% payout (stacks, no cap). A losing spin resets the streak and bleeds "
            + BloodlustEffect.bleedPercent() + "% of your balance.\n\n"
            + "Starts with only $75. Snowball or starve.";
    }

    @Override
    public Color getAccentColor() {
        return new Color(0.72f, 0.10f, 0.14f, 1f); // blood red
    }

    @Override
    public String getPortraitPath() {
        return "characters/count.png";
    }

    @Override
    public int getStartingBalance() {
        return 75;
    }

    @Override
    public Ball createSignatureBall() {
        // vivid crimson - reads on black and against the red segments
        return new Ball(new Color(0.85f, 0.13f, 0.16f, 1f), "Crimson Ball");
    }

    @Override
    public List<GameEventListener> createListeners() {
        return Collections.singletonList(new BloodlustEffect());
    }
}
