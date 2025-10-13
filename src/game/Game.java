package game;

import utils.Color;
import board.Board;
import position.Position;
import pieces.Piece;

import java.util.Scanner;

/**
 * The {@code Game} class is the central controller for the chess application.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Initialize the board and player objects</li>
 *   <li>Manage turn order and player actions</li>
 *   <li>Parse and validate user input</li>
 *   <li>Execute moves and update the game state</li>
 *   <li>Detect check and checkmate conditions</li>
 * </ul>
 *
 * <p>This class does not enforce advanced rules like castling, promotion, or en passant.
 * Those can be added in future phases.
 */
public class Game {

    /** The game board instance used for all gameplay logic. */
    private Board board;

    /** Player object representing the white side. */
    private Player whitePlayer;

    /** Player object representing the black side. */
    private Player blackPlayer;

    /** Tracks whose turn it currently is. */
    private Player currentPlayer;

    /** Scanner for reading player input from the console. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts a new chess game:
     * <ul>
     *   <li>Initializes players</li>
     *   <li>Sets up the board</li>
     *   <li>Displays the starting position</li>
     * </ul>
     */
    public void start() {
        whitePlayer = new Player("Alice", Color.WHITE);
        blackPlayer = new Player("Bob", Color.BLACK);

        // Create board and associate players so piece lists stay in sync
        board = new Board(whitePlayer, blackPlayer);
        currentPlayer = whitePlayer;

        System.out.println("Initial Board:");
        board.display();

        printPlayerPieces(); // 🧪 optional debug output at start
    }

    /**
     * Main game loop.
     * <p>
     * Runs until a checkmate occurs or a player enters "quit".
     * Each iteration:
     * <ul>
     *   <li>Prompts the current player for a move</li>
     *   <li>Parses and validates input</li>
     *   <li>Moves the piece if legal</li>
     *   <li>Checks if opponent is in check or checkmate</li>
     *   <li>Switches turns</li>
     * </ul>
     */
    public void play() {
        System.out.println("Game loop starting...");

        while (true) {
            System.out.println("\nTurn: " + currentPlayer);
            System.out.print("Enter your move (e.g., E2 E4) or 'quit' to exit: ");

            // Read player input
            String fromInput = scanner.next();
            if (fromInput.equalsIgnoreCase("quit")) break;

            String toInput = scanner.next();
            if (toInput.equalsIgnoreCase("quit")) break;

            try {
                // Convert chess notation to board coordinates
                Position from = parsePosition(fromInput);
                Position to = parsePosition(toInput);

                // Check if a piece exists at the source square
                Piece piece = board.getPiece(from);
                if (piece == null) {
                    System.out.println("⚠️ No piece at that square.");
                    continue;
                }

                // Prevent players from moving opponent's pieces
                if (piece.getColor() != currentPlayer.getColor()) {
                    System.out.println("⚠️ You can only move your own pieces.");
                    continue;
                }


                // Execute the move
                board.movePiece(from, to);
                board.display();

                // Determine opponent for check and checkmate detection
                Player opponent = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;

                // Check if opponent is in check or checkmate after the move
                if (board.isCheck(opponent.getColor())) {
                    System.out.println("⚠️ " + opponent.getName() + " is in CHECK!");
                    if (board.isCheckmate(opponent.getColor())) {
                        System.out.println("♔ Checkmate! " + opponent.getName() + " loses.");
                        break;
                    }
                }

                // Switch turns
                switchTurn();

            } catch (IllegalArgumentException e) {
                System.out.println("⚠️ Invalid move: " + e.getMessage());
            }
        }

        end();
    }

    /**
     * Converts standard chess notation (e.g., "E2") into an internal {@link Position}.
     * <p>Example:
     * <pre>
     *   "E2" → row 6, col 4
     * </pre>
     *
     * @param input the chess coordinate string
     * @return a {@code Position} object representing that square
     * @throws IllegalArgumentException if the input is invalid or out of bounds
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
     * Switches the current player between white and black.
     */
    private void switchTurn() {
        currentPlayer = (currentPlayer == whitePlayer) ? blackPlayer : whitePlayer;
    }

    /**
     * Ends the game by printing a message.
     * <p>
     * Future versions may add saving game state, score tracking, or statistics here.
     */
    public void end() {
        System.out.println("\n🏁 Game Over!");
    }

    /**
     * 🧪 Debug utility method:
     * Prints all remaining pieces for both players along with their counts.
     */
    private void printPlayerPieces() {
        System.out.println("\nWhite Pieces (" + whitePlayer.getAvailablePieces().size() + "):");
        whitePlayer.getAvailablePieces().forEach(
                p -> System.out.print(p.getClass().getSimpleName() + " ")
        );
        System.out.println();

        System.out.println("Black Pieces (" + blackPlayer.getAvailablePieces().size() + "):");
        blackPlayer.getAvailablePieces().forEach(
                p -> System.out.print(p.getClass().getSimpleName() + " ")
        );
        System.out.println("\n");
    }
}
