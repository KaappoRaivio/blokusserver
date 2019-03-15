import blokus.Board;

import java.io.Serializable;

public class GameValues implements Serializable {
    private final Board board;
    private int turn;
    private int moveCount;

    public Board getBoard() {
        return board;
    }

    public int getTurn() {
        return turn;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public GameValues(Board board, int turn, int moveCount) {
        this.board = board;
        this.turn = turn;
        this.moveCount = moveCount;
    }

}
