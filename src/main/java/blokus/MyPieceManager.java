package blokus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class MyPieceManager implements PieceManager, Serializable {
    private List< List<Piece> > cachedPieces = new Vector<>();
    private List< List<PieceID> > piecesOnBoard = new Vector<>();
    private List< List<PieceID> > piecesNotOnBoard = new Vector<>();

    private Stack<Integer> colorHistory = new Stack<>();

    private int amountOfPlayers;

    public MyPieceManager (int amountOfPlayers) {
        for (int i = 0; i < amountOfPlayers; i++) {
            piecesNotOnBoard.add(new ArrayList<>());
            piecesOnBoard.add(new ArrayList<>());
            cachedPieces.add(new ArrayList<>());

            List<PieceID> pieceIDs = Piece.getAllPieces(i);
            for (PieceID pieceID : pieceIDs) {
                try {
                    cachedPieces.get(i).add(pieceID.getOrdinal(), (new Piece(pieceID, i)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            piecesNotOnBoard.get(i).addAll(pieceIDs);
        }

        this.amountOfPlayers = amountOfPlayers;
    }

    @Override
    public List<Piece> getCachedPieces(int color) {
        return cachedPieces.get(color);
    }

    @Override
    public List<PieceID> getPiecesOnBoard(int color) {
        return piecesOnBoard.get(color);
    }

    @Override
    public List<PieceID> getPiecesNotOnBoard(int color) {
        return piecesNotOnBoard.get(color);
    }

    @Override
    public Piece getCachedPiece(PieceID pieceID, int color) {
        try {
            return cachedPieces.get(color).get(pieceID.getOrdinal());
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Invalid color " + color + " or pieceID " + pieceID + "!");
        }
    }

    @Override
    public void placeOnBoard(PieceID pieceID, int color) {
        if (isOnBoard(pieceID, color)) {
            throw new RuntimeException("blokus.Piece " + pieceID + " is already on board!");
        }

        colorHistory.push(color);
        piecesNotOnBoard.get(color).remove(pieceID);
        piecesOnBoard.get(color).add(pieceID);
    }


    private void takeOffBoard (PieceID pieceID, int color) {
        if (!isOnBoard(pieceID, color)) {
            throw new RuntimeException("blokus.Piece " + pieceID + " is not on board!");
        }

        piecesOnBoard.get(color).remove(pieceID);
        piecesNotOnBoard.get(color).add(pieceID);
    }

    @Override
    public boolean isOnBoard (PieceID pieceID, int color) {
        return piecesOnBoard.get(color).contains(pieceID);
    }

    @Override
    public boolean isColorOnBoard(int color) {
        return !piecesOnBoard.get(color).isEmpty();
    }

    @Override
    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    @Override
    public void undo(int depth) {
        if (depth > 0) {
            int colorToUndo = colorHistory.pop();
            PieceID pieceID = piecesOnBoard.get(colorToUndo).get(piecesOnBoard.get(colorToUndo).size() - 1);
            takeOffBoard(pieceID, colorToUndo);
            undo(depth - 1);
        }
    }


}
