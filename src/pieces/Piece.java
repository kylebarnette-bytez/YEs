package pieces;

import utils.Color;
import position.Position;
import board.Board;

import java.util.List;

/**
 * The {@code Piece} class is the abstract base class for all chess pieces
 * (e.g., {@link Pawn}, {@link Rook}, {@link Knight}, {@link Bishop}, {@link Queen}, {@link King}).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Store and manage the color and position of the piece.</li>
 *   <li>Provide a common interface for move generation.</li>
 *   <li>Offer utility methods shared across different piece types.</li>
 * </ul>
 *
 * <p>Each concrete piece subclass must implement its own movement logic
 * by overriding {@link #possibleMoves(Board)}.
 */
public abstract class Piece {

	/** The color of the piece (WHITE or BLACK). */
	protected Color color;

	/** The current position of the piece on the board. */
	protected Position position;

	/**
	 * Constructs a new {@code Piece} with the specified color and starting position.
	 *
	 * @param color    the piece color ({@link Color#WHITE} or {@link Color#BLACK})
	 * @param position the initial position of the piece on the board
	 */
	public Piece(Color color, Position position) {
		this.color = color;
		this.position = position;
	}

	/**
	 * Returns the color of this piece.
	 *
	 * @return the piece's color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Returns the current position of this piece.
	 *
	 * @return the piece's position on the board
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Updates the position of the piece to a new location on the board.
	 * <p>This is typically called after a successful move.
	 *
	 * @param newPosition the new position after a move
	 */
	public void move(Position newPosition) {
		this.position = newPosition;
	}

	/**
	 * Returns a list of valid moves that this piece can make
	 * based on its movement rules and the current board state.
	 * <p>This method must be implemented in each concrete subclass.
	 *
	 * @param board the current {@link Board} state
	 * @return list of valid target positions for this piece
	 */
	public abstract List<Position> possibleMoves(Board board);

	/**
	 * Checks if a given square is within the board boundaries.
	 * <p>Useful for preventing array out-of-bounds errors during move generation.
	 *
	 * @param row row index (0–7)
	 * @param col column index (0–7)
	 * @return true if the square is on the board, false otherwise
	 */
	protected boolean isInBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}

	/**
	 * Returns a short string abbreviation for displaying the piece on the console.
	 * <p>Format:
	 * <ul>
	 *   <li><code>wP</code> = white pawn</li>
	 *   <li><code>bK</code> = black king</li>
	 *   <li><code>wN</code> = white knight</li>
	 * </ul>
	 *
	 * @return two-character string representing the piece
	 */
	@Override
	public String toString() {
		// By default, use the first letter of the class name
		String abbrev = this.getClass().getSimpleName().substring(0, 1).toUpperCase();

		// Knight is represented as "N" (not "K")
		if (this instanceof Knight) abbrev = "N";
		// Pawn is represented as "P"
		if (this instanceof Pawn) abbrev = "P";

		// Prefix with color (w or b)
		return (color == Color.WHITE ? "w" : "b") + abbrev;
	}
}
