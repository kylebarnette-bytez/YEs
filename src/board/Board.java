package board;

import game.Player;
import pieces.*;
import utils.Color;
import position.Position;

import java.util.List;
import java.util.function.Consumer;

/**
 * The {@code Board} class represents an 8x8 chess board and manages
 * piece placement, movement, and game state checks such as check and checkmate.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Initialize the board with the standard starting chess layout.</li>
 *   <li>Track which pieces belong to which player.</li>
 *   <li>Provide methods to move pieces, validate moves, and check for check/checkmate conditions.</li>
 *   <li>Render the board state in the console.</li>
 * </ul>
 *
 * <p>Note: The board is represented internally as a 2D array of {@link Piece}.
 */
public class Board {

	/** 8x8 array representing the board. Each element is a Piece or null for an empty square. */
	private Piece[][] board = new Piece[8][8];

	/** References to both players to sync piece ownership. */
	private Player whitePlayer;
	private Player blackPlayer;

	/**
	 * Constructs a new board with references to both players.
	 * Used when starting an actual game to keep piece lists in sync with the board.
	 *
	 * @param whitePlayer the white player
	 * @param blackPlayer the black player
	 */
	public Board(Player whitePlayer, Player blackPlayer) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		initializeBoard();
	}

	/**
	 * Constructs a board without player references.
	 * Useful for temporary boards (e.g., checkmate simulations).
	 */
	public Board() {
		this.whitePlayer = null;
		this.blackPlayer = null;
		initializeBoard();
	}

	/**
	 * Initializes the board with standard chess starting positions
	 * and registers pieces to their respective players.
	 */
	private void initializeBoard() {
		// Clear the board
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				board[row][col] = null;
			}
		}

		// Helper lambda to register pieces with the correct player
		Consumer<Piece> registerPiece = piece -> {
			if (piece.getColor() == Color.WHITE) {
				whitePlayer.addPiece(piece);
			} else {
				blackPlayer.addPiece(piece);
			}
		};

		// 🟡 Place Pawns
		for (int col = 0; col < 8; col++) {
			Piece wp = new Pawn(Color.WHITE, new Position(6, col));
			Piece bp = new Pawn(Color.BLACK, new Position(1, col));
			setPiece(wp, new Position(6, col));
			setPiece(bp, new Position(1, col));
			registerPiece.accept(wp);
			registerPiece.accept(bp);
		}

		// 👑 Kings
		Piece wK = new King(Color.WHITE, new Position(7, 4));
		Piece bK = new King(Color.BLACK, new Position(0, 4));
		setPiece(wK, new Position(7, 4));
		setPiece(bK, new Position(0, 4));
		registerPiece.accept(wK);
		registerPiece.accept(bK);

		// 👸 Queens
		Piece wQ = new Queen(Color.WHITE, new Position(7, 3));
		Piece bQ = new Queen(Color.BLACK, new Position(0, 3));
		setPiece(wQ, new Position(7, 3));
		setPiece(bQ, new Position(0, 3));
		registerPiece.accept(wQ);
		registerPiece.accept(bQ);

		// 🧙 Bishops
		Piece wb1 = new Bishop(Color.WHITE, new Position(7, 2));
		Piece wb2 = new Bishop(Color.WHITE, new Position(7, 5));
		Piece bb1 = new Bishop(Color.BLACK, new Position(0, 2));
		Piece bb2 = new Bishop(Color.BLACK, new Position(0, 5));
		setPiece(wb1, new Position(7, 2));
		setPiece(wb2, new Position(7, 5));
		setPiece(bb1, new Position(0, 2));
		setPiece(bb2, new Position(0, 5));
		registerPiece.accept(wb1);
		registerPiece.accept(wb2);
		registerPiece.accept(bb1);
		registerPiece.accept(bb2);

		// 🐴 Knights
		Piece wn1 = new Knight(Color.WHITE, new Position(7, 1));
		Piece wn2 = new Knight(Color.WHITE, new Position(7, 6));
		Piece bn1 = new Knight(Color.BLACK, new Position(0, 1));
		Piece bn2 = new Knight(Color.BLACK, new Position(0, 6));
		setPiece(wn1, new Position(7, 1));
		setPiece(wn2, new Position(7, 6));
		setPiece(bn1, new Position(0, 1));
		setPiece(bn2, new Position(0, 6));
		registerPiece.accept(wn1);
		registerPiece.accept(wn2);
		registerPiece.accept(bn1);
		registerPiece.accept(bn2);

		// 🏰 Rooks
		Piece wr1 = new Rook(Color.WHITE, new Position(7, 0));
		Piece wr2 = new Rook(Color.WHITE, new Position(7, 7));
		Piece br1 = new Rook(Color.BLACK, new Position(0, 0));
		Piece br2 = new Rook(Color.BLACK, new Position(0, 7));
		setPiece(wr1, new Position(7, 0));
		setPiece(wr2, new Position(7, 7));
		setPiece(br1, new Position(0, 0));
		setPiece(br2, new Position(0, 7));
		registerPiece.accept(wr1);
		registerPiece.accept(wr2);
		registerPiece.accept(br1);
		registerPiece.accept(br2);
	}

	/**
	 * Returns the piece at the given position, or {@code null} if the square is empty.
	 *
	 * @param pos board coordinates
	 * @return the piece at the position or null
	 */
	public Piece getPiece(Position pos) {
		return board[pos.getRow()][pos.getCol()];
	}

	/**
	 * Places a piece on the board at the given position.
	 *
	 * @param piece the piece to place
	 * @param pos   the target position
	 */
	public void setPiece(Piece piece, Position pos) {
		board[pos.getRow()][pos.getCol()] = piece;
		if (piece != null) {
			piece.move(pos);
		}
	}

	/**
	 * Moves a piece from one square to another.
	 * If {@code verbose} is true, prints messages for invalid moves.
	 * Handles captures and updates the player’s piece lists.
	 *
	 * @param from    source square
	 * @param to      target square
	 * @param verbose whether to print errors
	 */
	public void movePiece(Position from, Position to, boolean verbose) {
		Piece moving = board[from.getRow()][from.getCol()];

		// Handle empty source square
		if (moving == null) {
			if (verbose) {
				System.out.println("No piece at source square.");
			}
			return;
		}

		// Validate legal move
		if (!validateMove(from, to)) {
			if (verbose) {
				throw new IllegalArgumentException("Move violates movement rules.");
			} else {
				return;
			}
		}

		// Handle capture if target square is occupied
		Piece dest = board[to.getRow()][to.getCol()];
		if (dest != null) {
			if (dest.getColor() == Color.WHITE) {
				whitePlayer.removePiece(dest);
			} else {
				blackPlayer.removePiece(dest);
			}
		}

		// Execute the move
		board[to.getRow()][to.getCol()] = moving;
		moving.move(to);
		board[from.getRow()][from.getCol()] = null;
	}

	/**
	 * Moves a piece with verbose errors enabled by default.
	 *
	 * @param from source square
	 * @param to   target square
	 */
	public void movePiece(Position from, Position to) {
		movePiece(from, to, true);
	}

	/**
	 * Prints a text-based rendering of the board to the console.
	 */
	public void display() {
		System.out.println("   A  B  C  D  E  F  G  H");
		for (int row = 0; row < 8; row++) {
			System.out.print((8 - row) + " ");
			for (int col = 0; col < 8; col++) {
				Piece piece = board[row][col];
				if (piece == null) {
					System.out.print("## ");
				} else {
					System.out.print(piece.toString() + " ");
				}
			}
			System.out.println(8 - row);
		}
		System.out.println("   A  B  C  D  E  F  G  H");
	}

	/**
	 * Validates whether a move from one position to another is legal.
	 * This checks both the piece’s movement rules and team color conflicts.
	 *
	 * @param from source square
	 * @param to   target square
	 * @return true if the move is valid, false otherwise
	 */
	public boolean validateMove(Position from, Position to) {
		Piece piece = getPiece(from);
		if (piece == null) return false;

		List<Position> possibleMoves = piece.possibleMoves(this);
		if (!possibleMoves.contains(to)) return false;

		// Prevent capturing your own piece
		Piece dest = getPiece(to);
		if (dest != null && dest.getColor() == piece.getColor()) {
			return false;
		}

		return true;
	}

	/**
	 * Finds and returns the position of the king of the given color.
	 *
	 * @param color the king’s color
	 * @return king position, or null if not found (should never happen in a real game)
	 */
	private Position findKing(Color color) {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Piece piece = board[row][col];
				if (piece instanceof King && piece.getColor() == color) {
					return new Position(row, col);
				}
			}
		}
		return null;
	}

	/**
	 * Checks whether the king of the given color is currently in check.
	 *
	 * @param color color to check
	 * @return true if in check, false otherwise
	 */
	public boolean isCheck(Color color) {
		Position kingPos = findKing(color);
		if (kingPos == null) return false;

		Color opponent = (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Piece piece = board[row][col];
				if (piece != null && piece.getColor() == opponent) {
					List<Position> moves = piece.possibleMoves(this);
					if (moves.contains(kingPos)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether the given color is in checkmate.
	 *
	 * @param color the color to check for checkmate
	 * @return true if checkmate, false otherwise
	 */
	public boolean isCheckmate(Color color) {
		// Step 1: If the king is not currently in check, it's not checkmate
		if (!isCheck(color)) return false;

		// Step 2: Check every piece belonging to this color
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Piece piece = board[row][col];
				if (piece != null && piece.getColor() == color) {
					List<Position> moves = piece.possibleMoves(this);
					for (Position move : moves) {
						Board temp = this.copy();
						try {
							// ⚡ Simulate the move silently
							temp.movePiece(piece.getPosition(), move, false);

							// If the king is NOT in check after this move → not checkmate
							if (!temp.isCheck(color)) {
								return false;
							}
						} catch (IllegalArgumentException ignored) {
							// Ignore invalid moves during simulation
						}
					}
				}
			}
		}

		// Step 3: No legal moves can escape check — checkmate
		return true;
	}

	/**
	 * Creates a shallow copy of the current board.
	 * Used for check/checkmate simulations without altering real state.
	 *
	 * @return new Board instance with the same piece layout
	 */
	public Board copy() {
		Board newBoard = new Board(whitePlayer, blackPlayer);
		for (int r = 0; r < 8; r++) {
			for (int c = 0; c < 8; c++) {
				newBoard.board[r][c] = this.board[r][c];
			}
		}
		return newBoard;
	}
}
