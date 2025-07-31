package de.mario.roguelette;

import com.badlogic.gdx.Game;
import de.mario.roguelette.screens.MainMenuScreen;


public class RougeletteGame extends Game {

    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }

}
