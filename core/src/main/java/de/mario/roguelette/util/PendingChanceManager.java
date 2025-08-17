package de.mario.roguelette.util;

import de.mario.roguelette.items.chances.PendingChanceShopItem;

import java.util.ArrayList;
import java.util.List;

public class PendingChanceManager {
    private final List<PendingChanceShopItem> activeChances = new ArrayList<>();

    public List<PendingChanceShopItem> getActiveChances() {
        return activeChances;
    }

    /**
     * Adds the given chance if there is not a chance of the same type already present.
     * If there is a chance of the same type already present, increases the duration instead.
     */
    public void add(final PendingChanceShopItem item) {
        for (PendingChanceShopItem activeItem : activeChances) {
            if (activeItem.getClass().equals(item.getClass())) {
                activeItem.setDuration(activeItem.getDuration() + item.getDuration());
                return;
            }
        }
        activeChances.add(item);
    }

    public void resetActiveChances() {
        activeChances.clear();
    }

    public void removeDeadChances() {
        activeChances.removeIf(item -> item.getDuration() <= 0);
    }
}
