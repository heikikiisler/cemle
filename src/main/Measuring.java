package main;

import board.Board;
import moves.Moves;
import side.Side;

import java.time.Duration;
import java.time.Instant;

public class Measuring {

    public static void main(String[] args) {
        Moves moves = new Moves(new Board());

        for (int j = 0; j < 3; j++) {
            Instant start = Instant.now();
            for (int i = 0; i < 1000000; i++) {
                //moves.getKnightMoves2(Side.WHITE, 3, 3);
            }
            Instant end = Instant.now();
            Instant start3 = Instant.now();
            for (int i = 0; i < 1000000; i++) {
                moves.getKnightMoves(Side.WHITE, 3, 3);
            }
            Instant end3 = Instant.now();
            System.out.println(Duration.between(start, end));
            System.out.println(Duration.between(start3, end3));
            System.out.println();
        }
    }

}
