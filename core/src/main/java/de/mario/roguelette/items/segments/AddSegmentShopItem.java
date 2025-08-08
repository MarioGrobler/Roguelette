package de.mario.roguelette.items.segments;

import de.mario.roguelette.GameState;
import de.mario.roguelette.wheel.Segment;

public class AddSegmentShopItem extends SegmentShopItem {

    private final Segment segment;

    public AddSegmentShopItem(Segment segment, int cost) {
        this.segment = segment;
        this.cost = cost;
    }

    @Override
    public String getShortDescription() {
        return segment.getShortDescription();
    }

    @Override
    public String getDescription() {
        return String.format("%s\nCost: %d\nBuying this adds the segment immediately.", segment.getDescription(), cost);
    }

    @Override
    protected void onBuy(final GameState gameState) {
        gameState.getWheel().addSegmentRandom(segment);
    }

    public Segment getSegment() {
        return segment;
    }

}
