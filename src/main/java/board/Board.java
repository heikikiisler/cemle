package board;

import com.esotericsoftware.kryo.Kryo;
import moves.NormalMove;
import moves.SetMove;

import java.util.ArrayList;


public class Board {

    private char[] pieces;

    private static final Kryo KRYO = new Kryo();

    private static final char[] WHITE_PIECES = new char[]{'P', 'R', 'N', 'B', 'Q', 'K'};
    private static final char[] BLACK_PIECES = new char[]{'p', 'r', 'n', 'b', 'q', 'k'};

    private static final char[] WHITE_PROMOTION_PIECES = new char[]{'R', 'N', 'B', 'Q'};
    private static final char[] BLACK_PROMOTION_PIECES = new char[]{'r', 'n', 'b', 'q'};

    private boolean whiteKingSideCastling = true;
    private boolean whiteQueenSideCastling = true;
    private boolean blackKingSideCastling = true;
    private boolean blackQueenSideCastling = true;

    private int sideToMove;

    private int totalPlies;
    private int currentInactivePlies;

    private Square enPassantSquare;

    private ArrayList<Square> disallowedCheckSquares;

    public Board() {
        setUpBoard();
    }

    private void setUpBoard() {
        disallowedCheckSquares = new ArrayList<>();
        sideToMove = 1;
        totalPlies = 0;
        currentInactivePlies = 0;
        pieces = new char[64];
        for (int i = 0; i < 64; i++) {
            pieces[i] = '.';
        }
        for (int i = 0; i < 8; i++) {
            setPiece('P', 1, i);
            setPiece('p', 6, i);
        }
        setPiece('R', 0, 0);
        setPiece('R', 0, 7);
        setPiece('N', 0, 6);
        setPiece('N', 0, 1);
        setPiece('B', 0, 2);
        setPiece('B', 0, 5);
        setPiece('Q', 0, 3);
        setPiece('K', 0, 4);

        setPiece('r', 7, 0);
        setPiece('r', 7, 7);
        setPiece('n', 7, 6);
        setPiece('n', 7, 1);
        setPiece('b', 7, 2);
        setPiece('b', 7, 5);
        setPiece('q', 7, 3);
        setPiece('k', 7, 4);
    }

    public void move(NormalMove move) {
        char piece = getPiece(move.getStartSquare());
        refreshAlways();
        // Castling rights
//        if (piece == 'K') {
//            whiteQueenSideCastling = false;
//            whiteKingSideCastling = false;
//        } else if (piece == 'k') {
//            blackQueenSideCastling = false;
//            blackKingSideCastling = false;
//        } else if (move.getStartRow() == 0 && move.getStartCol() == 0) {
//            whiteQueenSideCastling = false;
//        } else if (move.getStartRow() == 0 && move.getStartCol() == 7) {
//            whiteKingSideCastling = false;
//        } else if (move.getStartRow() == 7 && move.getStartCol() == 0) {
//            blackQueenSideCastling = false;
//        } else if (move.getStartRow() == 7 && move.getStartCol() == 7) {
//            blackKingSideCastling = false;
//        }
        // Finally move piece
        setPiece(piece, move.getEndSquare());
        setPiece('.', move.getStartSquare());
    }

    public void specialMove(ArrayList<SetMove> setMoves) {
        refreshAlways();
        for (SetMove setMove: setMoves) {
            setMove(setMove);
        }
    }

    public void castlingMove(boolean kingSide) {
        // TODO: 27.09.2017 Implement
    }

    public void promotionMove(Square startSquare, Square endSquare, char piece) {
        // TODO: 27.09.2017 Implement
    }

    public void enPassantMove(Square startSquare) {
        // TODO: 27.09.2017 Implement
    }

    private void resetInactivePlies() {
        currentInactivePlies = 0;
    }

    private void refreshAlways() {
        totalPlies++;
        currentInactivePlies++;
        enPassantSquare = null;
        sideToMove = -sideToMove;
    }

    public void printBoard() {
        System.out.println();
        System.out.println(String.format("Move #%d; Ply #%d", (totalPlies + 1) / 2, totalPlies));
        System.out.println();
        for (int i = 7; i >= 0; i--) {
            System.out.print(i + "   ");
            for (int j = 0; j < 8; j++) {
                System.out.print(getPiece(i, j) + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("    0 1 2 3 4 5 6 7");
        System.out.println();
    }

    public char getPiece(int row, int col) {
        return pieces[row * 8 + col];
    }

    public char getPiece(Square startSquare) {
        return pieces[startSquare.getIndex()];
    }

    private void setPiece(char piece, int row, int col) {
        pieces[row * 8 + col] = piece;
    }

    private void setPiece(char piece, Square square) {
        pieces[square.getIndex()] = piece;
    }

    private void setMove(SetMove setMove) {
        pieces[setMove.getSquare().getIndex()] = setMove.getPiece();
    }

    public static int getPieceSide(char piece) {
        if (piece == '.') {
            return 0;
        } else {
            for (char c: WHITE_PIECES) {
                if (piece == c) {
                    return 1;
                }
            }
        }
        return -1;
    }

    public int getPieceSide(Square square) {
        return getPieceSide(getPiece(square));
    }

    public ArrayList<Square> getDisallowedCheckSquares() {
        return disallowedCheckSquares;
    }

    public Square getKingPosition(int side) {
        char king = (side == 1) ? 'K' : 'k';
        for (int i = 0; i < 64; i++) {
            if (pieces[i] == king) {
                return new Square(i);
            }
        }
        throw new RuntimeException(String.format("King not found, side: %s", side));
    }

    public boolean isSquareEmpty(Square square) {
        return getPiece(square) == '.';
    }

    public static boolean pieceIsSide(char piece, int side) {
        return getPieceSide(piece) == side;
    }

    public Square getEnPassantSquare() {
        return enPassantSquare;
    }

    public boolean getWhiteKingSideCastling() {
        return whiteKingSideCastling;
    }

    public boolean getWhiteQueenSideCastling() {
        return whiteQueenSideCastling;
    }

    public boolean getBlackKingSideCastling() {
        return blackKingSideCastling;
    }

    public boolean getBlackQueenSideCastling() {
        return blackQueenSideCastling;
    }

    public static char[] getWhitePromotionPieces() {
        return WHITE_PROMOTION_PIECES;
    }

    public static char[] getBlackPromotionPieces() {
        return BLACK_PROMOTION_PIECES;
    }

    public char[] getPieces() {
        return pieces;
    }

    public int getSideToMove() {
        return sideToMove;
    }

    public Board getCopy() {
        return KRYO.copy(this);
    }

    public boolean isCastlingAllowed(boolean kingSide) {
        if (sideToMove == 1) {
            if (kingSide) {
                return whiteKingSideCastling;
            } else {
                return whiteQueenSideCastling;
            }
        } else {
            if (kingSide) {
                return blackKingSideCastling;
            } else {
                return blackQueenSideCastling;
            }
        }
    }
}
