package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Knight chess piece.
 */
public class Knight extends Piece {

	public Knight(Color color, Position position) {
		super(color, position);
	}

	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		int[] rowOffsets = {-2, -2,  2,  2, -1,  1, -1,  1};
		int[] colOffsets = {-1,  1, -1,  1, -2, -2,  2,  2};

		for (int i = 0; i < 8; i++) {
			int newRow = row + rowOffsets[i];
			int newCol = col + colOffsets[i];

			if (isInBounds(newRow, newCol)) {
				Position newPos = new Position(newRow, newCol);
				Piece occupying = board.getPiece(newPos);
				if (occupying == null || occupying.getColor() != this.color) {
					moves.add(newPos);
				}
			}
		}

		return moves;
	}
}
