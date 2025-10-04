package pieces;

import board.Position;
import java.util.List;

/**
 * The Piece abstract class defines the generic functions of the chess pieces.
 * This includes the piece color, position, and methods to get all possible positions
 * the piece can move to on the board and actually moving the piece.
 */
public abstract class Piece {
	/** Piece color "white" or "black" */
	protected String color;
	/** Current location on the board */
	protected Position position;

	/**
	 * Constructs a Piece with a given color and position.
	 * 
	 * @param color "white" or "black"
	 * @param position current position of the piece on the board
	 */
	public Piece(String color, Position position) {
		this.color = color;
		this.position = position;
	}

	/**
	 * Gets the color of the piece.
	 *
	 * @return color of the piece
	 */
	public String getColor() { return color; }
	
	/**
	 * Gets the position of the piece.
	 *
	 * @return position of the piece
	 */
	public Position getPosition() { return position; }
	
	/**
	 * Sets the position of the piece.
	 *
	 * @param pos the new position
	 */
	public void setPosition(Position pos) { this.position = pos; }

	/**
	 * Gets all possible moves the piece can perform based on its movement rules.
	 *
	 * @return list of all possible moves
	 */
	public abstract List<Position> possibleMoves();
}
