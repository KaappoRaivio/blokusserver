package blokus;

import misc.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public enum PieceID {
    PIECE_1 ("I1", 0, 1, 4, OrientationGroup.SYMMETRIC_90_NO_FLIP),
    PIECE_2 ("I2", 1, 2, 4, OrientationGroup.SYMMETRIC_180_NO_FLIP),
    PIECE_3 ("V3", 2, 3, 5, OrientationGroup.SYMMETRIC_360_NO_FLIP),
    PIECE_4 ("I3", 3, 3, 4, OrientationGroup.SYMMETRIC_180_NO_FLIP),
    PIECE_5 ("O", 4, 4, 4, OrientationGroup.SYMMETRIC_90_NO_FLIP),
    PIECE_6 ("T4", 5, 4, 6, OrientationGroup.SYMMETRIC_360_NO_FLIP),
    PIECE_7 ("I4", 6, 4, 4, OrientationGroup.SYMMETRIC_180_NO_FLIP),
    PIECE_8 ("L4", 7, 4, 5, OrientationGroup.SYMMETRIC_360_FLIP),
    PIECE_9 ("Z4", 8, 4, 6, OrientationGroup.SYMMETRIC_180_FLIP),
    PIECE_10("L5", 9, 5, 4, OrientationGroup.SYMMETRIC_360_FLIP),
    PIECE_11("T5", 10, 5, 6, OrientationGroup.SYMMETRIC_360_NO_FLIP),
    PIECE_12("V5", 11, 5, 5, OrientationGroup.SYMMETRIC_360_NO_FLIP),
    PIECE_13("N", 12, 5, 6, OrientationGroup.SYMMETRIC_360_FLIP),
    PIECE_14("Z5", 13, 5, 6, OrientationGroup.SYMMETRIC_180_FLIP),
    PIECE_15("I5", 14, 5, 4, OrientationGroup.SYMMETRIC_180_NO_FLIP),
    PIECE_16("P", 15, 5, 5, OrientationGroup.SYMMETRIC_360_FLIP),
    PIECE_17("W", 16, 5, 7, OrientationGroup.SYMMETRIC_360_NO_FLIP),
    PIECE_18("U", 17, 5, 6, OrientationGroup.SYMMETRIC_360_NO_FLIP),
    PIECE_19("F", 18, 5, 7, OrientationGroup.SYMMETRIC_360_FLIP),
    PIECE_20("X", 19, 5, 8, OrientationGroup.SYMMETRIC_90_NO_FLIP),
    PIECE_21("Y", 20, 5, 6, OrientationGroup.SYMMETRIC_360_FLIP);

    private static HashMap<String, PieceID> dict = new HashMap<String, PieceID>();

    static {
        Arrays.stream(PieceID.values()).forEach((pieceID) -> dict.put(pieceID.term, pieceID));
    }

    private String term;
    private int ordinal;
    private int amountOfSquares;
    private OrientationGroup relevantOrientations;

    public int getAmountOfCorners() {
        return amountOfCorners;
    }

    private int amountOfCorners;

    PieceID (String term, int ordinal, int amountOfSquares, int amountOfCorners, OrientationGroup relevantOrientations) {
        this.term = term;
        this.ordinal = ordinal;
        this.amountOfSquares = amountOfSquares;
        this.amountOfCorners = amountOfCorners;
        this.relevantOrientations = relevantOrientations;
    }


    public int getOrdinal () {
        return ordinal;
    }

    public int getAmountOfSquares () {
        return amountOfSquares;
    }

    @Override
    public String toString() {
        return /*super.toString().substring(6) + " " +*/ term;
    }

    public List<Pair<Orientation, Boolean>> getAllOrientations () {
        List<Pair<Orientation, Boolean>> orientations = new Vector<>();

        for (Orientation orientation : Orientation.values()) {
            if (relevantOrientations.isRelevant(orientation, true)) {
                orientations.add(new Pair<>(orientation, true));
            }

            if (relevantOrientations.isRelevant(orientation, false)) {
                orientations.add(new Pair<>(orientation, false));
            }
        }

        return orientations;
    }

//    public static class OrientationAndFlip {
//        private Orientation orientation;
//        private boolean flip;
//
//        public Orientation getOrientation() {
//            return orientation;
//        }
//
//        public boolean isFlip() {
//            return flip;
//        }
//
//        public OrientationAndFlip(Orientation orientation, boolean flip) {
//            this.orientation = orientation;
//            this.flip = flip;
//        }
//
//        @Override
//        public String toString() {
//            return "OrientationAndFlip{" +
//                    "orientation=" + orientation +
//                    ", flip=" + flip +
//                    '}';
//        }
//    }

    public static PieceID fromStandardNotation (String notation) {
        if (dict.containsKey(notation)) {
            return dict.get(notation);
        } else {
            throw new RuntimeException("Unknown string " + notation + "!");
        }

    }
}
