package de.mario.roguelette.items.segments;

import de.mario.roguelette.GameState;

public class DeleteSegmentShopItem extends SegmentShopItem {

    public DeleteSegmentShopItem(int cost) {
        this.cost = cost;
    }

    @Override
    public String getShortDescription() {
        return "Segment Remover";
    }

    @Override
    public String getDescription() {
        return "Left click on a segment on the wheel to remove it. Right click to cancel.";
    }


    /**
     * If the player can afford this item, sets the game state mode to
     * {@link de.mario.roguelette.GameState.GameStateMode#DELETE_SEGMENT_SELECTING}.
     * Then, expects the player to select a segment and call {@link #tryBuy(GameState, int)}.
     * @return true if the player can afford this item
     */
    @Override
    public boolean tryBuy(GameState gameState) {
        if (!sold && canBuy(gameState.getPlayer())) {
            gameState.pushState(GameState.GameStateMode.DELETE_SEGMENT_SELECTING);
            gameState.setPendingDeleteItem(this);
            return true;
        }
        return false;
    }

    /**
     * Actually tries to "buy" the item: deletes the segment with the given index from the wheel and charges the player.
     * Afterwards, resets game state (to {@link de.mario.roguelette.GameState.GameStateMode#DELETE_SEGMENT_SELECTING}).
     * @param segmentIndex the index of the segment to delete
     * @return true if successful
     */
    public boolean tryBuy(final GameState gameState, int segmentIndex) {
        if (!sold && canBuy(gameState.getPlayer())) {
            gameState.getWheel().removeSegmentAt(segmentIndex);
            gameState.getPlayer().pay(getCost());
            gameState.setPendingDeleteItem(null);
            gameState.popState();
            gameState.getShop().increaseNumOfSoldDeletes();
            sold = true;
            return true;
        }
        return false;
    }

    @Override
    protected void onBuy(GameState gameState) {
        // not used here
    }
}
