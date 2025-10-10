package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Rook} class represents a rook chess piece.
 *
 * <p>
 * The rook moves any number of squares along:
 * <ul>
 *   <li>Vertical files (up or down)</li>
 *   <li>Horizontal ranks (left or right)</li>
 * </ul>
 *
 * It cannot jump over other pieces — its path must be clear.
 * If it encounters an opponent’s piece, it can capture it and must stop.
 *
 * <p>Examples of movement:
 * <pre>
 *   Rook at A1 → up along file A, right along rank 1
 *   Rook at D4 → up, down, left, right from D4 until blocked
 * </pre>
 */
public class Rook extends Piece {

	/**
	 * Constructs a {@code Rook} with the given color and starting position.
	 *
	 * @param color    the color of the rook ({@link Color#WHITE} or {@link Color#BLACK})
	 * @param position the initial position of the rook on the board
	 */
	public Rook(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Generates all legal moves for this rook based on the current board state.
	 * <p>
	 * The rook can:
	 * <ul>
	 *   <li>Move vertically or horizontally any number of squares.</li>
	 *   <li>Capture an opponent’s piece in its path.</li>
	 *   <li>Stop moving when it encounters any piece (cannot jump).</li>
	 * </ul>
	 *
	 * @param board the current {@link Board} state
	 * @return a list of valid {@link Position} objects for this rook
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();

		int row = position.getRow();
		int col = position.getCol();

		// 4 straight-line directions: up, down, left, right
		int[][] directions = {
				{-1, 0}, // up
				{ 1, 0}, // down
				{ 0, -1}, // left
				{ 0,  1}  // right
		};

		// Explore each direction step by step
		for (int[] dir : directions) {
			int r = row + dir[0];
			int c = col + dir[1];

			while (isInBounds(r, c)) {
				Position nextPos = new Position(r, c);
				Piece occupying = board.getPiece(nextPos);

				if (occupying == null) {
					// ✅ Empty square — valid move
					moves.add(nextPos);
				} else {
					// ⚔️ Capture opponent and stop scanning further
					if (occupying.getColor() != this.color) {
						moves.add(nextPos);
					}
					break; // Blocked by any piece
				}

				// Advance further in the same direction
				r += dir[0];
				c += dir[1];
			}
		}

		return moves;
	}
}
