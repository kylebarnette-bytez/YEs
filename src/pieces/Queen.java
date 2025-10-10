package pieces;

import board.Board;
import position.Position;
import utils.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Queen chess piece.
 * A Queen can move any number of squares vertically, horizontally,
 * or diagonally until it is blocked or captures an opponent's piece.
 */
public class Queen extends Piece {

	/**
	 * Constructs a Queen piece with a given color and position.
	 *
	 * @param color the color of the piece (WHITE or BLACK)
	 * @param position the current position of the Queen on the board
	 */
	public Queen(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Returns all possible moves for the Queen from its current position.
	 * The Queen can move vertically, horizontally, or diagonally,
	 * and can capture opposing pieces.
	 *
	 * @param board the chess board
	 * @return list of possible positions the Queen can move to
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		// All 8 directions
		int[][] directions = {
				{-1,  0}, { 1,  0}, { 0, -1}, { 0,  1},
				{-1, -1}, {-1,  1}, { 1, -1}, { 1,  1}
		};

		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];

			// Move in this direction until blocked
			while (isInBounds(r, c)) {
				Position newPos = new Position(r, c);
				Piece pieceAtDestination = board.getPiece(newPos);

				if (pieceAtDestination == null) {
					moves.add(newPos);
				} else {
					// Stop on opponent piece (capture allowed)
					if (pieceAtDestination.getColor() != this.color) {
						moves.add(newPos);
					}
					break;
				}

				r += dir[0];
				c += dir[1];
			}
		}

		return moves;
	}
}
