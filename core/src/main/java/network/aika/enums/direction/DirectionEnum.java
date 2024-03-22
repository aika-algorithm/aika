package network.aika.enums.direction;

public enum DirectionEnum {
    INPUT(Direction.INPUT),
    OUTPUT(Direction.OUTPUT);

    private Direction dir;

    DirectionEnum(Direction dir) {
        this.dir = dir;
    }

    public Direction getDir() {
        return dir;
    }
}
