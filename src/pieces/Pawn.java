package pieces;

import utils.Color;
import board.Board;
import position.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pawn chess piece.
 */
public class Pawn extends Piece {
	private boolean firstMove = true;

	public Pawn(Color color, Position position) {
		super(color, position);
	}

	public void markMoved() {
		firstMove = false;
	}

	/**
	 * Returns all possible moves for the Pawn from its current position.
	 * - Can move one step forward if empty
	 * - Can move two steps forward if first move and both squares empty
	 * - Can capture diagonally if an enemy piece is present
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();
		int row = position.getRow();
		int col = position.getCol();

		// Determine direction: white moves up (row - 1), black moves down (row + 1)
		int direction = (color == Color.WHITE) ? -1 : 1;

		// One square forward
		int oneStepRow = row + direction;
		Position oneStep = new Position(oneStepRow, col);
		if (isInBounds(oneStepRow, col) && board.getPiece(oneStep) == null) {
			moves.add(oneStep);

			// Two squares forward if first move and both squares empty
			int twoStepRow = row + (2 * direction);
			Position twoStep = new Position(twoStepRow, col);
			if (firstMove && isInBounds(twoStepRow, col) && board.getPiece(twoStep) == null) {
				moves.add(twoStep);
			}
		}

		// Diagonal captures
		int[] captureCols = {col - 1, col + 1};
		for (int c : captureCols) {
			if (isInBounds(oneStepRow, c)) {
				Position capturePos = new Position(oneStepRow, c);
				Piece target = board.getPiece(capturePos);
				if (target != null && target.getColor() != this.color) {
					moves.add(capturePos);
				}
			}
		}

		return moves;
	}
}
