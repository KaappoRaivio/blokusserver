package blokus;

import misc.Saver;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class GameHistory implements Serializable {
    private List<Move> moves;
    private Board initialPosition;
    private int initialMoveCounter;

    public GameHistory(Board initialPosition, int initialMoveCounter) {
        this.initialPosition = initialPosition;
        this.initialMoveCounter = initialMoveCounter;
        moves = new Vector<>();
    }

    public void addMove (Move move) {
        moves.add(move);
    }

    public Board renderEndPosition () {
        Board newBoard = initialPosition.deepCopy();

        for (Move move : moves) {
            if (!newBoard.putOnBoard(move)) {
                throw new RuntimeException("Move " + move + "doen't fit on " + newBoard + "!");
            }
        }

        return newBoard;
    }

    public String save (String filename) {
        String path = System.getProperty("user.dir") + "/src/main/resources/games/" + filename + ".ser";

        return new Saver<GameHistory>().save(this, path, false);
    }

    public String save () {
        return save(new Date().toString());
    }

    public static GameHistory fromFile (String path, boolean relative) {
        String absolutePath;

        if (relative) {
            absolutePath = System.getProperty("user.dir") + "/src/main/resources/games/" + path;
        } else {
            absolutePath = path;
        }

        GameHistory board;
        try {
            FileInputStream fileIn = new FileInputStream(absolutePath);

            ObjectInputStream in = new ObjectInputStream(fileIn);
            board = (GameHistory) in.readObject();
            in.close();

            fileIn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return board;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Initial move count: " + initialMoveCounter + "\n");

        for (Move move : moves) {
            stringBuilder.append(move);
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();

    }
}
