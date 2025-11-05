package gui;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.*;
import java.awt.*;

/**
 * Represents the main chessboard panel responsible for rendering the 8×8 grid,
 * handling user interactions, piece movement, and communication with the GUI
 * and game history components.
 */
public class BoardPanel extends JPanel {

    private static final int BOARD_SIZE = 8;
    private final SquarePanel[][] squares = new SquarePanel[BOARD_SIZE][BOARD_SIZE];
    private SquarePanel selectedSquare = null;
    private boolean whiteTurn = true;
    private final Stack<MoveRecord> moveHistory = new Stack<>();
    private ChessGUI parentGUI;
    private GameHistoryPanel historyPanel;

    /**
     * Links this board to a {@link GameHistoryPanel} for displaying move history.
     *
     * @param panel the history panel to associate
     */
    public void setHistoryPanel(GameHistoryPanel panel) {
        this.historyPanel = panel;
    }

    /**
     * Indicates whether it is currently White's turn.
     *
     * @return {@code true} if White's turn; {@code false} if Black's turn
     */
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    /**
     * Creates a new {@code BoardPanel} with a parent GUI reference.
     * Initializes the layout, board squares, and starting pieces.
     *
     * @param parent the parent {@link ChessGUI} instance
     */
    public BoardPanel(ChessGUI parent) {
        this.parentGUI = parent;
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        initializePieces();
    }

    /** Initializes all 64 squares of the chessboard grid. */
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                SquarePanel square = new SquarePanel(row, col, this);
                squares[row][col] = square;
                add(square);
            }
        }
    }

    /** Places all chess pieces in their default starting positions. */
    private void initializePieces() {
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

    /**
     * Handles user clicks on a board square. Determines whether the action
     * is a piece selection, move attempt, or deselection.
     *
     * @param clicked the square that was clicked
     */
    public void handleSquareClick(SquarePanel clicked) {
        if (selectedSquare == null) {
            if (clicked.hasPiece() && isCorrectTurn(clicked.getPieceKey())) {
                clearHighlights();
                selectedSquare = clicked;
                if (parentGUI != null) {
                    parentGUI.flashMessage("Showing possible moves for " +
                            clicked.getPieceKey().replace("_", " "));
                }
                showPossibleMoves(clicked);
                clicked.setHighlighted(true);
            }
            return;
        }

        if (clicked == selectedSquare) {
            clearHighlights();
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        moveHistory.push(new MoveRecord(selectedSquare, clicked));

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

        if (clicked.hasPiece() && clicked.getPieceKey().contains("KING")) {
            String winner = whiteTurn ? "White" : "Black";
            clicked.setPiece(selectedSquare.getPieceKey());
            selectedSquare.clearPiece();
            if (parentGUI != null) parentGUI.showEndgameMessage(winner);
            return;
        }

        clicked.setPiece(movingPiece);
        selectedSquare.clearPiece();
        clearHighlights();
        selectedSquare.setHighlighted(false);
        selectedSquare = null;

        whiteTurn = !whiteTurn;
        if (parentGUI != null) {
            parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");
            parentGUI.switchTurnTimer(whiteTurn);
        }
    }

    /**
     * Determines whether the clicked piece belongs to the player whose turn it is.
     *
     * @param pieceKey the key representing the piece
     * @return {@code true} if the turn matches the piece color, otherwise {@code false}
     */
    private boolean isCorrectTurn(String pieceKey) {
        return (whiteTurn && pieceKey.startsWith("WHITE")) ||
               (!whiteTurn && pieceKey.startsWith("BLACK"));
    }

    /**
     * Highlights all legal moves for a given piece based on its type and position.
     *
     * @param fromSquare the square containing the selected piece
     */
    public void showPossibleMoves(SquarePanel fromSquare) {
        clearHighlights();
        String piece = fromSquare.getPieceKey();
        if (piece == null || piece.isEmpty()) return;

        int row = fromSquare.getRow();
        int col = fromSquare.getCol();

        if (piece.equals("WHITE_PAWN")) {
            if (row > 0 && !squares[row - 1][col].hasPiece())
                squares[row - 1][col].setHighlighted(true);
            if (row > 0 && col > 0 && squares[row - 1][col - 1].hasPiece() &&
                squares[row - 1][col - 1].getPieceKey().startsWith("BLACK"))
                squares[row - 1][col - 1].setHighlighted(true);
            if (row > 0 && col < 7 && squares[row - 1][col + 1].hasPiece() &&
                squares[row - 1][col + 1].getPieceKey().startsWith("BLACK"))
                squares[row - 1][col + 1].setHighlighted(true);
        } else if (piece.equals("BLACK_PAWN")) {
            if (row < 7 && !squares[row + 1][col].hasPiece())
                squares[row + 1][col].setHighlighted(true);
            if (row < 7 && col > 0 && squares[row + 1][col - 1].hasPiece() &&
                squares[row + 1][col - 1].getPieceKey().startsWith("WHITE"))
                squares[row + 1][col - 1].setHighlighted(true);
            if (row < 7 && col < 7 && squares[row + 1][col + 1].hasPiece() &&
                squares[row + 1][col + 1].getPieceKey().startsWith("WHITE"))
                squares[row + 1][col + 1].setHighlighted(true);
        } else if (piece.endsWith("KNIGHT")) {
            int[][] moves = {{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}};
            for (int[] m : moves) {
                int r = row + m[0], c = col + m[1];
                if (r >= 0 && r < 8 && c >= 0 && c < 8 && !isSameColor(r, c, piece))
                    squares[r][c].setHighlighted(true);
            }
        } else if (piece.endsWith("BISHOP") || piece.endsWith("QUEEN")) {
            int[][] dirs = {{1,1},{1,-1},{-1,1},{-1,-1}};
            slideMoves(row, col, dirs, piece);
        } else if (piece.endsWith("ROOK") || piece.endsWith("QUEEN")) {
            int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
            slideMoves(row, col, dirs, piece);
        } else if (piece.endsWith("KING")) {
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

    /**
     * Generates possible slide-type moves (used by rooks, bishops, and queens).
     *
     * @param row starting row
     * @param col starting column
     * @param dirs direction vectors
     * @param piece the moving piece
     */
    private void slideMoves(int row, int col, int[][] dirs, String piece) {
        for (int[] dir : dirs) {
            int r = row + dir[0], c = col + dir[1];
            while (r >= 0 && r < 8 && c >= 0 && c < 8) {
                if (squares[r][c].hasPiece()) {
                    if (!isSameColor(r, c, piece))
                        squares[r][c].setHighlighted(true);
                    break;
                }
                squares[r][c].setHighlighted(true);
                r += dir[0];
                c += dir[1];
            }
        }
    }

    /**
     * Checks whether a target square contains a piece of the same color.
     *
     * @param r target row
     * @param c target column
     * @param piece the moving piece
     * @return {@code true} if same color; {@code false} otherwise
     */
    private boolean isSameColor(int r, int c, String piece) {
        String target = squares[r][c].getPieceKey();
        if (target == null) return false;
        return (piece.startsWith("WHITE") && target.startsWith("WHITE")) ||
               (piece.startsWith("BLACK") && target.startsWith("BLACK"));
    }

    /** Removes all move highlights from the board. */
    private void clearHighlights() {
        for (SquarePanel[] rowArr : squares)
            for (SquarePanel sq : rowArr)
                sq.setHighlighted(false);
    }

    /**
     * Resets the board to its initial state, clearing all pieces,
     * move history, and highlights.
     */
    public void resetBoard() {
        for (SquarePanel[] row : squares)
            for (SquarePanel s : row) {
                s.clearPiece();
                s.setHighlighted(false);
            }

        initializePieces();
        whiteTurn = true;
        if (parentGUI != null)
            parentGUI.updateStatus("White's Turn");
        if (historyPanel != null)
            historyPanel.reset();

        revalidate();
        repaint();
        if (parentGUI != null) parentGUI.resetTimers();
    }

    /**
     * Saves the current game state (board, turn, and move history) to a file.
     * Displays a file chooser for user input.
     */
    public void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            GameState state = new GameState();

            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    state.board[row][col] = squares[row][col].getPieceKey();
                }
            }

            state.whiteTurn = this.whiteTurn;

            for (MoveRecord m : moveHistory) {
                state.moveHistory.add(new GameState.MoveData(
                        m.fromRow, m.fromCol, m.toRow, m.toCol, m.capturedPiece));
            }

            out.writeObject(state);
            JOptionPane.showMessageDialog(this, "Game saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads a previously saved game from file and restores the board, moves,
     * and turn state.
     */
    public void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            GameState state = (GameState) in.readObject();

            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    squares[row][col].setPiece(state.board[row][col]);
                }
            }

            this.whiteTurn = state.whiteTurn;
            if (parentGUI != null)
                parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");

            moveHistory.clear();
            for (GameState.MoveData md : state.moveHistory) {
                moveHistory.add(new MoveRecord(
                        squares[md.fromRow][md.fromCol],
                        squares[md.toRow][md.toCol],
                        md.capturedPiece));
            }

            revalidate();
            repaint();

            JOptionPane.showMessageDialog(this, "Game loaded successfully!", "Load", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Undoes the most recent move, restoring the previous board state.
     */
    public void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            MoveRecord last = moveHistory.pop();
            last.undo();
            whiteTurn = !whiteTurn;
            if (parentGUI != null)
                parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");
            if (historyPanel != null)
                historyPanel.removeLastMove();
        }
    }

    /** Represents a record of a single chess move, used for undo and save/load. */
    private static class MoveRecord {
        private final SquarePanel from, to;
        private final String capturedPiece;
        private final int fromRow, fromCol, toRow, toCol;

        MoveRecord(SquarePanel from, SquarePanel to) {
            this(from, to, to.getPieceKey());
        }

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

    /** Serializable container for game save data (board, turn, and move history). */
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
