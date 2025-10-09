package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King chess piece.
 */
public class King extends Piece {
	/**
	 * Constructs a King piece with a given color and position.
	 *
	 * @param color "white" or "black"
	 * @param position current position of the King on the board
	 */
	public King(String color, Position position) {
		super(color, position);
	}
	
	/**
	 * Returns all possible moves for the King from its current position.
	 * A King can move one square in any direction.
	 *
	 * @return list of possible positions the King can move to
	 */
	@Override
	public List<Position> possibleMoves() {
		List<Position> moves = new ArrayList<Position>();
		int row = position.getRow();
		int col = position.getCol();

		// ALL 8 directions a King can move
		int[] rowOffsets = {-1, -1, -1,  0, 0,  1, 1, 1};
		int[] colOffsets = {-1,  0,  1, -1, 1, -1, 0, 1};

		for (int i = 0; i < 8; i++) {
			int newRow = row + rowOffsets[i];
			int newCol = col + colOffsets[i];

			// Check bounds
			if (isInBounds(newRow, newCol)) {
				moves.add(new Position(newRow, newCol));
			}
		}

		return moves;
	}

	/**
	 * Checks if a position is within the board bounds (0–7 for both row and col).
	 */
	@Override
	protected boolean isInBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}
}