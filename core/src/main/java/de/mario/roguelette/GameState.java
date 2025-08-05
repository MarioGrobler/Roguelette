package de.mario.roguelette;

import de.mario.roguelette.util.BetManager;
import de.mario.roguelette.util.MathHelper;
import de.mario.roguelette.wheel.Segment;
import de.mario.roguelette.wheel.Wheel;

public class GameState {
    private final Player player;
    private final Wheel wheel;
    private final BetManager betManager;

    public GameState(Player player, Wheel wheel, BetManager betManager) {
        this.player = player;
        this.wheel = wheel;
        this.betManager = betManager;
    }

    public Player getPlayer() {
        return player;
    }

    public Wheel getWheel() {
        return wheel;
    }

    public BetManager getBetManager() {
        return betManager;
    }

    /**
     * @return Balance that is neither currently in hand nor on the bet table
     */
    public int getAvailableBalance() {
        return player.getBalance() - player.getCurrentlyInHand() - betManager.totalAmount();
    }

    public int getBalanceMinusBets() {
        return player.getBalance() - betManager.totalAmount();
    }

    /**
     * Computes the return of the bets for the current segment. Clears the bets afterwards.
     */
    public void applyReturnOfBets(final Segment segment) {
        player.earn(betManager.computeReturn(segment));
        betManager.clear();
    }

    public int magnitudeBalance() {
        return MathHelper.magnitude(player.getBalance());
    }

    public int magnitudeAvailableBalance() {
        return MathHelper.magnitude(getAvailableBalance());
    }

}
