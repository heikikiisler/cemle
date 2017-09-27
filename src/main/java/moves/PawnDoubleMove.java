package moves;

import board.Board;
import board.Square;

public class PawnDoubleMove implements Move {

    private Square startSquare;
    private Square endSquare;

    public PawnDoubleMove(Square startSquare, Square endSquare) {
        this.startSquare = startSquare;
        this.endSquare = endSquare;
    }

    @Override
    public void move(Board board) {
        // TODO: 27.09.2017 Implement double move en passant updating
    }
}
