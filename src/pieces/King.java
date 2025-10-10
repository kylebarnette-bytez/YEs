package pieces;

import position.Position;
import utils.Color;
import board.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a King chess piece.
 * A King can move one square in any direction.
 */
public class King extends Piece {

	/**
	 * Constructs a King piece with a given color and position.
	 *
	 * @param color the color of the piece (WHITE or BLACK)
	 * @param position current position of the King on the board
	 */
	public King(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Returns all possible moves for the King from its current position.
	 * A King can move one square in any direction, provided it's within bounds
	 * and doesn't land on its own color piece.
	 *
	 * @param board the current chess board
	 * @return list of possible positions the King can move to
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		int[] rowOffsets = {-1, -1, -1,  0, 0,  1, 1, 1};
		int[] colOffsets = {-1,  0,  1, -1, 1, -1, 0, 1};

		for (int i = 0; i < 8; i++) {
			int newRow = row + rowOffsets[i];
			int newCol = col + colOffsets[i];

			if (isInBounds(newRow, newCol)) {
				Position newPos = new Position(newRow, newCol);
				Piece pieceAtDestination = board.getPiece(newPos);

				// Only add if the square is empty or occupied by opponent
				if (pieceAtDestination == null || pieceAtDestination.getColor() != this.color) {
					moves.add(newPos);
				}
			}
		}

		return moves;
	}
}
