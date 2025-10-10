package game;

import utils.Color;
import board.Board;
import position.Position;
import pieces.Piece;

import java.util.Scanner;

/**
 * The Game class manages the flow of a chess game.
 * Responsibilities:
 * - Initialize board and players
 * - Manage turn order
 * - Handle user input
 * - Perform move validation
 * - (Future) Check/checkmate handling
 */
public class Game {

    /** Game board */
    private Board board;

    /** White and Black players */
    private Player whitePlayer;
    private Player blackPlayer;

    /** Whose turn it currently is */
    private Player currentPlayer;

    /** Input scanner */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts a new game by creating players and initializing the board.
     */
    public void start() {
        whitePlayer = new Player("Alice", Color.WHITE);
        blackPlayer = new Player("Bob", Color.BLACK);

        // Pass players to board so it can populate their pieces
        board = new Board(whitePlayer, blackPlayer);
        currentPlayer = whitePlayer;

        System.out.println("Initial Board:");
        board.display();

        printPlayerPieces(); // 🧪 Optional debug output
    }

    /**
     * Main game loop. Runs until user types "quit".
     */
    public void play() {
        System.out.println("Game loop starting...");

        while (true) {
            System.out.println("\nTurn: " + currentPlayer);
            System.out.print("Enter your move (e.g., E2 E4) or 'quit' to exit: ");

            String fromInput = scanner.next();
            if (fromInput.equalsIgnoreCase("quit")) break;

            String toInput = scanner.next();
            if (toInput.equalsIgnoreCase("quit")) break;

            try {
                Position from = parsePosition(fromInput);
                Position to = parsePosition(toInput);

                Piece piece = board.getPiece(from);
                if (piece == null) {
                    System.out.println("⚠️ No piece at that square.");
                    continue;
                }

                // ✅ Enforce correct turn color
                if (piece.getColor() != currentPlayer.getColor()) {
                    System.out.println("⚠️ You can only move your own pieces.");
                    continue;
                }

                board.movePiece(from, to);
                board.display();

                printPlayerPieces(); // 🧪 optional debug after each move

                switchTurn();
            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Invalid move: " + e.getMessage());
            }
        }

        end();
    }

    /**
     * Converts chess notation like "E2" into a Position object.
     * @param input user input in chess coordinates
     * @return Position with 0–7 row/col indices
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
     * Switches the turn to the other player.
     */
    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    /**
     * Ends the game.
     */
    public void end() {
        System.out.println("\n🏁 Game Over!");
    }

    /**
     * 🧪 Debug method: Prints current remaining pieces for both players.
     */
    private void printPlayerPieces() {
        System.out.println("\nWhite Pieces (" + whitePlayer.getAvailablePieces().size() + "):");
        whitePlayer.getAvailablePieces().forEach(p -> System.out.print(p.getClass().getSimpleName() + " "));
        System.out.println();

        System.out.println("Black Pieces (" + blackPlayer.getAvailablePieces().size() + "):");
        blackPlayer.getAvailablePieces().forEach(p -> System.out.print(p.getClass().getSimpleName() + " "));
        System.out.println("\n");
    }
}
