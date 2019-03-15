package blokus;

import java.util.List;

public interface PieceManager {
    List<Piece> getCachedPieces(int color);
    List<PieceID> getPiecesOnBoard(int color);
    List<PieceID> getPiecesNotOnBoard(int color);
    Piece getCachedPiece(PieceID pieceID, int color);
    void placeOnBoard(PieceID pieceID, int color);
    boolean isOnBoard(PieceID pieceID, int color);
    boolean isColorOnBoard(int color);
    int getAmountOfPlayers();
    void undo(int depth);

}
