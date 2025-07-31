package de.mario.roguelette.exception;

public class GameException extends RuntimeException {

    //TODO: in future it might be a good idea to be more specific than just "GameException"
    public GameException(String message) {
        super(message);
    }
}
