package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Bishop chess piece.
 */
public class Bishop extends Piece {

	public Bishop(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Returns all possible moves for the Bishop from its current position.
	 * A Bishop can move diagonally in any direction until it encounters
	 * another piece or the edge of the board.
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		// Diagonal directions
		int[][] directions = {
				{-1, -1}, // top-left
				{-1,  1}, // top-right
				{ 1, -1}, // bottom-left
				{ 1,  1}  // bottom-right
		};

		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];

			while (isInBounds(r, c)) {
				Position nextPos = new Position(r, c);
				Piece occupying = board.getPiece(nextPos);

				if (occupying == null) {
					moves.add(nextPos);
				} else {
					if (occupying.getColor() != this.color) {
						moves.add(nextPos); // capture opponent piece
					}
					break; // stop scanning further
				}

				r += dir[0];
				c += dir[1];
			}
		}

		return moves;
	}
}
