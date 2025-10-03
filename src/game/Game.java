package game;

import board.Board;
import board.Position;
import java.util.Scanner;

/**
 * The Game class manages the flow of a chess game.
 * Uses Player objects instead of raw strings for turns.
 */
public class Game {
    /** The chess board for the game. */
    private Board board;

    /** The two players. */
    private Player whitePlayer;
    private Player blackPlayer;

    /** The player whose turn it currently is. */
    private Player currentPlayer;

    /**
     * Starts a new game by creating a board and two players.
     */
    public void start() {
        board = new Board();
        whitePlayer = new Player("Alice", "white");   // placeholder names
        blackPlayer = new Player("Bob", "black");

        currentPlayer = whitePlayer; // white always starts

        System.out.println("Initial Board:");
        board.display();
    }

    /**
     * Runs the game loop (Phase 1 demo).
     */
    public void play() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Turn: " + currentPlayer);
        System.out.println("Enter your move (e.g., E2 E4): ");
        String fromInput = scanner.next();
        String toInput = scanner.next();

        Position from = parsePosition(fromInput);
        Position to = parsePosition(toInput);

        board.movePiece(from, to);
        board.display();

        switchTurn();
        end();
    }

    /**
     * Switches the turn to the other player.
     */
    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    private Position parsePosition(String input) {
        input = input.toUpperCase().trim();

        if (input.length() != 2) {
            throw new IllegalArgumentException("Invalid position format: " + input);
        }

        char file = input.charAt(0);
        int col = file - 'A';

        int rank = Character.getNumericValue(input.charAt(1));
        int row = 8 - rank;

        return new Position(row, col);
    }

    /**
     * Ends the game for now (Phase 1 stub).
     */
    public void end() {
        System.out.println("Game Over!");
    }
}
