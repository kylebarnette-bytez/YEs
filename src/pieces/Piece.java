package pieces;

import board.Board;
import position.Position;
import utils.Color;

import java.util.List;

/**
 * Abstract base class for all chess pieces.
 * Each piece has a color and a position on the board.
 * Subclasses must implement the movement logic in {@link #possibleMoves(Board)}.
 */
public abstract class Piece {

	/** The color of the piece (WHITE or BLACK). */
	protected Color color;

	/** The current position of the piece on the board. */
	protected Position position;

	/**
	 * Constructs a new piece with the given color and position.
	 *
	 * @param color the color of the piece
	 * @param position the initial position
	 */
	public Piece(Color color, Position position) {
		this.color = color;
		this.position = position;
	}

	public Color getColor() {
		return color;
	}

	public Position getPosition() {
		return position;
	}

	/**
	 * Moves the piece to a new position.
	 *
	 * @param newPosition the new board position
	 */
	public void move(Position newPosition) {
		this.position = newPosition;
	}

	/**
	 * Returns a list of legal moves for this piece from its current position.
	 * This method must be implemented by all concrete piece subclasses.
	 *
	 * @param board the chess board
	 * @return list of valid target positions
	 */
	public abstract List<Position> possibleMoves(Board board);

	/**
	 * Overloaded version of {@link #possibleMoves(Board)} that allows
	 * the caller to ignore king-safety checks (to avoid recursion loops).
	 *
	 * By default, this just calls the standard method.
	 * Only the King overrides this to skip movePutsPlayerInCheck().
	 *
	 * @param board the chess board
	 * @param ignoreKingSafety if true, skip any king-safety checks
	 * @return list of target positions
	 */
	public List<Position> possibleMoves(Board board, boolean ignoreKingSafety) {
		return possibleMoves(board);
	}

	/**
	 * Utility method to check if a given row/col is inside the 8x8 chess board.
	 *
	 * @param row the row index
	 * @param col the column index
	 * @return true if within 0–7 for both row and col
	 */
	protected boolean isInBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}

	/**
	 * Returns a short text representation of the piece.
	 * Example: "wK" for white king, "bQ" for black queen.
	 */
	@Override
	public String toString() {
		String symbol = this.getClass().getSimpleName().substring(0, 1);
		return (color == Color.WHITE ? "w" : "b") + symbol;
	}
}
