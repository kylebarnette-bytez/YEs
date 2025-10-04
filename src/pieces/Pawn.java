package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
* Represents a Pawn chess piece.
*/
public class Pawn extends Piece {
	/** Indicates if this is the first move of the pawn */
	private boolean firstMove = true;
	
	/**
	 * Sets that the pawn has moved (Should be called after a move is made).
	 */
	public void markMoved() {
		firstMove = false;
	}
	
	/**
	 * Constructs a Pawn piece with a given color and position.
	 *
	 * @param color "white" or "black"
	 * @param position current position of the Pawn on the board
	 */
	 public Pawn(String color, Position position) {
		 super(color, position);
	 }

	/**
	 * Returns all possible moves for the Pawn from its current position.
	 * A Pawn can move two spaces forward only on its first move, otherwise it can
	 * only move one space forward (or forward diagonaly if it's capturing a piece).
	 * The "forward" direction depends on its color.
	 *
	 * @return list of possible positions the Pawn can move to
	 */
	@Override
	public List<Position> possibleMoves() {
		List<Position> moves = new ArrayList<Position>();
		int row = position.getRow();
		int col = position.getCol();

		// Determine direction: white moves up (row - 1), black moves down (row + 1)
		int direction = color.equalsIgnoreCase("white") ? -1 : 1;

		// One square forward
		int oneStepRow = row + direction;
		if (isInBounds(oneStepRow, col)) {
			moves.add(new Position(oneStepRow, col));
		}

		// Two squares forward if first move
		int twoStepRow = row + (2 * direction);
		if (firstMove && isInBounds(twoStepRow, col)) {
			moves.add(new Position(twoStepRow, col));
		}

		// Diagonal captures (Technically possible moves, but should check if there is an enemy present)
		if (isInBounds(oneStepRow, col - 1)) {
			moves.add(new Position(oneStepRow, col - 1));
		}
		if (isInBounds(oneStepRow, col + 1)) {
			moves.add(new Position(oneStepRow, col + 1));
		}

		return moves;
	}

	/**
	 * Checks if a position is within the board bounds (0–7 for both row and col).
	 */
	private boolean isInBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}
}
