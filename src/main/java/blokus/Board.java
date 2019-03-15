package blokus;

import misc.Pair;
import misc.Saver;

import java.io.Serializable;
import java.util.*;


public class Board implements Serializable {
    public static final Board DUO_BOARD = new Board(14, 14, new MyPieceManager(2));
    public static final Board ORIGINAL_BOARD = new Board(20, 20, new MyPieceManager(4), true);



    private static final int NO_PIECE = -1;
    private static final int EDGE = -2;


    private int[][] board;
    private int[][] errorBoard;

    private int dimX;
    private int dimY;
    private int amountOfPlayers;


    private PieceManager pieceManager;
    private boolean startFromCorners;

    private List<int[][]> moveHistory = new Vector<>();

    private Saver<Board> saver;

    public Board(int dimX, int dimY, PieceManager pieceManager) {
        this(dimX, dimY, pieceManager, false);
    }

    public Board (int dimX, int dimY, PieceManager pieceManager, boolean startFromCorners) {
        this.dimX = dimX;
        this.dimY = dimY;
        amountOfPlayers = pieceManager.getAmountOfPlayers();

        this.pieceManager = pieceManager;
        this.startFromCorners = startFromCorners;
        saver = new Saver<>();


        board = new int[dimY][dimX];
        errorBoard = new int[dimY][dimX];

        initializeBoards();

    }

    private void saveUndoState () {
        int[][] oldBoard = new int[dimY][dimX];

        for (int y = 0; y < dimY; y++) {
            if (dimX >= 0) System.arraycopy(board[y], 0, oldBoard[y], 0, dimX);
        }

        moveHistory.add(oldBoard);

    }

    public void undo (int depth) {
        if (moveHistory.size() - 1 - depth >= 0) {
            int[][] oldBoard = moveHistory.get(moveHistory.size() - depth - 1);
            moveHistory.remove(moveHistory.size() - 1 - depth);
            board = oldBoard;
            pieceManager.undo(depth + 1);

        } else {
            throw new RuntimeException("Can't undo this far! " + depth + " " + moveHistory.size());
        }

    }

    private void initializeBoards () {
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                errorBoard[y][x] = NO_PIECE;
                board[y][x] = NO_PIECE;
            }
        }
    }

    private boolean putOnBoard(int baseX, int baseY, PieceID pieceID, int color, Orientation orientation, boolean flip) {
        if (pieceManager.isOnBoard(pieceID, color)) {
            throw new RuntimeException("blokus.Piece " + pieceID + "already on board");
        }

        Piece piece = pieceManager.getCachedPiece(pieceID, color).rotate(orientation, flip);

        if (fits(baseX, baseY, pieceID, color, orientation, flip)) {
            dummyPut(baseX, baseY, piece);
            addToPiecesOnBoard(piece);
            piece.placeOnBoard(baseX, baseY);
            return true;
        } else {
            errorPut(baseX, baseY, piece);
            return false;
        }
    }

    public boolean putOnBoard (Move move) {
        return putOnBoard(move.getX(), move.getY(), move.getPieceID(), move.getColor(), move.getOrientation(), move.isFlip());
    }

    private int safeOffset(int baseX, int baseY, int offsetX, int offsetY) {
        try {
            return board[baseY + offsetY][baseX + offsetX];
        } catch (ArrayIndexOutOfBoundsException e) {
            return EDGE;
        }
    }

    private boolean fits(int baseX, int baseY, PieceID pieceID, int color, Orientation orientation, boolean flip) {
        return fits(baseX, baseY, pieceID, color, orientation, flip, true);
    }

    private boolean fits (int baseX, int baseY, PieceID pieceID, int color, Orientation orientation, boolean flip, boolean noDupe) {
        if (pieceManager.isOnBoard(pieceID, color) && noDupe) {
            return false;
        } else {
            Piece piece = pieceManager.getCachedPiece(pieceID, color).rotate(orientation, flip);
            return fits(baseX, baseY, piece);
        }


    }

    private boolean fits (int baseX, int baseY, Piece piece) {

        char[][] mesh = piece.getMesh();

        boolean isConnected = false;
        boolean fits = true;
        boolean touchesCorner = false;


        for (int y = 0; y < piece.getDimY(); y++) {
            for (int x = 0; x < piece.getDimX(); x++) {
                char current = mesh[y][x];
                if (current == Piece.TRANSPARENT) {
                    continue;
                }

                int absX = baseX + x;
                int absY = baseY + y;

                if (safeOffset(absX, absY, 0, 0) != NO_PIECE) {
                    return false;
                }

                if (!touchesCorner && isCorner(absX, absY)) {

                    touchesCorner = true;
                }

                int topRight = safeOffset(absX, absY, +1, -1);
                int topLeft = safeOffset(absX, absY, -1, -1);
                int bottomRight = safeOffset(absX, absY, +1, +1);
                int bottomLeft = safeOffset(absX, absY, -1, +1);


                if (!isConnected &&
                       (topRight == piece.getColor() ||
                        topLeft == piece.getColor() ||
                        bottomRight == piece.getColor() ||
                        bottomLeft == piece.getColor())
                    ) {
                    isConnected = true;
                }

                if (!adjacentsFree(absX, absY, piece.getColor())) {
                    return false;
                }
            }
        }

        if (!isColorOnBoard(piece.getColor()) && touchesCorner) {
            isConnected = true;
        }


        return isConnected;

    }

    public boolean adjacentsFree (int x, int y, int color) {
        int top     = safeOffset(x, y, 0, -1);
        int bottom  = safeOffset(x, y, 0, +1);
        int left    = safeOffset(x, y, -1, 0);
        int right   = safeOffset(x, y, +1, 0);

        return top != color &&
                bottom != color &&
                left != color &&
                right != color;
    }



    public boolean fits (Move move) {
        try {
            return fits(move.getX(), move.getY(), move.getPieceID(), move.getColor(), move.getOrientation(), move.isFlip());
        } catch (NullPointerException e) {
            System.out.println(move);
            throw e;
        }
    }

    private void addToPiecesOnBoard (Piece piece) {
        pieceManager.placeOnBoard(piece.getID(), piece.getColor());
    }

    private boolean isColorOnBoard (int color) {
        return pieceManager.isColorOnBoard(color);
    }

    private void dummyPut (int baseX, int baseY, Piece piece) {
        char[][] mesh = piece.getMesh();
        saveUndoState();

        for (int y = 0; y < mesh.length; y++) {
            for (int x = 0; x < mesh[y].length; x++) {
                char current = mesh[y][x];

                switch (current) {
                    case Piece.TRANSPARENT:
                        break;
                    case Piece.OPAQUE:
                        board[baseY + y][baseX + x] = piece.getColor();
                        break;
                    default:
                        throw new RuntimeException("Invalid piece " + piece + ", " + current);
                }

            }
        }
    }

    private void errorPut (int baseX, int baseY, Piece piece) {
        char[][] mesh = piece.getMesh();

        for (int y = 0; y < mesh.length; y++) {
            for (int x = 0; x < mesh[y].length; x++) {
                char current = mesh[y][x];

                switch (current) {
                    case Piece.OPAQUE:
                        try {
                            errorBoard[baseY + y][baseX + x] = piece.getColor();
                        } catch (ArrayIndexOutOfBoundsException e) {
//                            e.printStackTrace();
                        }
                        break;
                    case Piece.TRANSPARENT:
                        break;
                    default:
                        throw new RuntimeException("Invalid piece " + piece);
                }

            }
        }
    }

    public boolean hasMoves (int color) {
        return getAllFittingMoves(color).size() != 0;
    }

    public boolean canPlay () {
        for (int i = 0; i < getAmountOfPlayers(); i++) {
            if (!hasMoves(i)) {
                return false;
            }
        }

        return true;
    }

    private static char getMatchingChar (int color) {
        if (color == -1) {
            return Piece.TRANSPARENT;
        }
        return (char) (color + 48);
    }

    private static int getPieceColorFromChar (char color) {
        return ((int) color) - 48;
    }

    public int[][] getBoard() {
        return board;
    }

    public String toString () {
        StringBuilder builder = new StringBuilder();

        builder.append("\n  ");
        for (int x = 0; x < dimX; x++) {
            builder.append(getStringFromX(x)).append(" ");
        }

        for (int y = 0; y < dimY; y++) {
            int[] row = board[y];
            builder.append("\n");
            builder.append(getStringFromY(y)).append(" ");
            for (int index = 0; index < row.length - 1; index++) {
                if (errorBoard[y][index] != NO_PIECE) {
                    builder.append('E');
                } else {
                    builder.append(getMatchingChar(row[index]));
                }
                builder.append(" ");
            }


            if (errorBoard[y][row.length - 1] != NO_PIECE) {
                builder.append('E');
            } else {
                builder.append(getMatchingChar(row[row.length - 1]));
            }

            builder.append(" ").append(getStringFromY(y));
        }

        builder.append("\n  ");

        for (int x = 0; x < dimX; x++) {
            builder.append(getStringFromX(x)).append(" ");
        }

        return builder.toString();
    }

    private String getStringFromX (int x) {
        return String.valueOf(x % 10);
    }

    private String getStringFromY (int y) {
        return String.valueOf(y % 10);
    }



    private String save (String name) {
        String path = System.getProperty("user.dir") + "/src/main/resources/boards/" + name + ".ser";
        saver.save(this, path, false);
        return path;

    }

    public String save () {
        return save(new Date().toString());
    }

    public static Board fromFile (String path, boolean relative) {
        String absolutePath;

        if (relative) {
            absolutePath = System.getProperty("user.dir") + "/src/main/resources/boards/" + path;
            System.out.println(System.getProperty("user.dir"));
        } else {
            absolutePath = path;
        }

        return new Saver<Board>().fromFile(absolutePath, false);
    }

    public static Board fromHumanReadableFile (String filePath, boolean relative) {
        throw new RuntimeException(new NotImplementedError());
    }

    private List<PieceID> getPiecesNotOnBoard (int color) {
        return pieceManager.getPiecesNotOnBoard(color);
    }

    public List<Move> getAllFittingMoves (int color) {
        return getAllFittingMoves(color, getPiecesNotOnBoard(color));
    }

    private List<Move> getAllFittingMoves (int color, List<PieceID> pieces) {
        List<Move> moves = new ArrayList<>();
        for (Position boardPosition : getEligibleCorners(color)) {
            moves.addAll(getAllFittingMoves(color, boardPosition.x, boardPosition.y, pieces));
        }


        return moves;
    }

    private List<Move> getAllFittingMoves (int color, int x, int y, List<PieceID> pieces) {
        List<Move> moves = new ArrayList<>();

        for (PieceID pieceID : pieces) {
            moves.addAll(getAllFittingMoves(color, x, y, pieceID));
        }

        return moves;
    }


    private List<Move> getAllFittingMoves (int color, int x, int y, PieceID pieceID) {
        List<Move> moves = new ArrayList<>();
        Piece piece = pieceManager.getCachedPiece(pieceID, color);

//        if (pieceID == PieceID.fromStandardNotation("L4")) {


        for (Pair<Orientation, Boolean> orientationAndFlip: pieceID.getAllOrientations()) {
            for (Position position : piece.getSquares()) {
                int baseX = x - position.x;
                int baseY = y - position.y;

                if (fits(baseX, baseY, piece.rotate(orientationAndFlip.getK(), orientationAndFlip.getV()))) {
                    moves.add(new Move(baseX, baseY, pieceID, color, orientationAndFlip.getK(), orientationAndFlip.getV()));
                }
            }
        }

        return moves;
    }

    private boolean isCorner (int x, int y) {
        if (startFromCorners) {
            return (x == 0 || x == dimX - 1) && (y == 0 || y == dimY - 1);
        } else {
            return (x == 4 && y == 4) || (x == dimX - 5 && y == dimY - 5);
        }

    }

    private boolean isEligibleCorner (int color, int x, int y) {
        if (!isColorOnBoard(color) && isCorner(x, y) && isEmpty(x, y)) {
            return true;
        }

        int topRight = safeOffset(x, y, 1, -1);
        int bottomRight = safeOffset(x, y, 1, 1);
        int topLeft = safeOffset(x, y, -1, -1);
        int bottomLeft = safeOffset(x, y, -1, 1);

        int left = safeOffset(x, y, -1 ,0);
        int right = safeOffset(x, y, 1 ,0);
        int top = safeOffset(x, y, 0 ,-1);
        int bottom = safeOffset(x, y, 0 ,1);

        boolean edges = left != color && right != color && top != color && bottom != color;
        boolean corners = topRight == color || bottomRight == color || topLeft == color || bottomLeft == color;

        return edges && corners;
    }

    public boolean isEmpty(int x, int y) {
        try{
            return board[y][x] == NO_PIECE;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    public List<Position> getEligibleCorners(int color) {
        List<Position> corners = new ArrayList<>();

        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {

                if (isEligibleCorner(color, x, y)) {
                    corners.add(new Position(x, y));
                }
            }
        }

        return corners;
    }

    public int amountOfFreeCorners (int color) {
        return getEligibleCorners(color).size();
    }


    public Board deepCopy () {
        return saver.deepCopy(this);
    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public PieceManager getPieceManager() {
        return pieceManager;
    }

    public List<Move> getFirstNFittingMoves (int n, int color) {
        List<PieceID> pieceIDs = getPiecesNotOnBoard(color);

        pieceIDs.sort((pieceID, t1) -> {
            int result = t1.getAmountOfSquares() - pieceID.getAmountOfSquares();

            if (result > 0) {
                return 1;
            } else if (result < 0) {
                return -1;
            } else {
                int corners = t1.getAmountOfCorners() - pieceID.getAmountOfCorners();
                return Integer.compare(corners, 0);
            }
        });

        List<Move> moves = new ArrayList<>();
        int endIndex = 0;
        while (moves.size() < n) {
            PieceID current;
            try {
                current = pieceIDs.get(endIndex);

            } catch (IndexOutOfBoundsException e) {
                break;
            }
            moves.addAll(getAllFittingMoves(color, Collections.singletonList(current)));
            endIndex++;
        }
        return moves;
//        if (purge) {
//
//
//            if (heavyPurge) {
//                int max = pieceIDs.stream().mapToInt(PieceID::getAmountOfSquares).max().getAsInt();
//                pieceIDs = pieceIDs.stream().filter((pieceID -> pieceID.getAmountOfSquares() == max)).collect(Collectors.toList());
//                try {
//                    pieceIDs = pieceIDs.subList(start, n);
//                } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {}
//            } else {
//                try {
//                    pieceIDs = pieceIDs.subList(start, n);
//    //                System.out.println(pieceIDs);
//                } catch (IllegalArgumentException | IndexOutOfBoundsException ignored) {}
//            }
//
//
//





//        List<Move> moves = getAllFittingMoves(color, pieceIDs);
//        if (moves.size() == 0 && heavyPurge) {
//            System.out.println("No fitting moves found with ultra strict!");
//            return getFirstNFittingMoves(start,n, color, true, false);
//        } else if (moves.size() == 0 && purge) {
//            System.out.println("No fitting moves found with strict!");
//            return getFirstNFittingMoves((int) (1.5 * n),(int) 2.5 * n, color, true, false);
//        }
//        else {
//            return moves;
//        }

    }


}

