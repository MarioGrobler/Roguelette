package de.mario.roguelette.betting;

import de.mario.roguelette.wheel.JokerColorSegment;
import de.mario.roguelette.wheel.JokerNumberRangeSegment;
import de.mario.roguelette.wheel.NumberSegment;
import de.mario.roguelette.wheel.Segment;

import java.util.Objects;

public class ColumnBet implements BetType {

    public enum Column {
        THIRD_COLUMN_CONGRUENT_0(0),
        FIRST_COLUMN_CONGRUENT_1(1),
        SECOND_COLUMN_CONGRUENT_2(2);

        public final int column;

        private Column(final int column) {
            this.column = column;
        }

        @Override
        public String toString() {
            return "2to1";
        }
    }

    private final Column column;

    public ColumnBet(Column column) {
        this.column = column;
    }

    @Override
    public boolean isWinningSegment(Segment segment) {
        if (segment instanceof NumberSegment) {
            int number = ((NumberSegment)segment).getNumber();
            return number != 0 && number % 3 == column.column;
        }
        if (segment instanceof JokerNumberRangeSegment) {
            JokerNumberRangeSegment range = (JokerNumberRangeSegment) segment;
            //only false if range consists of at most two numbers, and these numbers are not in the column
            return (range.getMax() - range.getMin() >= 2) ||
                range.getMin() % 3 == column.column || range.getMax() % 3 == column.column;
        }

        // as every column contains both colors, a JokerColorSegment always returns true
        return segment instanceof JokerColorSegment;
    }

    @Override
    public float getPayoutMultiplier() {
        return 3f;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ColumnBet columnBet = (ColumnBet) o;
        return column == columnBet.column;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(column);
    }

    public Column getColumn() {
        return column;
    }
}
