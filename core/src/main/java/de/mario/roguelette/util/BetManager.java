package de.mario.roguelette.util;

import de.mario.roguelette.betting.Bet;
import de.mario.roguelette.betting.BetType;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BetManager {
    private final List<Bet> bets = new ArrayList<>();

    public void clear() {
        bets.clear();
    }

    /**
     * Adds the given bet. If there is also ready a bet with equivalent bet type, increase the amount of this bet instead.
     * @param bet the bet to add or update
     */
    public void addBet(final Bet bet) {
        for (Bet b : bets) {
            if (b.getBetType().equals(bet.getBetType())) {
                b.increaseAmount(bet.getAmount());
                return;
            }
        }
        bets.add(bet);
    }

    public void removeBet(final BetType betType) {
        Iterator<Bet> iterator = bets.iterator();
        while (iterator.hasNext()) {
            Bet b = iterator.next();
            if (b.getBetType().equals(betType)) {
                iterator.remove(); // should be ok as there is at most one fitting element by design
                break;
            }
        }
    }

    public List<Bet> getBets() {
        return Collections.unmodifiableList(bets);
    }

    public int totalAmount() {
        int total = 0;
        for (Bet b : bets) {
            total += b.getAmount();
        }
        return total;
    }

    public int computeReturn(Segment segment) {
        float ret = 0;
        for (Bet b : bets) {
            ret += b.getPayout(segment);
        }
        return (int) ret;
    }
}
