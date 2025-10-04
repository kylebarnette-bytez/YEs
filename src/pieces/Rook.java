package pieces;

import board.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Rook chess piece.
 */
public class Rook extends Piece {
	/**
	 * Constructs a Rook piece with a given color and position.
	 *
	 * @param color "white" or "black"
	 * @param position current position of the Rook on the board
	 */
	public Rook(String color, Position position) {
		super(color, position);
	}
	
	/**
	 * Returns all possible moves for the Rook from its current position.
	 * A Rook can move horizontally or vertically as far as it wants
	 * (assuming no obstacles and is within the board).
	 *
	 * @return list of possible positions the Rook can move to
	 */
	@Override
	public List<Position> possibleMoves() {
		List<Position> moves = new ArrayList<Position>();
		int row = position.getRow();
		int col = position.getCol();
		
		//Vertical moves
		for(int i = 0; i < 8; i++) {
			if(i != row) {
				moves.add(new Position(i, col));
			}
		}
		
		//Horizontal moves
		for(int i = 0; i < 8; i++) {
			if(i != col) {
				moves.add(new Position(row, i));
			}
		}
		
		return moves;
	}
}