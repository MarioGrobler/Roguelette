package de.mario.roguelette;

import de.mario.roguelette.items.Inventory;

public class Player {
    private final Inventory inventory;

    private int balance;
    private int currentlyInHand = 0; //amount of money currently in hand to be placed somewhere
    private final String name;

    public Player(final Inventory inventory, int money, String name) {
        this.inventory = inventory;
        this.balance = money;
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public int getCurrentlyInHand() {
        return currentlyInHand;
    }

    public void increaseHandBy(int amount) {
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

    public void pay(int amount) {
        this.balance -= amount;
    }

    public void earn(int amount) {
        this.balance += amount;
    }

    public boolean canAfford(int amount) {
        // strictly greater as a balance of 0 means game over
        return this.balance > amount;
    }

    public boolean isDead() {
        return this.balance <= 0;
    }

}
