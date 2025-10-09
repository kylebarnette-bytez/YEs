package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Knight chess piece
 */
public class Knight extends Piece {
	/**
	 * Constructs a Knight piece with a given color and position.
	 *
	 * @param color "white" or "black"
	 * @param position current position of the Knight on the board
	 */
	public Knight(String color, Position position) {
		super(color, position);
	}
	 
	/**
	 * Returns all possible moves for the Knight from its current position.
	 * A Knight can move in an L shape (two spaces one direction, one space perpendicular).
	 *
	 * @return list of possible positions the Knight can move to
	 */
	@Override
	public List<Position> possibleMoves() {
		List<Position> moves = new ArrayList<Position>();
		int row = position.getRow();
		int col = position.getCol();
		
		// All 8 possible L-shape moves
		int[] rowOffsets = {-2, -2,  2, 2, -1,  1, -1, 1};
		int[] colOffsets = {-1,  1, -1, 1, -2, -2,  2, 2};

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