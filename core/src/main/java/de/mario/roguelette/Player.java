package de.mario.roguelette;

public class Player {
    private int balance;
    private int currentlyInHand = 0; //amount of money currently in hand to be placed somewhere
    private final String name;

    public Player(int money, String name) {
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
