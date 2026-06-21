package de.mario.roguelette.characters;

import java.util.Arrays;
import java.util.List;

/**
 * Registry of the characters offered on the select screen. Add new characters here to make them
 * selectable.
 */
public final class Characters {

    private Characters() {}

    public static List<Character> all() {
        return Arrays.asList(
            new GamblerCharacter(),
            new VampireCharacter(),
            new ProfessorCharacter()
        );
    }
}
