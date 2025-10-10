package pieces;

import utils.Color;
import position.Position;
import board.Board;

import java.util.List;

/**
 * Abstract base class for all chess pieces.
 * Defines shared attributes and basic behaviors.
 */
public abstract class Piece {
	/** The color of the piece (WHITE or BLACK). */
	protected Color color;

	/** The current board position of the piece. */
	protected Position position;

	/**
	 * Creates a new piece with a given color and position.
	 *
	 * @param color    the piece color
	 * @param position the initial board position
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
	 * Updates the position of the piece on the board.
	 *
	 * @param newPosition the new position after a move
	 */
	public void move(Position newPosition) {
		this.position = newPosition;
	}

	/**
	 * Returns a list of valid moves for this piece.
	 * Must be implemented in each subclass.
	 *
	 * @param board the current board state
	 * @return list of valid positions
	 */
	public abstract List<Position> possibleMoves(Board board);

	/**
	 * Utility method to check if a square is on the board.
	 */
	protected boolean isInBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}

	/**
	 * Returns a string abbreviation for display.
	 * Example: "wK" for white king, "bP" for black pawn.
	 */
	@Override
	public String toString() {
		switch (this.getClass().getSimpleName()) {
			case "King":   return (color == Color.WHITE) ? "♔" : "♚";
			case "Queen":  return (color == Color.WHITE) ? "♕" : "♛";
			case "Rook":   return (color == Color.WHITE) ? "♖" : "♜";
			case "Bishop": return (color == Color.WHITE) ? "♗" : "♝";
			case "Knight": return (color == Color.WHITE) ? "♘" : "♞";
			case "Pawn":   return (color == Color.WHITE) ? "♙" : "♟";
			default: return "??";
		}
	}

}
