package board;

import pieces.*;

/**
 * The {@code Board} class represents the chess board.
 * The board is an 8x8 grid of squares, each of which may contain a Piece.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Initialize the board to its starting state.</li>
 *   <li>Allow retrieval of pieces at given positions.</li>
 *   <li>Move pieces from one position to another.</li>
 *   <li>Display the board as text in the console.</li>
 * </ul>
 */
public class Board {
    /** 2D array representing the 8x8 board. Each square may contain a Piece or null. */
    private Piece[][] squares = new Piece[8][8];

    /**
     * Constructs a new chess board and initializes it.
     * For Phase 1, this may be partially or fully populated.
     */
    public Board() {
        initializeBoard();
    }

    /**
     * Initializes the board with the standard starting positions for chess pieces.
     * For Phase 1, this can be adjusted or simplified if needed.
     */
    private void initializeBoard() {
        // Initializes an empty board
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = null;
            }
        }
		
		// White pawns
		for (int col = 0; col < 8; col++) {
			setPiece(new Pawn("white", new Position(6, col)), new Position(6, col));
		}

		// Black pawns
		for (int col = 0; col < 8; col++) {
			setPiece(new Pawn("black", new Position(1, col)), new Position(1, col));
		}

		// Kings
		setPiece(new King("white", new Position(7, 4)), new Position(7, 4)); // e1
		setPiece(new King("black", new Position(0, 4)), new Position(0, 4)); // e8
		
		// Queens
		setPiece(new Queen("white", new Position(7, 3)), new Position(7, 3)); // d1
		setPiece(new Queen("black", new Position(0, 3)), new Position(0, 3)); // d8
		
		// Bishops
		setPiece(new Bishop("white", new Position(7, 2)), new Position(7, 2)); // c1
		setPiece(new Bishop("white", new Position(7, 5)), new Position(7, 5)); // f1
		setPiece(new Bishop("black", new Position(0, 2)), new Position(0, 2)); // c8
		setPiece(new Bishop("black", new Position(0, 5)), new Position(0, 5)); // f8
		
		// Knights
		setPiece(new Knight("white", new Position(7, 1)), new Position(7, 1)); // b1
		setPiece(new Knight("white", new Position(7, 6)), new Position(7, 6)); // g1
		setPiece(new Knight("black", new Position(0, 1)), new Position(0, 1)); // b8
		setPiece(new Knight("black", new Position(0, 6)), new Position(0, 6)); // g8
		
		// Rooks
		setPiece(new Rook("white", new Position(7, 0)), new Position(7, 0)); // a1
		setPiece(new Rook("white", new Position(7, 7)), new Position(7, 7)); // h1
		setPiece(new Rook("black", new Position(0, 0)), new Position(0, 0)); // a8
		setPiece(new Rook("black", new Position(0, 7)), new Position(0, 7)); // h8
    }

    /**
     * Returns the piece at the given position, or null if the square is empty.
     *
     * @param pos the board position
     * @return the Piece at the position, or null
     */
    public Piece getPiece(Position pos) {
        return squares[pos.getRow()][pos.getCol()];
    }

    /**
     * Places a piece on the board at the given position.
     * (useful for testing or custom setups)
     *
     * @param piece the piece to place
     * @param pos   the position to place the piece at
     */
    public void setPiece(Piece piece, Position pos) {
        squares[pos.getRow()][pos.getCol()] = piece;
        if (piece != null) {
            piece.setPosition(pos);
        }
    }

    /**
     * Moves a piece from one square to another.
     * Overwrites any piece at the target square (capture).
     * Clears the source square after the move.
     * If the source square is empty, prints an error and does nothing.
	 * Checks to see if the move is valid given the type of piece.
     *
     * @param from the source position
     * @param to   the target position
     */
    public void movePiece(Position from, Position to) {
        Piece moving = squares[from.getRow()][from.getCol()];

        // ✅ Check if source square is empty
        if (moving == null) {
            System.out.println("No piece at source square.");
            return;
        }
		
		if(!validateMove(from, to)) {
			throw new IllegalArgumentException("Move violates movement rules.");
			return;
		}

        // ✅ Move the piece
        squares[to.getRow()][to.getCol()] = moving;
        moving.setPosition(to);

        // ✅ Clear the source square
        squares[from.getRow()][from.getCol()] = null;
    }

    /**
     * Displays the current board state in the console.
     * Empty squares are shown as "##". Pieces are shown using abbreviations
     * (e.g., "wP" for white pawn, "bR" for black rook).
     */
    public void display() {
        System.out.println("   A  B  C  D  E  F  G  H");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + " "); // rank labels on the left

            for (int col = 0; col < 8; col++) {
                Piece piece = squares[row][col];
                if (piece == null) {
                    System.out.print("## ");
                } else {
					
					String abbrev = piece.getClass().getSimpleName().substring(0, 1);
					if (piece instanceof Knight) {
						abbrev = "N";
					} else if (piece instanceof King) {
						abbrev = "K";
					} else if (piece instanceof Queen) {
						abbrev = "Q";
					} else if (piece instanceof Rook) {
						abbrev = "R";
					} else if (piece instanceof Bishop) {
						abbrev = "B";
					} else if (piece instanceof Pawn) {
						abbrev = "p";
					}
					System.out.print(piece.getColor().charAt(0) + abbrev + " ");
                }
            }

            System.out.println(8 - row);
        }
        System.out.println("   A  B  C  D  E  F  G  H");
    }
	/**
	 * Validates whether a move from one position to another is legal
	 * based on the piece's movement rules and current board state.
	 *
	 * @param from source position
	 * @param to destination position
	 * @return true if the move is valid, false otherwise
	 */
	public boolean validateMove(Position from, Position to) {
		Piece piece = getPiece(from);
		if (piece == null) return false;

		// Basic move legality (piece-defined)
		List<Position> possibleMoves = piece.possibleMoves();
		boolean isValid = false;
		for (Position pos : possibleMoves) {
			if (pos.getRow() == to.getRow() && pos.getCol() == to.getCol()) {
				isValid = true;
				break;
			}
		}
		if (!isValid) return false;

		// Can't capture own piece
		Piece destination = getPiece(to);
		if (destination != null && destination.getColor().equals(piece.getColor())) {
			return false;
		}

		// Pawn-specific logic
		if (piece instanceof Pawn) {
			int fromRow = from.getRow();
			int fromCol = from.getCol();
			int toRow = to.getRow();
			int toCol = to.getCol();
			int rowDiff = Math.abs(toRow - fromRow);
			int colDiff = Math.abs(toCol - fromCol);

			if (colDiff == 1 && rowDiff == 1) {
				// Diagonal: must be capturing
				if (destination == null || destination.getColor().equals(piece.getColor())) {
					return false;
				}
			} else if (colDiff == 0) {
				// Forward: must be clear
				if (destination != null) return false;

				// If moving two steps, also check intermediate square
				if (rowDiff == 2) {
					int midRow = (fromRow + toRow) / 2;
					if (getPiece(new Position(midRow, fromCol)) != null) {
						return false;
					}
				}
			}
		}

		// Obstruction check for Rook, Bishop, Queen
		if (piece instanceof Rook || piece instanceof Bishop || piece instanceof Queen) {
			if (isPathObstructed(from, to)) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Checks if any pieces exist between two positions (exclusive).
	 * Assumes the move is either straight or diagonal.
	 *
	 * @param from starting position
	 * @param to destination position
	 * @return true if the path is obstructed, false if clear
	 */
	private boolean isPathObstructed(Position from, Position to) {
		int fromRow = from.getRow();
		int fromCol = from.getCol();
		int toRow = to.getRow();
		int toCol = to.getCol();

		int rowStep = Integer.compare(toRow, fromRow); // -1, 0, or 1
		int colStep = Integer.compare(toCol, fromCol); // -1, 0, or 1

		int currentRow = fromRow + rowStep;
		int currentCol = fromCol + colStep;

		while (currentRow != toRow || currentCol != toCol) {
			if (getPiece(new Position(currentRow, currentCol)) != null) {
				return true; // Found obstruction
			}
			currentRow += rowStep;
			currentCol += colStep;
		}

		return false; // No obstruction found
	}
}
