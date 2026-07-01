package de.mario.roguelette.profile;

import java.util.ArrayList;

/**
 * The player's persistent profile — everything that survives across runs. Serialized as versioned
 * JSON by {@link ProfileManager} ({@link #schemaVersion} future-proofs the format: bump it when the
 * shape changes and migrate on load). Plain public fields on purpose: libGDX's {@code Json}
 * reads/writes them directly.
 *
 * <p>This is the foundation for the roguelite layer: run statistics now, Casino-Curses progress
 * ({@link CharacterProgress#highestCurseBeaten}) and, later, achievements/unlocks.
 */
public class Profile {

    /** Bump when the JSON shape changes; {@link ProfileManager} can migrate old files on load. */
    public int schemaVersion = 1;

    public int totalRuns;
    public int totalWins;
    /** Highest balance ever held in any run. */
    public long bestBalance;

    public ArrayList<CharacterProgress> characters = new ArrayList<>();

    /** Per-character record, keyed by the character's display name. */
    public static class CharacterProgress {
        public String name;
        public int runs;
        public int wins;
        /** Highest Casino-Curses level beaten with this character; 0 = none (curses not built yet). */
        public int highestCurseBeaten;
    }

    /** Finds or creates the progress record for the given character name. */
    public CharacterProgress characterProgress(final String name) {
        for (CharacterProgress cp : characters) {
            if (cp.name.equals(name)) {
                return cp;
            }
        }
        CharacterProgress cp = new CharacterProgress();
        cp.name = name;
        characters.add(cp);
        return cp;
    }
}
