package game;

import board.Board;

/**
 * The Game class manages the flow of a chess game.
 * It is responsible for setting up the board, tracking turns,
 * and running the main game loop.
 *
 * Phase 1: Implements game skeleton (board setup, turns, basic loop).
 * Future phases: Add move parsing, validation, and win conditions.
 */
public class Game {
    /** The chess board for the game. */
    private Board board;

    /** The current player's turn ("white" or "black"). */
    private String currentTurn;

    /**
     * Starts a new game by creating a fresh board and setting
     * the first player to "white". Also displays the initial board.
     */
    public void start() {
        board = new Board();
        currentTurn = "white";
        System.out.println("Initial Board:");
        board.display();
    }
/**
 * Runs the main game loop.
 * For Phase 1, this simply alternates turns and shows the board
 * until a fixed number of turns have passed.
 * In future phases, this will handle user input, moves, and end conditions.
 */

public void play() {
    System.out.println("Game loop starting...");

    // Temporary: run for 4 sample turns to simulate flow
    for (int i = 0; i < 4; i++) {
        System.out.println("Turn: " + currentTurn);
        // TODO: Later add: prompt input -> validate -> board.movePiece(from, to)
        board.display();
        switchTurn();
    }

    end();
}

    /**
     * Switches the current player's turn between white and black.
     */
    private void switchTurn() {
        currentTurn = currentTurn.equals("white") ? "black" : "white";
    }

    /**
     * Ends the game. For now, simply prints "Game Over!".
     * Future versions may display the winner or draw result.
     */
    public void end() {
        System.out.println("Game Over!");
    }

}
