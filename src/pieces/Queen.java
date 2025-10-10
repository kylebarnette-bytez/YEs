package pieces;

import board.Board;
import position.Position;
import utils.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Queen} class represents a queen chess piece.
 *
 * <p>
 * The queen combines the movement abilities of the {@link Rook} and the {@link Bishop}:
 * <ul>
 *   <li>Vertical movement (up and down)</li>
 *   <li>Horizontal movement (left and right)</li>
 *   <li>Diagonal movement (in all four diagonal directions)</li>
 * </ul>
 * It can move any number of squares along a straight line
 * until it is blocked by another piece or captures an opponent's piece.
 *
 * <p>Examples of movement:
 * <pre>
 *   From D1 → any square along rank 1, file D, or diagonals
 *   From E4 → all empty squares reachable horizontally, vertically, or diagonally
 * </pre>
 */
public class Queen extends Piece {

	/**
	 * Constructs a {@code Queen} with the given color and starting position.
	 *
	 * @param color    the color of the queen ({@link Color#WHITE} or {@link Color#BLACK})
	 * @param position the initial position of the queen on the board
	 */
	public Queen(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Generates all legal moves for this queen based on the current board state.
	 * <p>
	 * The queen can:
	 * <ul>
	 *   <li>Move vertically, horizontally, or diagonally any number of squares.</li>
	 *   <li>Capture an opponent’s piece in its path.</li>
	 *   <li>Stop moving when it encounters any piece (cannot jump).</li>
	 * </ul>
	 *
	 * @param board the current {@link Board} state
	 * @return a list of valid {@link Position} objects for this queen
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		// 8 movement directions: vertical, horizontal, and diagonal
		int[][] directions = {
				{-1,  0},  // up
				{ 1,  0},  // down
				{ 0, -1},  // left
				{ 0,  1},  // right
				{-1, -1},  // up-left
				{-1,  1},  // up-right
				{ 1, -1},  // down-left
				{ 1,  1}   // down-right
		};

		// Explore each direction step by step
		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];

			while (isInBounds(r, c)) {
				Position newPos = new Position(r, c);
				Piece pieceAtDestination = board.getPiece(newPos);

				if (pieceAtDestination == null) {
					// ✅ Empty square — can move here
					moves.add(newPos);
				} else {
					// ⚔️ Capture opponent and stop scanning further
					if (pieceAtDestination.getColor() != this.color) {
						moves.add(newPos);
					}
					break; // Blocked — cannot jump over
				}

				r += dir[0];
				c += dir[1];
			}
		}

		return moves;
	}
}
