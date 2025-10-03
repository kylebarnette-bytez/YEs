package board;

import pieces.Piece;

/**
 * The {@code Board} class represents the chess board.
 * The board is an 8x8 grid of squares, each of which may contain a Piece.
 *
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Initialize the board to its starting state (Phase 1 may leave empty or stubbed).</li>
 *   <li>Allow retrieval of pieces at given positions.</li>
 *   <li>Move pieces from one position to another.</li>
 *   <li>Display the board as text in the console.</li>
 * </ul>
 *
 */
public class Board {
    /** 2D array representing the 8x8 board. Each square may contain a Piece or null. */
    private Piece[][] squares = new Piece[8][8];

    /**
     * Constructs a new chess board and initializes it.
     * For Phase 1, pieces may not yet be fully set up.
     */
    public Board() {
        // TODO: set up initial board (to be filled in when Piece subclasses are ready)
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
     * Moves a piece from one square to another.
     * Overwrites any piece at the target square (capture).
     * Clears the source square after the move.
     *
     * @param from the source position
     * @param to   the target position
     */
    public void movePiece(Position from, Position to) {
        // TODO: implement full move logic once Piece subclasses exist
    }

    /**
     * Displays the current board state in the console.
     * Empty squares are shown as "##". Pieces are shown using abbreviations
     * (e.g., "wP" for white pawn, "bR" for black rook).
     */
    public void display() {
        // TODO: implement textual board rendering
    }
}
