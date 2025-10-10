package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Knight} class represents a knight chess piece.
 *
 * <p>
 * A knight moves in an "L" shape:
 * <ul>
 *   <li>Two squares in one direction (vertical or horizontal),</li>
 *   <li>then one square perpendicular to that direction.</li>
 * </ul>
 *
 * Unlike other pieces, a knight can **jump over other pieces**.
 * It can land on an empty square or capture an opponent’s piece.
 *
 * <p>Examples of movement:
 * <pre>
 *   From G1 → E2, F3, H3
 *   From D4 → B3, B5, C2, C6, E2, E6, F3, F5
 * </pre>
 */
public class Knight extends Piece {

	/**
	 * Constructs a {@code Knight} with the given color and starting position.
	 *
	 * @param color    the color of the knight ({@link Color#WHITE} or {@link Color#BLACK})
	 * @param position the initial position of the knight on the board
	 */
	public Knight(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Generates all legal moves for this knight based on the current board state.
	 * <p>
	 * A knight ignores blocking pieces and can move to:
	 * <ul>
	 *   <li>an empty square, or</li>
	 *   <li>a square occupied by an opponent’s piece (capture)</li>
	 * </ul>
	 *
	 * @param board the current {@link Board} state
	 * @return a list of valid {@link Position} objects for this knight
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();

		int row = position.getRow();
		int col = position.getCol();

		// All 8 possible L-shaped moves relative to current position
		int[] rowOffsets = {-2, -2,  2,  2, -1,  1, -1,  1};
		int[] colOffsets = {-1,  1, -1,  1, -2, -2,  2,  2};

		for (int i = 0; i < 8; i++) {
			int newRow = row + rowOffsets[i];
			int newCol = col + colOffsets[i];

			if (isInBounds(newRow, newCol)) {
				Position newPos = new Position(newRow, newCol);
				Piece occupying = board.getPiece(newPos);

				// ✅ Add move if destination is empty or contains opponent's piece
				if (occupying == null || occupying.getColor() != this.color) {
					moves.add(newPos);
				}
			}
		}

		return moves;
	}
}
