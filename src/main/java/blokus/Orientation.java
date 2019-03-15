package blokus;

public enum Orientation {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    public static Orientation fromString (String string) {
        switch (string.toLowerCase()) {
            case "up":
                return UP;
            case "down":
                return DOWN;
            case "left":
                return LEFT;
            case "right":
                return RIGHT;
            default:
                throw new RuntimeException("Invalid string " + string + "!");
        }
    }
}
