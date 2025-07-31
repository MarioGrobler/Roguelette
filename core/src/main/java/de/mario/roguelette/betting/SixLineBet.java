package de.mario.roguelette.betting;

public class SixLineBet extends RangeBet {

    public enum SixLine {
        SIX_LINE_1_TO_6(0),
        SIX_LINE_4_TO_9(1),
        SIX_LINE_7_TO_12(2),
        SIX_LINE_10_TO_15(3),
        SIX_LINE_13_TO_18(4),
        SIX_LINE_16_TO_21(5),
        SIX_LINE_19_TO_24(6),
        SIX_LINE_22_TO_27(7),
        SIX_LINE_25_TO_30(8),
        SIX_LINE_28_TO_33(9),
        SIX_LINE_31_TO_36(10);

        public final int sixLine;

        private SixLine(int sixLine) {
            this.sixLine = sixLine;
        }
    }

    protected SixLineBet(SixLine sixLine) {
        super(sixLine.sixLine * 3 + 1, (sixLine.sixLine + 2) * 3);
    }

    @Override
    public float getPayoutMultiplier() {
        return 6f;
    }
}
