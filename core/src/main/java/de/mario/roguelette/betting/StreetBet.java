package de.mario.roguelette.betting;

public class StreetBet extends RangeBet {

    public enum Street {
        STREET_1_TO_3(0),
        STREET_4_TO_6(1),
        STREET_7_TO_9(2),
        STREET_10_TO_12(3),
        STREET_13_TO_15(4),
        STREET_16_TO_18(5),
        STREET_19_TO_21(6),
        STREET_22_TO_24(7),
        STREET_25_TO_27(8),
        STREET_28_TO_30(9),
        STREET_31_TO_33(10),
        STREET_34_TO_36(11);

        public final int street;

        private Street(int street) {
            this.street = street;
        }
    }

    protected StreetBet(Street street) {
        super(street.street * 3 + 1, (street.street + 1) * 3);
    }

    @Override
    public float getPayoutMultiplier() {
        return 12f;
    }
}
