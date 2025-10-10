package pieces;

import position.Position;
import utils.Color;
import board.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code King} class represents a king chess piece.
 *
 * <p>
 * A king can move exactly one square in any direction:
 * <ul>
 *   <li>vertically</li>
 *   <li>horizontally</li>
 *   <li>diagonally</li>
 * </ul>
 *
 * The king cannot land on a square occupied by its own color.
 * In future phases, castling logic could also be added here.
 *
 * <p>Examples of movement:
 * <pre>
 *   From E1 → D1, F1, D2, E2, F2
 *   From D4 → C4, E4, C3, D3, E3, C5, D5, E5
 * </pre>
 */
public class King extends Piece {

	/**
	 * Constructs a {@code King} with the given color and starting position.
	 *
	 * @param color    the color of the king ({@link Color#WHITE} or {@link Color#BLACK})
	 * @param position the initial position of the king on the board
	 */
	public King(Color color, Position position) {
		super(color, position);
	}

	/**
	 * Generates all legal moves for the king based on the current board state.
	 * <p>
	 * The king can move to any adjacent square, provided that:
	 * <ul>
	 *   <li>the target square is inside the board, and</li>
	 *   <li>the target square is empty or occupied by an opponent’s piece</li>
	 * </ul>
	 *
	 * @param board the current {@link Board} state
	 * @return a list of valid {@link Position} objects for this king
	 */
	@Override
	public List<Position> possibleMoves(Board board) {
		List<Position> moves = new ArrayList<>();

		int row = position.getRow();
		int col = position.getCol();

		// 8 surrounding squares (top-left, top, top-right, left, right, bottom-left, bottom, bottom-right)
		int[] rowOffsets = {-1, -1, -1,  0, 0,  1, 1, 1};
		int[] colOffsets = {-1,  0,  1, -1, 1, -1, 0, 1};

		for (int i = 0; i < 8; i++) {
			int newRow = row + rowOffsets[i];
			int newCol = col + colOffsets[i];

			if (isInBounds(newRow, newCol)) {
				Position newPos = new Position(newRow, newCol);
				Piece pieceAtDestination = board.getPiece(newPos);

				// ✅ Only allow empty squares or captures
				if (pieceAtDestination == null || pieceAtDestination.getColor() != this.color) {
					moves.add(newPos);
				}
			}
		}

		return moves;
	}
}
