package de.mario.roguelette.profile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * The single gateway to persistent state: loads the {@link Profile} once at startup and saves it
 * on every mutation (run end, future unlocks). Nothing else in the game touches files — new
 * persistent systems (curse progress, achievements) go through here.
 *
 * <p>Stored as pretty-printed JSON in the user's home directory
 * ({@code ~/.roguelette/profile.json}), independent of where the game is run from. A corrupt file
 * is backed up ({@code profile.json.bad}) and replaced with a fresh profile rather than crashing
 * the game.
 */
public class ProfileManager {

    private static final String PROFILE_PATH = ".roguelette/profile.json";

    private final Json json = new Json();
    private Profile profile;

    public ProfileManager() {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false); // write all fields, including 0/defaults — keeps the file explicit
        json.setElementType(Profile.class, "characters", Profile.CharacterProgress.class);
        load();
    }

    private FileHandle file() {
        return Gdx.files.external(PROFILE_PATH);
    }

    private void load() {
        FileHandle fh = file();
        if (!fh.exists()) {
            profile = new Profile();
            return;
        }
        try {
            profile = json.fromJson(Profile.class, fh.readString("UTF-8"));
            if (profile == null) {
                profile = new Profile();
            }
            // schemaVersion migrations go here when the shape changes (currently only v1 exists)
        } catch (Exception e) {
            // don't let a corrupt profile kill the game: keep the evidence, start fresh
            Gdx.app.error("ProfileManager", "profile.json unreadable, backing up and starting fresh", e);
            try {
                fh.moveTo(Gdx.files.external(PROFILE_PATH + ".bad"));
            } catch (Exception ignored) {
                // even the backup failed; the fresh profile will overwrite on next save
            }
            profile = new Profile();
        }
    }

    public void save() {
        try {
            file().writeString(json.prettyPrint(profile), false, "UTF-8");
        } catch (Exception e) {
            Gdx.app.error("ProfileManager", "could not save profile", e);
        }
    }

    public Profile getProfile() {
        return profile;
    }

    /**
     * Records a finished run (won or lost) for the given character and saves. A win at curse
     * level L advances the character's {@code highestCurseBeaten} (which gates level L+1 on the
     * select screen).
     */
    public void recordRunEnd(final String characterName, final boolean won, final long finalBalance,
                             final int curseLevel) {
        profile.totalRuns++;
        Profile.CharacterProgress cp = profile.characterProgress(characterName);
        cp.runs++;
        if (won) {
            profile.totalWins++;
            cp.wins++;
            cp.highestCurseBeaten = Math.max(cp.highestCurseBeaten, curseLevel);
        }
        profile.bestBalance = Math.max(profile.bestBalance, finalBalance);
        save();
    }

    /** Tracks the best balance seen mid-run (a peak can be higher than the final balance). */
    public void recordBalancePeak(final long balance) {
        if (balance > profile.bestBalance) {
            profile.bestBalance = balance;
            save();
        }
    }
}
