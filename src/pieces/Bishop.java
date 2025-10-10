package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Bishop} class represents a bishop chess piece.
 *
 * <p>
 * A bishop moves any number of squares diagonally in any direction,
 * until it reaches the edge of the board or is blocked by another piece.
 * <ul>
 *   <li>If the square is empty — the bishop can move there.</li>
 *   <li>If the square contains an opponent’s piece — the bishop can capture it, but cannot move past it.</li>
 *   <li>If the square contains its own piece — the bishop cannot move there or beyond.</li>
 * </ul>
 *
 * <p>Examples of movement:
 * <pre>
 *   From D4 → C3, B2, A1
 *   From D4 → E5, F6, G7, H8
 * </pre>
 */
public class Bishop extends Piece {

	/**
	 * Constructs a new {@code Bishop} with the given color and starting position.
	 *
	 * @param color    the color of the bishop ({@link Color#WHITE} or {@link Color#BLACK})
	 * @param position the initial position of the bishop on the board
	 */
	public Bishop(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Generates all legal moves for this bishop based on the current board state.
	 *
	 * @param board the current {@link Board}
	 * @return a list of valid {@link Position} objects the bishop can move to
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();

		int row = position.getRow();
		int col = position.getCol();

		// Four diagonal directions:
		//  top-left (-1, -1), top-right (-1, +1), bottom-left (+1, -1), bottom-right (+1, +1)
		int[][] directions = {
				{-1, -1}, // top-left
				{-1,  1}, // top-right
				{ 1, -1}, // bottom-left
				{ 1,  1}  // bottom-right
		};

		// Scan outward in each direction
		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];

			// Move along the diagonal until blocked or off-board
			while (isInBounds(r, c)) {
				Position nextPos = new Position(r, c);
				Piece occupying = board.getPiece(nextPos);

				if (occupying == null) {
					// Empty square — bishop can move here
					moves.add(nextPos);
				} else {
					// Occupied square — can capture opponent but can't move further
					if (occupying.getColor() != this.color) {
						moves.add(nextPos);
					}
					break;
				}

				// Step further in the same direction
				r += dir[0];
				c += dir[1];
			}
		}

		return moves;
	}
}
