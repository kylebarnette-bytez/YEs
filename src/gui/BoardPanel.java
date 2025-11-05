package gui;

import java.util.Stack; // KYLE: added for undo feature
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import gui.*;

public class BoardPanel extends JPanel {

    private static final int BOARD_SIZE = 8;
    private final SquarePanel[][] squares = new SquarePanel[BOARD_SIZE][BOARD_SIZE];
    private SquarePanel selectedSquare = null;

    // KYLE: added for extra features
    private boolean whiteTurn = true;
    private final Stack<MoveRecord> moveHistory = new Stack<>();
    private ChessGUI parentGUI; // reference to parent for popup + status updates
    // --- added for GameHistoryPanel ---
    private GameHistoryPanel historyPanel;
    public void setHistoryPanel(GameHistoryPanel panel) {
        this.historyPanel = panel;
    }
    public boolean isWhiteTurn() {
        return whiteTurn;
    }




    public BoardPanel(ChessGUI parent) { // KYLE: added parent for GUI communication
        this.parentGUI = parent;
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        initializePieces();
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquarePanel square = new SquarePanel(row, col, this);
                squares[row][col] = square;
                add(square);
            }
        }
    }

    private void initializePieces() {
        // Black pieces
        for (int col = 0; col < BOARD_SIZE; col++) {
            squares[1][col].setPiece("BLACK_PAWN");
        }
        squares[0][0].setPiece("BLACK_ROOK");
        squares[0][7].setPiece("BLACK_ROOK");
        squares[0][1].setPiece("BLACK_KNIGHT");
        squares[0][6].setPiece("BLACK_KNIGHT");
        squares[0][2].setPiece("BLACK_BISHOP");
        squares[0][5].setPiece("BLACK_BISHOP");
        squares[0][3].setPiece("BLACK_QUEEN");
        squares[0][4].setPiece("BLACK_KING");

        // White pieces
        for (int col = 0; col < BOARD_SIZE; col++) {
            squares[6][col].setPiece("WHITE_PAWN");
        }
        squares[7][0].setPiece("WHITE_ROOK");
        squares[7][7].setPiece("WHITE_ROOK");
        squares[7][1].setPiece("WHITE_KNIGHT");
        squares[7][6].setPiece("WHITE_KNIGHT");
        squares[7][2].setPiece("WHITE_BISHOP");
        squares[7][5].setPiece("WHITE_BISHOP");
        squares[7][3].setPiece("WHITE_QUEEN");
        squares[7][4].setPiece("WHITE_KING");
    }

    public void handleSquareClick(SquarePanel clicked) {

        //  Selecting a piece
        if (selectedSquare == null) {
            if (clicked.hasPiece() && isCorrectTurn(clicked.getPieceKey())) {
                clearHighlights(); // remove any old highlights first
                selectedSquare = clicked;
                if (parentGUI != null) {
                    parentGUI.flashMessage("Showing possible moves for " + clicked.getPieceKey().replace("_", " "));
                }

                showPossibleMoves(clicked); // 🔥 highlight all possible moves
                clicked.setHighlighted(true); // highlight selected piece
            }
            return;
        }

        // 2️⃣ — Clicking the same square again cancels selection
        if (clicked == selectedSquare) {
            clearHighlights(); // 🧹 remove move highlights
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        // 3️⃣ — Otherwise, it’s a move attempt
        moveHistory.push(new MoveRecord(selectedSquare, clicked));

        // --- added for GameHistoryPanel ---
        String movingPiece = selectedSquare.getPieceKey();
        String capturedPiece = clicked.getPieceKey();
        String from = "(" + selectedSquare.getRow() + "," + selectedSquare.getCol() + ")";
        String to = "(" + clicked.getRow() + "," + clicked.getCol() + ")";

        if (historyPanel != null) {
            historyPanel.addMove(movingPiece + ": " + from + " → " + to);
            if (capturedPiece != null && !capturedPiece.isEmpty()) {
                String currentPlayer = whiteTurn ? "White" : "Black";
                historyPanel.addCapturedPiece(currentPlayer, capturedPiece);
            }
        }

        // 4️⃣ — Check for King capture (endgame)
        if (clicked.hasPiece() && clicked.getPieceKey().contains("KING")) {
            String winner = whiteTurn ? "White" : "Black";
            clicked.setPiece(selectedSquare.getPieceKey());
            selectedSquare.clearPiece();
            if (parentGUI != null) parentGUI.showEndgameMessage(winner);
            return;
        }

        // 5️⃣ — Perform the actual move
        clicked.setPiece(movingPiece);
        selectedSquare.clearPiece();

        // 6️⃣ — Clean up highlights and selection
        clearHighlights(); // remove possible-move highlights
        selectedSquare.setHighlighted(false);
        selectedSquare = null;

        // 7️⃣ — Switch turn and update status label
        whiteTurn = !whiteTurn;
        if (parentGUI != null) {
            parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");
            parentGUI.switchTurnTimer(whiteTurn); // 👈 new method call
        }

    }

    // KYLE: ensures correct player moves
    private boolean isCorrectTurn(String pieceKey) {
        return (whiteTurn && pieceKey.startsWith("WHITE")) ||
                (!whiteTurn && pieceKey.startsWith("BLACK"));
    }

    // --- Highlight Feature: Show possible moves for a selected piece ---
    public void showPossibleMoves(SquarePanel fromSquare) {
        clearHighlights(); // Remove any old highlights first

        String piece = fromSquare.getPieceKey();
        if (piece == null || piece.isEmpty()) return;

        int row = fromSquare.getRow();
        int col = fromSquare.getCol();

        if (piece.equals("WHITE_PAWN")) {
            if (row > 0 && !squares[row - 1][col].hasPiece()) {
                squares[row - 1][col].setHighlighted(true); // move forward
            }
            // capture diagonally
            if (row > 0 && col > 0 && squares[row - 1][col - 1].hasPiece() &&
                    squares[row - 1][col - 1].getPieceKey().startsWith("BLACK"))
                squares[row - 1][col - 1].setHighlighted(true);
            if (row > 0 && col < 7 && squares[row - 1][col + 1].hasPiece() &&
                    squares[row - 1][col + 1].getPieceKey().startsWith("BLACK"))
                squares[row - 1][col + 1].setHighlighted(true);
        }

        else if (piece.equals("BLACK_PAWN")) {
            if (row < 7 && !squares[row + 1][col].hasPiece()) {
                squares[row + 1][col].setHighlighted(true);
            }
            // capture diagonally
            if (row < 7 && col > 0 && squares[row + 1][col - 1].hasPiece() &&
                    squares[row + 1][col - 1].getPieceKey().startsWith("WHITE"))
                squares[row + 1][col - 1].setHighlighted(true);
            if (row < 7 && col < 7 && squares[row + 1][col + 1].hasPiece() &&
                    squares[row + 1][col + 1].getPieceKey().startsWith("WHITE"))
                squares[row + 1][col + 1].setHighlighted(true);
        }

        else if (piece.endsWith("KNIGHT")) {
            int[][] moves = {{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
            for (int[] m : moves) {
                int r = row + m[0], c = col + m[1];
                if (r >= 0 && r < 8 && c >= 0 && c < 8 && !isSameColor(r, c, piece))
                    squares[r][c].setHighlighted(true);
            }
        }

        else if (piece.endsWith("BISHOP") || piece.endsWith("QUEEN")) {
            int[][] dirs = {{1,1},{1,-1},{-1,1},{-1,-1}};
            slideMoves(row, col, dirs, piece);
        }

        else if (piece.endsWith("ROOK") || piece.endsWith("QUEEN")) {
            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
            slideMoves(row, col, dirs, piece);
        }

        else if (piece.endsWith("KING")) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int r = row + dr, c = col + dc;
                    if (r >= 0 && r < 8 && c >= 0 && c < 8 && !isSameColor(r, c, piece))
                        squares[r][c].setHighlighted(true);
                }
            }
        }
    }

    // Helper: Slide in given directions until blocked
    private void slideMoves(int row, int col, int[][] dirs, String piece) {
        for (int[] dir : dirs) {
            int r = row + dir[0], c = col + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                if (squares[r][c].hasPiece()) {
                    if (!isSameColor(r, c, piece))
                        squares[r][c].setHighlighted(true);
                    break; // stop at first piece
                }
                squares[r][c].setHighlighted(true);
                r += dir[0];
                c += dir[1];
            }
        }
    }

    // Helper: check if target square has same color piece
    private boolean isSameColor(int r, int c, String piece) {
        String target = squares[r][c].getPieceKey();
        if (target == null) return false;
        return (piece.startsWith("WHITE") && target.startsWith("WHITE")) ||
                (piece.startsWith("BLACK") && target.startsWith("BLACK"));
    }

    // Helper: remove all highlights
    private void clearHighlights() {
        for (SquarePanel[] rowArr : squares)
            for (SquarePanel sq : rowArr)
                sq.setHighlighted(false);
    }



    // KYLE: Reset board for "New Game"
    // KYLE: Reset the full board properly
    public void resetBoard() {
        // Clear every square before re-populating
        for (SquarePanel[] row : squares) {
            for (SquarePanel s : row) {
                s.clearPiece();        // remove any piece icon
                s.setHighlighted(false); // remove yellow border if selected
            }
        }

        // Re-add pieces in starting positions
        initializePieces();

        whiteTurn = true;
        if (parentGUI != null)
            parentGUI.updateStatus("White's Turn");
        // --- added for GameHistoryPanel ---
        if (historyPanel != null)
            historyPanel.reset();


        // Force a redraw to clear ghost icons
        revalidate();
        repaint();
        if (parentGUI != null) parentGUI.resetTimers();

    }
	
	// --- SAVE GAME ---
	public void saveGame() {
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showSaveDialog(this);
		if (option != JFileChooser.APPROVE_OPTION) return;

		File file = fileChooser.getSelectedFile();

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
			GameState state = new GameState();

			// Save board state
			for (int row = 0; row < BOARD_SIZE; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					state.board[row][col] = squares[row][col].getPieceKey();
				}
			}

			// Save turn info
			state.whiteTurn = this.whiteTurn;

			// Save move history (just store "from" and "to" coordinates)
			for (MoveRecord m : moveHistory) {
				state.moveHistory.add(new GameState.MoveData(m.fromRow, m.fromCol, m.toRow, m.toCol, m.capturedPiece));
			}

			out.writeObject(state);
			JOptionPane.showMessageDialog(this, "Game saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error saving game: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// --- LOAD GAME ---
	public void loadGame() {
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(this);
		if (option != JFileChooser.APPROVE_OPTION) return;

		File file = fileChooser.getSelectedFile();

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
			GameState state = (GameState) in.readObject();

			// Restore board
			for (int row = 0; row < BOARD_SIZE; row++) {
				for (int col = 0; col < BOARD_SIZE; col++) {
					String piece = state.board[row][col];
					squares[row][col].setPiece(piece);
				}
			}

			// Restore turn
			this.whiteTurn = state.whiteTurn;
			if (parentGUI != null)
				parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");

			// Restore move history
			moveHistory.clear();
			for (GameState.MoveData md : state.moveHistory) {
				SquarePanel from = squares[md.fromRow][md.fromCol];
				SquarePanel to = squares[md.toRow][md.toCol];
				moveHistory.add(new MoveRecord(from, to, md.capturedPiece));
			}

			revalidate();
			repaint();

			JOptionPane.showMessageDialog(this, "Game loaded successfully!", "Load", JOptionPane.INFORMATION_MESSAGE);

		} catch (IOException | ClassNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Error loading game: "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

    // KYLE: Undo last move
    public void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            MoveRecord last = moveHistory.pop();
            last.undo();
            whiteTurn = !whiteTurn;
            if (parentGUI != null)
                parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");
            // --- added for GameHistoryPanel ---
            if (historyPanel != null)
                historyPanel.removeLastMove();

        }
    }

    // KYLE: track each move for Undo
    private static class MoveRecord {
        private final SquarePanel from, to;
        private final String capturedPiece;
		private final int fromRow, fromCol, toRow, toCol;

        MoveRecord(SquarePanel from, SquarePanel to) {
            this(from, to, to.getPieceKey());
        }
		
		//Overloaded method
		MoveRecord(SquarePanel from, SquarePanel to, String capturedPiece) {
            this.from = from;
            this.to = to;
            this.capturedPiece = capturedPiece;
			this.fromRow = from.getRow();
			this.fromCol = from.getCol();
			this.toRow = to.getRow();
			this.toCol = to.getCol();
        }

        void undo() {
            from.setPiece(to.getPieceKey());
            if (capturedPiece != null)
                to.setPiece(capturedPiece);
            else
                to.clearPiece();
        }
    }
	
	//Save file class
	private static class GameState implements Serializable {
		String[][] board = new String[BOARD_SIZE][BOARD_SIZE];
		boolean whiteTurn;
		List<MoveData> moveHistory = new ArrayList<>();

		static class MoveData implements Serializable {
			int fromRow, fromCol, toRow, toCol;
			String capturedPiece;
			MoveData(int fromRow, int fromCol, int toRow, int toCol, String capturedPiece) {
				this.fromRow = fromRow;
				this.fromCol = fromCol;
				this.toRow = toRow;
				this.toCol = toCol;
				this.capturedPiece = capturedPiece;
			}
		}
	}




}
