package board;

import pieces.Piece;

/**
 * The {@code Board} class represents the chess board.
 * The board is an 8x8 grid of squares, each of which may contain a Piece.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Initialize the board to its starting state.</li>
 *   <li>Allow retrieval of pieces at given positions.</li>
 *   <li>Move pieces from one position to another.</li>
 *   <li>Display the board as text in the console.</li>
 * </ul>
 */
public class Board {
    /** 2D array representing the 8x8 board. Each square may contain a Piece or null. */
    private Piece[][] squares = new Piece[8][8];

    /**
     * Constructs a new chess board and initializes it.
     * For Phase 1, this may be partially or fully populated.
     */
    public Board() {
        initializeBoard();
    }

    /**
     * Initializes the board with the standard starting positions for chess pieces.
     * For Phase 1, this can be adjusted or simplified if needed.
     */
    private void initializeBoard() {
        // TODO: Replace this with actual starting pieces if needed.
        // Currently, leaves board empty.
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }
    }

    /**
     * Returns the piece at the given position, or null if the square is empty.
     *
     * @param pos the board position
     * @return the Piece at the position, or null
     */
    public Piece getPiece(Position pos) {
        return squares[pos.getRow()][pos.getCol()];
    }

    /**
     * Places a piece on the board at the given position.
     * (useful for testing or custom setups)
     *
     * @param piece the piece to place
     * @param pos   the position to place the piece at
     */
    public void setPiece(Piece piece, Position pos) {
        squares[pos.getRow()][pos.getCol()] = piece;
        if (piece != null) {
            piece.setPosition(pos);
        }
    }

    /**
     * Moves a piece from one square to another.
     * Overwrites any piece at the target square (capture).
     * Clears the source square after the move.
     * If the source square is empty, prints an error and does nothing.
     *
     * @param from the source position
     * @param to   the target position
     */
    public void movePiece(Position from, Position to) {
        Piece moving = squares[from.getRow()][from.getCol()];

        // ✅ Check if source square is empty
        if (moving == null) {
            System.out.println("No piece at source square.");
            return;
        }

        // ✅ Move the piece
        squares[to.getRow()][to.getCol()] = moving;
        moving.setPosition(to);

        // ✅ Clear the source square
        squares[from.getRow()][from.getCol()] = null;
    }

    /**
     * Displays the current board state in the console.
     * Empty squares are shown as "##". Pieces are shown using abbreviations
     * (e.g., "wP" for white pawn, "bR" for black rook).
     */
    public void display() {
        System.out.println("   A  B  C  D  E  F  G  H");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " "); // rank labels on the left

            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece == null) {
                    System.out.print("## ");
                } else {
                    // Abbreviation: first letter of color + piece type initial
                    String abbrev = piece.getClass().getSimpleName().substring(0, 1);
                    if (abbrev.equals("K") && piece.getClass().getSimpleName().equals("Knight")) {
                        abbrev = "N"; // Knights are shown as N
                    }
                    System.out.print(piece.getColor().charAt(0) + abbrev + " ");
                }
            }

            System.out.println(8 - row);
        }
        System.out.println("   A  B  C  D  E  F  G  H");
    }
}
