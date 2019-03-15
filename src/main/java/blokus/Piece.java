package blokus;


import misc.Pair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class Piece implements java.io.Serializable {
    private static final List<String> paths = Arrays.asList(
            
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece1.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece2.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece3.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece4.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece5.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece6.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece7.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece8.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece9.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece10.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece11.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece12.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece13.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece14.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece15.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece16.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece17.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece18.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece19.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece20.txt",
            "/home/kaappo/git/blokusnew/src/main/resources/pieces/piece21.txt"
    );


    public static final char OPAQUE = '#';
    public static final char TRANSPARENT = '.';
    public static final char CORNER = '$';

    private char[][] mesh;
    private int color;

    private int posX = -1;
    private int posY = -1;
    private boolean onBoard;
    private final PieceID id;
    private final Orientation orientation;
    private final boolean flipped;
    private int amountOfSquares;
    private List<Position> squares = new Vector<>();
    private int dimX;
    private int dimY;

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }


    public void placeOnBoard (int posX, int posY) {
        if (onBoard) {
            throw new RuntimeException("blokus.Piece " + toString() + " is already on board at coordnates " + this.posX + ", " + this.posY + "!");
        }

        onBoard = true;
        this.posX = posX;
        this.posY = posY;
    }

    public PieceID getID () {
        return id;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public char[][] getMesh() {
        return mesh;
    }


    private static boolean isValid (int color) {
        return color > -1;
    }

    private void initializeMesh () {
        mesh = new char[dimY][dimX];

        for (int y = 0; y < mesh.length; y++) {
            for (int x = 0; x < mesh[y].length; x++) {
                mesh[y][x] = TRANSPARENT;
            }
        }
    }

    public List<Position> getSquares() {
        return squares;
    }

    private static List<String> readFile (String path) throws IOException {
            return Files.lines(Paths.get(path), StandardCharsets.UTF_8).collect(Collectors.toList());
    }

    public Piece (PieceID pieceID, int color) {
        if (isValid(color)) {
            this.color = color;
        } else {
            throw new RuntimeException("Invalid color " + color + "!");
        }


        List<String> text;
        try {
            text = readFile(paths.get(pieceID.getOrdinal()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] lines = (String[]) text.toArray();
        dimY = lines.length;
        dimX = lines[0].length();

        initializeMesh();

        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                char current = lines[y].charAt(x);
                switch (current) {
                    case TRANSPARENT:
                        mesh[y][x] = TRANSPARENT;
                        break;
                    case CORNER:
                    case OPAQUE:
                        mesh[y][x] = OPAQUE;
                        break;

                    default:
                        System.out.println("Invalid data in " + pieceID + " at " + x + ", " + y + "!" );
                        mesh[y][x] = TRANSPARENT;
                        break;

                }
            }
        }

        id = pieceID;
        orientation = Orientation.UP;
        flipped = false;
        refreshSquares();

        if (amountOfSquares != pieceID.getAmountOfSquares()) {
            throw new RuntimeException("Wrong amount of sqares in resource. Found " + amountOfSquares + ", expecting " + pieceID.getAmountOfCorners());
        }
    }

    private void refreshSquares () {
        squares = new Vector<>();

        int counter = 0;

        for (int x = 0; x < dimX; x++) {
            for (int y = 0; y < dimY; y++) {
                if (mesh[y][x] == OPAQUE) {
                    counter++;
                    squares.add(new Position(x, y));
                }
            }
        }

        amountOfSquares = counter;
    }

    public int getAmountOfSquares () {
        return amountOfSquares;
    }

    private Piece (char[][] mesh, int color, PieceID pieceID, Orientation orientation, boolean flipped, boolean isOnBoard, int amountOfSquares) {
        if (isValid(color)) {
            this.color = color;
        } else {
            throw new RuntimeException("Invalid color " + color + "!");
        }

        dimY = mesh.length;
        dimX = mesh[0].length;

        initializeMesh();

        this.mesh = mesh;
        refreshSquares();

        id = pieceID;
        this.orientation = orientation;
        this.flipped = flipped;
        onBoard = isOnBoard;
        this.amountOfSquares = amountOfSquares;
    }

    private void moveLeft () {
        for (int y = 0; y < dimY; y++) {
            if (mesh[y][0] == OPAQUE) {
                return;
            }
        }

        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                if (x + 1 >= dimX) {
                    mesh[y][x] = TRANSPARENT;
                } else {
                    mesh[y][x] = mesh[y][x + 1];
                }
            }
        }

        moveLeft();
    }

    private void moveUp () {
        for (int x = 0; x < 5; x++) {
            if (mesh[0][x] == OPAQUE) {
                return;
            }
        }

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (y + 1 >= dimY) {
                    mesh[y][x] = TRANSPARENT;
                } else {
                    mesh[y][x] = mesh[y + 1][x];
                }
            }
        }

        moveUp();
    }

    public Piece rotate (Orientation orientation, boolean flip) {
        char[][] newList;


        switch (orientation) {
            case UP:
                newList = mesh;
                break;
            case DOWN:
                newList = new char[dimY][dimX];

                for (int y = 0; y < dimY; y++) {
                    for (int x = 0; x < dimX; x++) {
                        newList[dimY - y - 1][dimX - x - 1] = mesh[y][x];
                    }
                }
                break;
            case LEFT:
                newList = new char[dimX][dimY];

                for (int y = 0; y < dimY; y++) {
                    for (int x = 0; x < dimX; x++) {
                        newList[dimX - x - 1][y] = mesh[y][x];
                    }
                }
                break;
            case RIGHT:
                newList = new char[dimX][dimY];

                for (int y = 0; y < dimY; y++) {
                    for (int x = 0; x < dimX; x++) {
                        newList[x][dimY - y - 1] = mesh[y][x];
                    }
                }
                break;

            default:
                throw new RuntimeException("Shouln't get here");
        }


        int dimY = newList.length;
        int dimX = newList[0].length;

        char[][] afterFlip = new char[dimY][dimX];


        if (flip) {
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    afterFlip[y][dimX - x - 1] = newList[y][x];
                }
            }
        } else {
            afterFlip = newList;
        }
        Piece piece = new Piece(afterFlip, color, getID(), orientation, flip, isOnBoard(), getAmountOfSquares());
        piece.moveLeft();
        piece.moveUp();

        return piece;

    }

    public List<Piece> getAllOrientations () {
        List<Piece> pieces = new Vector<>();

        for (Pair<Orientation, Boolean> orientationAndFlip : id.getAllOrientations()) {
            pieces.add(rotate(orientationAndFlip.getK(), orientationAndFlip.getV()));
        }

        return pieces;
    }


    public static List<PieceID> getAllPieces (int pieceColor) {
        return Arrays.asList(
                PieceID.PIECE_1,
                PieceID.PIECE_2,
                PieceID.PIECE_3,
                PieceID.PIECE_4,
                PieceID.PIECE_5,
                PieceID.PIECE_6,
                PieceID.PIECE_7,
                PieceID.PIECE_8,
                PieceID.PIECE_9,
                PieceID.PIECE_10,
                PieceID.PIECE_11,
                PieceID.PIECE_12,
                PieceID.PIECE_13,
                PieceID.PIECE_14,
                PieceID.PIECE_15,
                PieceID.PIECE_16,
                PieceID.PIECE_17,
                PieceID.PIECE_18,
                PieceID.PIECE_19,
                PieceID.PIECE_20,
                PieceID.PIECE_21
        );
    }

    public int getColor() {
        return color;
    }

    public String toString () {
        StringBuilder builder = new StringBuilder("Color: " + color + "\n");
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                builder.append(mesh[y][x] == TRANSPARENT ? "." : mesh[y][x]);
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    public String bareToString () {
        StringBuilder builder = new StringBuilder();
        for (int y = 0; y < mesh.length; y++) {
            for (int x = 0; x < mesh[y].length; x++) {
                builder.append(mesh[y][x] == TRANSPARENT ? " " : mesh[y][x]);
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    public boolean isOnBoard() {
        return onBoard;
    }

    public void setOnBoard(boolean set) {
//        this.onBoard = set;
    }

    public static int amountOfUniquePieces () {
        return 21;
    }



    public int getDimY () {
        return dimY;
    }

    public int getDimX () {
        return dimX;
    }
}