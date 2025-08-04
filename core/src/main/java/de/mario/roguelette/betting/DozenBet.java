package de.mario.roguelette.betting;

public class DozenBet extends RangeBet {

    private final Dozen dozen;

    public enum Dozen {
        FIRST_DOZEN_1_TO_12(0),
        SECOND_DOZEN_13_TO_24(1),
        THIRD_DOZEN_25_TO_36(2);

        public final int dozen;

        Dozen(int dozen) {
            this.dozen = dozen;
        }

        @Override
        public String toString() {
            switch (dozen) {
                case 0:
                    return "1st 12";
                case 1:
                    return "2nd 12";
                case 2:
                    return "3rd 12";
            }
            //should be unreachable
            return super.toString();
        }
    }

    public DozenBet(Dozen dozen) {
        super(dozen.dozen * 12 + 1, (dozen.dozen + 1) * 12);
        this.dozen = dozen;
    }

    @Override
    public float getPayoutMultiplier() {
        return 3f;
    }

    @Override
    public boolean isInsideBet() {
        return false;
    }

    public Dozen getDozen() {
        return dozen;
    }
}
