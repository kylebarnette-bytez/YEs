package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Rook chess piece.
 */
public class Rook extends Piece {

	public Rook(Color color, Position position) {
		super(color, position);
	}

	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		// Four directions: up, down, left, right
		int[][] directions = {
				{-1, 0}, // up
				{1, 0},  // down
				{0, -1}, // left
				{0, 1}   // right
		};

		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];

			while (isInBounds(r, c)) {
				Position nextPos = new Position(r, c);
				Piece occupying = board.getPiece(nextPos);

				if (occupying == null) {
					// Empty square — valid move
					moves.add(nextPos);
				} else {
					// If there's a piece of the opposite color, capture allowed
					if (occupying.getColor() != this.color) {
						moves.add(nextPos);
					}
					// Stop searching further in this direction
					break;
				}

				r += dir[0];
				c += dir[1];
			}
		}

		return moves;
	}
}
