package de.mario.roguelette.characters;

import com.badlogic.gdx.graphics.Color;
import de.mario.roguelette.balls.Ball;
import de.mario.roguelette.events.GameEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A run identity, picked once on the character-select screen before a run starts (Balatro-style).
 * A character is the protagonist the player plays as; it <em>carries</em> a signature {@link Ball}
 * (the cosmetic avatar that seeds every spin) plus a set of run-level passive
 * {@link GameEventListener}s that bend the rules of the run. The listeners are registered the same
 * way owned fortunes are (see {@code GameState.collectListeners}), so a character's mechanics reuse
 * the whole event layer without any special casing.
 *
 * <p>Mechanics that are about <em>where</em> the ball lands belong on {@code onBallLanded}; payout
 * and economy mechanics belong on {@code onResolveBet}/{@code onTurnChange}. The {@link Ball} itself
 * stays a cosmetic tint for now; per-ball behaviour can migrate onto it later if multi-ball
 * characters need it.
 */
public abstract class Character {

    /** Display name, e.g. "The Gambler". */
    public abstract String getName();

    /** Short epithet shown under the name, e.g. "Charmer of Fortune". */
    public abstract String getTitle();

    /** Player-facing explanation of the character's mechanic and trade-offs. */
    public abstract String getDescription();

    /** Theme colour used for the character's card border / accents on the select screen. */
    public abstract Color getAccentColor();

    /** Internal path to the character's portrait illustration, e.g. {@code "characters/gambler.png"}. */
    public abstract String getPortraitPath();

    /** Starting balance for a run with this character. Defaults to the standard $100. */
    public int getStartingBalance() {
        return 100;
    }

    /**
     * Builds the signature ball that seeds each spin. A fresh ball per spin is fine — balls are
     * stateless cosmetic objects today.
     */
    public Ball createSignatureBall() {
        return Ball.defaultBall();
    }

    /**
     * Builds this character's run-level passive listeners. Called once when a run is created, so
     * stateful effects get fresh state per run. Defaults to none (a vanilla character).
     */
    public List<GameEventListener> createListeners() {
        return new ArrayList<>();
    }
}
