package pieces;

import board.Position;
import java.util.List;

/**
 * Abstract base class for all chess pieces.
 */
public abstract class Piece {
	protected String color;        // "white" or "black"
	protected Position position;   // current board position

	public Piece(String color, Position position) {
		this.color = color;
		this.position = position;
	}

	public String getColor() {
		return color;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position newPosition) {
		this.position = newPosition;
	}

	/**
	 * Each subclass implements its own move logic.
	 * @return list of valid positions this piece can move to
	 */
	public abstract List<Position> possibleMoves();

	/**
	 * Utility for subclasses to check if a move is on the board.
	 */
	protected boolean isInBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}
}
