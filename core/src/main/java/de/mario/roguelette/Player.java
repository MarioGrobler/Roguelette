package de.mario.roguelette;

import de.mario.roguelette.characters.Character;
import de.mario.roguelette.events.GameEventListener;
import de.mario.roguelette.items.Inventory;

import java.util.List;

public class Player {
    private final Inventory inventory;

    private long balance;
    private long currentlyInHand = 0; //amount of money currently in hand to be placed somewhere
    private final String name;

    private final Character character;
    private final List<GameEventListener> characterListeners;

    /**
     * @param startingBalance from the run's {@code RunConfig} (baseline = the character's starting
     *                        balance; Casino Curses may modify it)
     */
    public Player(final Inventory inventory, final Character character, final long startingBalance) {
        this.inventory = inventory;
        this.character = character;
        this.balance = startingBalance;
        this.name = character.getName();
        this.characterListeners = character.createListeners();
    }

    public Character getCharacter() {
        return character;
    }

    /**
     * The character's run-level passive listeners, instantiated once for this run. Aggregated by
     * {@code GameState.collectListeners} ahead of fortunes and chances.
     */
    public List<GameEventListener> getCharacterListeners() {
        return characterListeners;
    }

    public long getBalance() {
        return balance;
    }

    public long getCurrentlyInHand() {
        return currentlyInHand;
    }

    public void increaseHandBy(long amount) {
        currentlyInHand += amount;
    }

    public void resetHand() {
        currentlyInHand = 0;
    }

    public String getName() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void pay(long amount) {
        this.balance -= amount;
    }

    public void earn(long amount) {
        this.balance += amount;
    }

    public boolean canAfford(long amount) {
        // strictly greater as a balance of 0 means game over
        return this.balance > amount;
    }

    public boolean isDead() {
        return this.balance <= 0;
    }

}
