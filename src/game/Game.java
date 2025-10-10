package game;

import board.Board;
import board.Position;

import java.util.Scanner;

/**
 * The Game class manages the flow of a chess game.
 * It is responsible for:
 * - Initializing the board
 * - Managing player turns
 * - Running the game loop for user input
 *
 * Phase 1: Basic game skeleton with simple move input and display.
 * Future phases: Move validation, check/checkmate detection, timers, etc.
 */
public class Game {
    /** The chess board for the game. */
    private Board board;

    /** White and Black players. */
    private Player whitePlayer;
    private Player blackPlayer;

    /** Current player taking a turn. */
    private Player currentPlayer;

    /** Scanner for user input. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts a new game by creating a fresh board and setting
     * the first player to white. Displays the initial board.
     */
    public void start() {
        board = new Board();
        whitePlayer = new Player("Alice", "white");
        blackPlayer = new Player("Bob", "black");
        currentPlayer = whitePlayer;

        System.out.println("Initial Board:");
        board.display();
    }

    /**
     * Runs the main game loop until the user quits.
     * Currently allows text-based input for moves (e.g. "E2 E4").
     */
    public void play() {
        System.out.println("Game loop starting...");

        while (true) {
            System.out.println("Turn: " + currentPlayer);
            System.out.print("Enter your move (e.g., E2 E4) or 'quit' to exit: ");

            String fromInput = scanner.next();
            if (fromInput.equalsIgnoreCase("quit")) break;

            String toInput = scanner.next();
            if (toInput.equalsIgnoreCase("quit")) break;

            try {
                Position from = parsePosition(fromInput);
                Position to = parsePosition(toInput);
				
				Piece piece = board.getPiece(from);
				if (!piece.getColor().equalsIgnoreCase(currentPlayer.getColor())) {
					System.out.println("⚠️ You can only move your own pieces.");
					continue;
				}

                board.movePiece(from, to);
                board.display();
                switchTurn();
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Invalid move: " + e.getMessage());
            }
        }

        end();
    }

    /**
     * Converts chess notation like "E2" into a Position object.
     *
     * @param input The chess coordinate as a String
     * @return Position representing the row and column
     */
    private Position parsePosition(String input) {
        input = input.toUpperCase().trim();
        if (input.length() != 2)
            throw new IllegalArgumentException("Invalid position format: " + input);

        char file = input.charAt(0);
        int col = file - 'A';

        int rank = Character.getNumericValue(input.charAt(1));
        int row = 8 - rank;

        if (col < 0 || col > 7 || row < 0 || row > 7)
            throw new IllegalArgumentException("Out of board bounds: " + input);

        return new Position(row, col);
    }

    /**
     * Switches the current player's turn.
     */
    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    /**
     * Ends the game.
     */
    public void end() {
        System.out.println("Game Over!");
    }
}
