package network.aika.enums;

public enum Direction {
    INPUT,
    OUTPUT;

    private Direction inverted;

    static {
        INPUT.inverted = OUTPUT;
        OUTPUT.inverted = INPUT;
    }

    public Direction invert() {
        return inverted;
    }
}
