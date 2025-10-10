package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Pawn} class represents a pawn chess piece.
 *
 * <p>
 * Pawns move differently than other pieces:
 * <ul>
 *   <li>They move <b>forward</b> only (toward the opponent).</li>
 *   <li>They capture <b>diagonally forward</b>.</li>
 *   <li>On their first move, they may advance two squares.</li>
 *   <li>They cannot move forward into an occupied square.</li>
 * </ul>
 *
 * <p>Examples of movement:
 * <pre>
 *   White pawn on E2 → E3 (normal), E4 (if first move)
 *   Black pawn on D7 → D6 (normal), D5 (if first move)
 *   White pawn on E5 capturing → D6 or F6
 * </pre>
 *
 * <p>
 * Special rules like en passant or promotion are not implemented in this phase,
 * but can be added later for completeness.
 * </p>
 */
public class Pawn extends Piece {

	/** Whether the pawn has not moved yet (important for the two-square opening move). */
	private boolean firstMove = true;

	/**
	 * Constructs a {@code Pawn} with the given color and starting position.
	 *
	 * @param color    the color of the pawn ({@link Color#WHITE} or {@link Color#BLACK})
	 * @param position the initial position of the pawn on the board
	 */
	public Pawn(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Marks this pawn as having moved at least once.
	 * <p>This should be called after a successful move to disable the two-step option.</p>
	 */
	public void markMoved() {
		firstMove = false;
	}

	/**
	 * Generates all legal moves for this pawn based on the current board state.
	 * <ul>
	 *   <li>1 square forward if empty</li>
	 *   <li>2 squares forward on first move if both squares are empty</li>
	 *   <li>Capture diagonally forward if an opponent's piece is present</li>
	 * </ul>
	 *
	 * @param board the current {@link Board} state
	 * @return a list of valid {@link Position} objects for this pawn
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();

		int row = position.getRow();
		int col = position.getCol();

		// White moves up (row - 1), Black moves down (row + 1)
		int direction = (color == Color.WHITE) ? -1 : 1;

		// --- Forward movement ---
		int oneStepRow = row + direction;
		Position oneStep = new Position(oneStepRow, col);

		// Move one square forward if empty
		if (isInBounds(oneStepRow, col) && board.getPiece(oneStep) == null) {
			moves.add(oneStep);

			// Move two squares forward if first move and path is clear
			int twoStepRow = row + (2 * direction);
			Position twoStep = new Position(twoStepRow, col);
			if (firstMove && isInBounds(twoStepRow, col) && board.getPiece(twoStep) == null) {
				moves.add(twoStep);
			}
		}

		// --- Diagonal captures ---
		int[] captureCols = {col - 1, col + 1};
		for (int c : captureCols) {
			if (isInBounds(oneStepRow, c)) {
				Position capturePos = new Position(oneStepRow, c);
				Piece target = board.getPiece(capturePos);

				// Capture only if opponent piece present
				if (target != null && target.getColor() != this.color) {
					moves.add(capturePos);
				}
			}
		}

		return moves;
	}
}
