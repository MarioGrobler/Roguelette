package de.mario.roguelette.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

import java.util.Arrays;

public class MusicManager {

    // the stems for the one song that exists atm
    private Music bass;
    private Music bongos;
    private Music celesta;
    private Music dulcimer;
    private Music strings;

    // used for smooth transitions when switching the mode
    private float volumeDulcimer, volumeBongos, volumeStrings, volumeCelesta, volumeBass;
    private float targetDulcimer, targetBongos, targetStrings, targetCelesta, targetBass;

    float fadeSpeed = 1.0f; //default value

    public void load() {
        bass = Gdx.audio.newMusic(Gdx.files.internal("music/mystic/bass.wav"));
        bongos = Gdx.audio.newMusic(Gdx.files.internal("music/mystic/percussion.wav"));
        celesta =  Gdx.audio.newMusic(Gdx.files.internal("music/mystic/celesta.wav"));
        dulcimer = Gdx.audio.newMusic(Gdx.files.internal("music/mystic/dulcimer.wav"));
        strings = Gdx.audio.newMusic(Gdx.files.internal("music/mystic/strings.wav"));

        for (Music music : Arrays.asList(bass, bongos, celesta, dulcimer, strings)) {
            music.setLooping(true);
            music.setVolume(0f);
            music.play();
        }
    }

    private float fade(float current, float target, float delta) {
        if (current == target) return current;

        float diff = target - current;
        float step = fadeSpeed * delta;
        if (Math.abs(diff) <= step) {
            return target;
        }
        return current + Math.signum(diff) * step;
    }

    public void update(float delta) {
        volumeBass = fade(volumeBass, targetBass, delta);
        volumeBongos = fade(volumeBongos, targetBongos, delta);
        volumeCelesta = fade(volumeCelesta, targetCelesta, delta);
        volumeDulcimer = fade(volumeDulcimer, targetDulcimer, delta);
        volumeStrings = fade(volumeStrings, targetStrings, delta);

        bass.setVolume(volumeBass);
        bongos.setVolume(volumeBongos);
        celesta.setVolume(volumeCelesta);
        dulcimer.setVolume(volumeDulcimer);
        strings.setVolume(volumeStrings);
    }

    public void setDefaultMode(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;

        // 🎮 Default Mode
        targetBass = 0.8f;
        targetBongos = 1.0f;
        targetCelesta = 0.0f; // no sparkles here
        targetDulcimer = 1.0f;
        targetStrings = 1.0f;
    }

    public void setDefaultMode() {
        setDefaultMode(1.0f);
    }

    public void setShopMode(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;

        // 🛒 Shop Mode
        targetBass = 0.5f;
        targetBongos = 0.0f; // no percussion
        targetCelesta = 0.7f; // sparkles!
        targetDulcimer = 1.0f;
        targetStrings = 0.4f;
    }


    public void setShopMode() {
        setShopMode(1.0f);
    }

    public void setCrystalBallMode(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;

        // 🔮 Crystal Ball Mode
        targetBass = 0.1f; // only very little bass
        targetBongos = 0.0f; // no percussion
        targetCelesta = 1.0f; // super sparkles!
        targetDulcimer = 0.0f;
        targetStrings = 0.7f;
    }

    public void setCrystalBallMode() {
        setCrystalBallMode(1.0f);
    }

    public float getFadeSpeed() {
        return fadeSpeed;
    }

    public void setFadeSpeed(float fadeSpeed) {
        this.fadeSpeed = fadeSpeed;
    }

    public void dispose() {
        for (Music music : Arrays.asList(bass, bongos, celesta, dulcimer, strings)) {
            music.dispose();
        }
    }
}
