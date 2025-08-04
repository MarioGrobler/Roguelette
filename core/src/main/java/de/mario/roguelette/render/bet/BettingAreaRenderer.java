package de.mario.roguelette.render.bet;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import de.mario.roguelette.betting.*;
import de.mario.roguelette.util.BetManager;
import de.mario.roguelette.wheel.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class BettingAreaRenderer {
    private final List<BetRegion> regions = new ArrayList<>();

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final Rectangle bounds;

    private final BetManager betManager;

    public BettingAreaRenderer(final BetManager betManager, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font) {
        this(betManager, shapeRenderer, batch, font, new Rectangle(0, 0, 720, 120));
    }

    public BettingAreaRenderer(final BetManager betManager, final ShapeRenderer shapeRenderer, final SpriteBatch batch, final BitmapFont font, final Rectangle bounds) {
        this.betManager = betManager;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.bounds = bounds;

        updateBetValues(0);
        buildLayout();
    }

    private void buildLayout() {

        // first build non-inside bets...
        buildNumberGrid();
        buildDozenGrid();
        buildColumnGrid();
        buildParityFields();
        buildColorFields();
        buildHalfFields();

        // then build inside bets to have them "on top"
        buildStreetFields();
        buildSixLineFields();
        buildCornerFields();
        buildHorizontalSplitFields();
        buildVerticalSplitFields();
    }

    public void updateBetValues(int currentMagnitude) {
        for (BetRegion region : regions) {
            region.deleteChip();
        }

        for (BetRegion region : regions) {
            for (Bet bet : betManager.getBets()) {
                if (region instanceof NumberRegion && bet.getBetType() instanceof NumberBet) {
                    if (((NumberRegion) region).getNumber() == ((NumberBet) bet.getBetType()).getNumber()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof DozenRegion && bet.getBetType() instanceof DozenBet) {
                    if (((DozenRegion) region).getDozen() == ((DozenBet) bet.getBetType()).getDozen()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof ColumnRegion && bet.getBetType() instanceof ColumnBet) {
                    if (((ColumnRegion) region).getColumn() == ((ColumnBet) bet.getBetType()).getColumn()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof ParityRegion && bet.getBetType() instanceof ParityBet) {
                    if (((ParityRegion) region).isEven() == ((ParityBet) bet.getBetType()).isEven()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof ColorRegion && bet.getBetType() instanceof ColorBet) {
                    if (((ColorRegion) region).getSegmentColor() == ((ColorBet) bet.getBetType()).getSegmentColor()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof HalfRegion && bet.getBetType() instanceof HalfBet) {
                    if (((HalfRegion) region).isLow() == ((HalfBet) bet.getBetType()).isLow()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof StreetRegion && bet.getBetType() instanceof StreetBet) {
                    if (((StreetRegion) region).getStreet() == ((StreetBet) bet.getBetType()).getStreet()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof SixLineRegion && bet.getBetType() instanceof SixLineBet) {
                    if (((SixLineRegion) region).getSixLine() == ((SixLineBet) bet.getBetType()).getSixLine()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof CornerRegion && bet.getBetType() instanceof CornerBet) {
                    if (((CornerRegion) region).getFirstNumber() == ((CornerBet) bet.getBetType()).getFirstNumber()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof HorizontalSplitRegion && bet.getBetType() instanceof HorizontalSplitBet) {
                    if (((HorizontalSplitRegion) region).getFirstNumber() == ((HorizontalSplitBet) bet.getBetType()).getFirstNumber()) {
                        createChip(region, bet, currentMagnitude);
                    }
                } else if (region instanceof VerticalSplitRegion && bet.getBetType() instanceof VerticalSplitBet) {
                    if (((VerticalSplitRegion) region).getFirstNumber() == ((VerticalSplitBet) bet.getBetType()).getFirstNumber()) {
                        createChip(region, bet, currentMagnitude);
                    }
                }
            }
        }
    }

    private void createChip(BetRegion region, Bet bet, int currentMagnitude) {
        region.makeChip(bet.getAmount());
        int b = (int) (region.chip.getValue() / Math.pow(10, currentMagnitude));
        region.chip.color = region.chip.colorForValue(b);
    }

    private void buildNumberGrid() {
        float cellWidth = bounds.width / 12;
        float cellHeight = bounds.height / 3;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 12; col++) {
                int number = 3 * col + row + 1;
                float x = bounds.x + col * cellWidth;
                float y = bounds.y + row * cellHeight;
                Rectangle cellBounds = new Rectangle(x, y, cellWidth, cellHeight);
                regions.add(new NumberRegion(number, cellBounds, shapeRenderer, batch, font));
            }
        }

        // add zero separately
        Rectangle zeroBounds = new Rectangle(bounds.x - cellWidth, bounds.y, cellWidth, cellHeight * 3);
        regions.add(new NumberRegion(0, zeroBounds, shapeRenderer, batch, font));
    }

    private void buildDozenGrid() {
        float cellWidth = bounds.width / 3;
        float cellHeight = bounds.height / 3;

        for (DozenBet.Dozen dozen : DozenBet.Dozen.values()) {
            float x = bounds.x + dozen.dozen * cellWidth; //TODO dangerous
            float y = bounds.y - cellHeight;
            Rectangle dozenBounds = new Rectangle(x, y, cellWidth, cellHeight);
            regions.add(new DozenRegion(dozen, dozenBounds, shapeRenderer, batch, font));
        }
    }

    private void buildColumnGrid() {
        float cellWidth = bounds.width / 12;
        float cellHeight = bounds.height / 3;

        // order is important!
        ColumnBet.Column[] cols = {ColumnBet.Column.FIRST_COLUMN_CONGRUENT_1,
            ColumnBet.Column.SECOND_COLUMN_CONGRUENT_2,
            ColumnBet.Column.THIRD_COLUMN_CONGRUENT_0};

        for (int i = 0; i < cols.length; i++) {
            float x = bounds.x + 12 * cellWidth;
            float y = bounds.y + i * cellHeight;
            Rectangle columnBounds = new Rectangle(x, y, cellWidth, cellHeight);
            regions.add(new ColumnRegion(cols[i], columnBounds, shapeRenderer, batch, font));
        }
    }

    private void buildParityFields() {
        float cellWidth = bounds.width / 6;
        float cellHeight = bounds.height / 3;

        float y = bounds.y - 2 * cellHeight;
        float x1 = bounds.x + cellWidth;
        float x2 = bounds.x + 4 * cellWidth;

        Rectangle evenBounds = new Rectangle(x1, y, cellWidth, cellHeight);
        Rectangle oddBounds = new Rectangle(x2, y, cellWidth, cellHeight);
        regions.add(new ParityRegion(true, evenBounds, shapeRenderer, batch, font));
        regions.add(new ParityRegion(false, oddBounds, shapeRenderer, batch, font));
    }

    private void buildColorFields() {
        float cellWidth = bounds.width / 6;
        float cellHeight = bounds.height / 3;

        float y = bounds.y - 2 * cellHeight;
        float x1 = bounds.x + 2 * cellWidth;
        float x2 = bounds.x + 3 * cellWidth;

        Rectangle redBounds = new Rectangle(x1, y, cellWidth, cellHeight);
        Rectangle blackBounds = new Rectangle(x2, y, cellWidth, cellHeight);
        regions.add(new ColorRegion(Segment.SegmentColor.RED, redBounds, shapeRenderer, batch, font));
        regions.add(new ColorRegion(Segment.SegmentColor.BLACK, blackBounds, shapeRenderer, batch, font));
    }

    private void buildHalfFields() {
        float cellWidth = bounds.width / 6;
        float cellHeight = bounds.height / 3;

        float y = bounds.y - 2 * cellHeight;
        float x1 = bounds.x;
        float x2 = bounds.x + 5 * cellWidth;

        Rectangle lowBounds = new Rectangle(x1, y, cellWidth, cellHeight);
        Rectangle highBounds = new Rectangle(x2, y, cellWidth, cellHeight);
        regions.add(new HalfRegion(true, lowBounds, shapeRenderer, batch, font));
        regions.add(new HalfRegion(false, highBounds, shapeRenderer, batch, font));
    }

    private void buildStreetFields() {
        float cellWidth = bounds.width / 12;
        float y = bounds.y;

        for (StreetBet.Street street : StreetBet.Street.values()) {
            float x = bounds.x + (street.street + 0.5f) * cellWidth;
            Circle bounds = new Circle(x, y, 15);
            regions.add(new StreetRegion(street, bounds, shapeRenderer, batch, font));
        }
    }

    private void buildSixLineFields() {
        float cellWidth = bounds.width / 12;
        float y = bounds.y;

        for (SixLineBet.SixLine sixLine : SixLineBet.SixLine.values()) {
            float x = bounds.x + (sixLine.sixLine + 1) * cellWidth;
            Circle bounds = new Circle(x, y, 15);
            regions.add(new SixLineRegion(sixLine, bounds, shapeRenderer, batch, font));
        }
    }

    private void buildCornerFields() {
        float cellWidth = bounds.width / 12;
        float cellHeight = bounds.height / 3;

        for (int i = 1; i <= 32; i++) {
            if (i % 3 == 0) continue;

            float x = bounds.x + ((i - 1) / 3 + 1) * cellWidth;
            float y = bounds.y + (i % 3) * cellHeight;
            Circle bounds = new Circle(x, y, 15);
            regions.add(new CornerRegion(i, bounds, shapeRenderer, batch, font));
        }
    }

    private void buildHorizontalSplitFields() {
        float cellWidth = bounds.width / 12;
        float cellHeight = bounds.height / 3;

        for (int i = 1; i <= 35; i++) {
            if (i % 3 == 0) continue;

            float x = bounds.x + ((i - 1) / 3 + 0.5f) * cellWidth;
            float y = bounds.y + (i % 3) * cellHeight;
            Circle bounds = new Circle(x, y, 15);
            regions.add(new HorizontalSplitRegion(i, bounds, shapeRenderer, batch, font));
        }
    }

    private void buildVerticalSplitFields() {
        float cellWidth = bounds.width / 12;
        float cellHeight = bounds.height / 3;

        for (int i = 1; i <= 33; i++) {

            float x = bounds.x + ((i - 1) / 3 + 1) * cellWidth;
            int m = i % 3;
            float y = bounds.y + ((m == 0 ? 3 : m) - 0.5f) * cellHeight;
            Circle bounds = new Circle(x, y, 15);
            regions.add(new VerticalSplitRegion(i, bounds, shapeRenderer, batch, font));
        }
    }

    public Optional<Bet> handleLeftClick(float x, float y, int amount) {
        if (amount == 0) {
            return Optional.empty();
        }

        // iterate through the list reversed, as the inside bets are the last entries, and they need to be handled first
        ListIterator<BetRegion> li = regions.listIterator(regions.size());
        while (li.hasPrevious()) {
            BetRegion region = li.previous();
            if (region.contains(x, y)) {
                return Optional.of(region.createBet(amount));
            }
        }
        return Optional.empty();
    }

    public Optional<BetType> handleRightClick(float x, float y) {
        // iterate through the list reversed, as the inside bets are the last entries, and they need to be handled first
        ListIterator<BetRegion> li = regions.listIterator(regions.size());
        while (li.hasPrevious()) {
            BetRegion region = li.previous();
            if (region.contains(x, y)) {
                return Optional.of(region.createBet(0).getBetType());
            }
        }
        return Optional.empty();
    }

    public void render() {
        for (BetRegion region : regions) {
            region.render();
        }
    }

    public boolean contains(float x, float y) {
        //TODO optimize number fields
        for (BetRegion region : regions) {
            if (region.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

}
