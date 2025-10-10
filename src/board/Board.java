package board;

import game.Player;
import pieces.*;
import utils.Color;
import position.Position;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents the chess board and manages piece placement, movement, and checks.
 */
public class Board {

	/** 8x8 array representing the board. */
	private Piece[][] board = new Piece[8][8];

	/** References to both players to sync piece ownership. */
	private Player whitePlayer;
	private Player blackPlayer;

	/**
	 * Creates a new Board with references to both players
	 * so their available pieces can be tracked automatically.
	 */
	public Board(Player whitePlayer, Player blackPlayer) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		initializeBoard();
	}

	public Board() {
		this.whitePlayer = null;
		this.blackPlayer = null;
		initializeBoard();
	}

	/**
	 * Initializes the board with the standard starting positions
	 * and registers all pieces to their respective players.
	 */
	private void initializeBoard() {
		// Clear board
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				board[row][col] = null;
			}
		}

		// Helper to register piece with correct player
		Consumer<Piece> registerPiece = piece -> {
			if (piece.getColor() == Color.WHITE) {
				whitePlayer.addPiece(piece);
			} else {
				blackPlayer.addPiece(piece);
			}
		};

		// 🟡 Pawns
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
	 * Returns the piece at the given position, or null if empty.
	 */
	public Piece getPiece(Position pos) {
		return board[pos.getRow()][pos.getCol()];
	}

	/**
	 * Places a piece on the board at the given position.
	 */
	public void setPiece(Piece piece, Position pos) {
		board[pos.getRow()][pos.getCol()] = piece;
		if (piece != null) {
			piece.move(pos);
		}
	}

	/**
	 * Moves a piece from one square to another if valid.
	 * Also handles capturing and updating player piece lists.
	 */
	public void movePiece(Position from, Position to) {
		Piece moving = board[from.getRow()][from.getCol()];

		if (moving == null) {
			System.out.println("No piece at source square.");
			return;
		}

		if (!validateMove(from, to)) {
			throw new IllegalArgumentException("Move violates movement rules.");
		}

		// Handle capture
		Piece dest = board[to.getRow()][to.getCol()];
		if (dest != null) {
			if (dest.getColor() == Color.WHITE) {
				whitePlayer.removePiece(dest);
			} else {
				blackPlayer.removePiece(dest);
			}
		}

		// Perform move
		board[to.getRow()][to.getCol()] = moving;
		moving.move(to);
		board[from.getRow()][from.getCol()] = null;
	}

	/**
	 * Displays the current board state in the console.
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
	 */
	public boolean validateMove(Position from, Position to) {
		Piece piece = getPiece(from);
		if (piece == null) return false;

		List<Position> possibleMoves = piece.possibleMoves(this);
		if (!possibleMoves.contains(to)) return false;

		// Can't capture your own piece
		Piece dest = getPiece(to);
		if (dest != null && dest.getColor() == piece.getColor()) {
			return false;
		}

		return true;
	}

	/**
	 * Finds and returns the position of the king of the given color.
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
	 * Checks if a given color is currently in check.
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
	 * Checks if a given color is in checkmate.
	 */
	public boolean isCheckmate(Color color) {
		if (!isCheck(color)) return false;

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Piece piece = board[row][col];
				if (piece != null && piece.getColor() == color) {
					List<Position> moves = piece.possibleMoves(this);
					for (Position move : moves) {
						Board temp = this.copy();
						temp.movePiece(piece.getPosition(), move);
						if (!temp.isCheck(color)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Returns a shallow copy of the current board.
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
