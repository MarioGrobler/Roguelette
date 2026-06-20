package de.mario.roguelette;

import com.badlogic.gdx.Game;
import de.mario.roguelette.screens.MainMenuScreen;
import de.mario.roguelette.util.FontManager;
import de.mario.roguelette.util.MusicManager;


public class RougeletteGame extends Game {

    private MusicManager musicManager;
    private FontManager fontManager;

    @Override
    public void create() {
        musicManager = new MusicManager();
        musicManager.load();
        musicManager.setShopMode(0.33f); //shop mode seems appropriate for menu, very slow fade in

        fontManager = new FontManager();

        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        musicManager.dispose();
        fontManager.dispose();
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }
}
