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
        if (selectedSquare == null) {
            if (clicked.hasPiece() && isCorrectTurn(clicked.getPieceKey())) {
                selectedSquare = clicked;
                clicked.setHighlighted(true);
            }
            return;
        }

        if (clicked == selectedSquare) {
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        // KYLE: track move history for Undo
        moveHistory.push(new MoveRecord(selectedSquare, clicked));

		// KYLE: check if King captured
        if (clicked.hasPiece() && clicked.getPieceKey().contains("KING")) {
            String winner = whiteTurn ? "White" : "Black";
            clicked.setPiece(selectedSquare.getPieceKey());
            selectedSquare.clearPiece();
            if (parentGUI != null) parentGUI.showEndgameMessage(winner);
            return;
        }

        String movingPiece = selectedSquare.getPieceKey();
        clicked.setPiece(movingPiece);

        selectedSquare.clearPiece();
        selectedSquare.setHighlighted(false);
        selectedSquare = null;
        // KYLE: switch turns and update label
        whiteTurn = !whiteTurn;
        if (parentGUI != null)
            parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");

    }
    // KYLE: ensures correct player moves
    private boolean isCorrectTurn(String pieceKey) {
        return (whiteTurn && pieceKey.startsWith("WHITE")) ||
                (!whiteTurn && pieceKey.startsWith("BLACK"));
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

        // Force a redraw to clear ghost icons
        revalidate();
        repaint();
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
