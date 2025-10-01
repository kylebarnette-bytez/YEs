package utils;

import board.Board;

/**
 * Entry point for testing the chess program.
 */
public class Main {
    public static void main(String[] args) {
        // Create a new Board
        Board board = new Board();

        // Display the starting board
        System.out.println("Initial Board:");
        board.display();

        // (Optional) Later: you can test moves here
        // Example placeholder (when Position + pieces are ready):
        // board.movePiece(new Position(6, 4), new Position(4, 4));
        // board.display();
    }
}
