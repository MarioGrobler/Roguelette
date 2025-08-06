package de.mario.roguelette.items.segments;

import de.mario.roguelette.GameState;
import de.mario.roguelette.wheel.JokerColorSegment;
import de.mario.roguelette.wheel.JokerNumberRangeSegment;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;

public class AddSegmentShopItem extends SegmentShopItem {

    private final Segment segment;
    private final int cost;

    public AddSegmentShopItem(Segment segment, int cost) {
        this.segment = segment;
        this.cost = cost;
    }

    @Override
    public String getShortDescription() {
        if (segment instanceof NumberSegment) {
            return "Adds a Number Segment";
        }
        if (segment instanceof JokerNumberRangeSegment) {
            return "Adds a Range Joker";
        }
        if (segment instanceof JokerColorSegment) {
            return "Adds a Color Joker";
        }
        return "";
    }

    @Override
    public String getDescription() {
        if (segment instanceof NumberSegment) {
            NumberSegment ns = ((NumberSegment) segment);
            return String.format("Number: %d\nColor: %s\nMultiplier: %s", ns.getNumber(), ns.getColor(), ns.getMultiplier());
        }
        if (segment instanceof JokerNumberRangeSegment) {
            JokerNumberRangeSegment ns = ((JokerNumberRangeSegment) segment);
            return String.format("Number: %d - %d\nColor: %s\nMultiplier: %s", ns.getMin(), ns.getMax(), ns.getColor(), ns.getMultiplier());
        }
        if (segment instanceof JokerColorSegment) {
            JokerColorSegment ns = ((JokerColorSegment) segment);
            return String.format("Color: %s\nMultiplier: %s", ns.getColor(), ns.getMultiplier());
        }
        return "";
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    protected void onBuy(final GameState gameState) {
        gameState.getWheel().addSegment(segment);
    }

    public Segment getSegment() {
        return segment;
    }

}
