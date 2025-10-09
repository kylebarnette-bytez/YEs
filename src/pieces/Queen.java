package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Queen chess piece.
 */
public class Queen extends Piece {
	/**
	 * Constructs a Queen piece with a given color and position.
	 *
	 * @param color "white" or "black"
	 * @param position current position of the Queen on the board
	 */
	public Queen(String color, Position position) {
		super(color, position);
	}

	/**
	 * Returns all possible moves for the Queen from its current position.
	 * The Queen can move vertically, horizontally, and diagonally as far as
	 * it wants (assuming no obstacles and is within the board).
	 *
	 * @return list of possible positions the Queen can move to
	 */
	@Override
	public List<Position> possibleMoves() {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		// All 8 directions: [row change, col change]
		int[][] directions = {
			{-1,  0}, // up
			{ 1,  0}, // down
			{ 0, -1}, // left
			{ 0,  1}, // right
			{-1, -1}, // top-left
			{-1,  1}, // top-right
			{ 1, -1}, // bottom-left
			{ 1,  1}  // bottom-right
		};

		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];

			// Keep moving in this direction until hitting board edge
			while (isInBounds(r, c)) {
				moves.add(new Position(r, c));
				r += dir[0];
				c += dir[1];
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
