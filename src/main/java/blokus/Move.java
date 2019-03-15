package blokus;

import java.util.Objects;

public class Move implements java.io.Serializable {
    @Override
    public String toString() {
//        return "blokus.Move{" +
//                "x=" + x +
//                ", y=" + y +
//                ", pieceID=" + pieceID +
//                ", color=" + color +
//                ", orientation=" + orientation +
//                ", flip=" + flip +
//                '}';
        return "(" + x + ", " + y + ") " + color + " " + pieceID + " " + orientation + " " + flip + ".";
    }

    private int x;
    private int y;
    private PieceID pieceID;
    private int color;
    private Orientation orientation;
    private boolean flip;

    public Move(int x, int y, PieceID pieceID, int color, Orientation orientation, boolean flip) {
        this.x = x;
        this.y = y;
        this.pieceID = pieceID;
        this.color = color;
        this.orientation = orientation;
        this.flip = flip;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PieceID getPieceID() {
        return pieceID;
    }

    public int getColor() {
        return color;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public boolean isFlip() {
        return flip;
    }

    public static Move parseMove (String input, int color) throws Exception {
        input = input.replaceAll("( )+", " "); // Remove multiple spaces
        String[] splitted = input.split(" ");

        if (splitted.length != 5) {
            throw new Exception("Invalid format!");
        }

        int x = Integer.parseInt(splitted[0]);
        int y = Integer.parseInt(splitted[1]);

        PieceID pieceID = PieceID.fromStandardNotation(splitted[2]);

        Orientation orientation = Orientation.fromString(splitted[3]);
        boolean flip = Boolean.parseBoolean(splitted[4]);

        return new Move(x, y, pieceID, color, orientation, flip);


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return x == move.x &&
                y == move.y &&
                color == move.color &&
                flip == move.flip &&
                pieceID == move.pieceID &&
                orientation == move.orientation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, pieceID, color, orientation, flip);
    }
}
