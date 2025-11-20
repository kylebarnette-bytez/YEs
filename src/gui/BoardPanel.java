package gui;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import javax.swing.*;
import java.awt.*;

// Backend Imports
import board.Board;
import position.Position;
import pieces.Piece;
// note: DON'T import utils.Color; we'll use utils.Color fully qualified

/**
 * Represents the main chessboard panel responsible for rendering the 8×8 grid,
 * handling user interactions, piece movement, and communication with the GUI
 * and game history components.
 */
public class BoardPanel extends JPanel {

    private Board backendBoard;       // ← REAL chess engine board
    private boolean gameOver = false; // ← stop clicks after checkmate

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
        this.backendBoard = new Board(); // uses backend initializeBoard()

        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        syncBoardFromBackend(); // ← NEW: fill squares from backend instead of initializePieces()
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
    /**
     * Handles user clicks on a board square. Determines whether the action
     * is a piece selection, move attempt, or deselection.
     *
     * @param clicked the square that was clicked
     */
    public void handleSquareClick(SquarePanel clicked) {

        if (gameOver) return;

        int row = clicked.getRow();
        int col = clicked.getCol();
        Position clickedPos = new Position(row, col);
        Piece clickedPiece = backendBoard.getPiece(clickedPos);

        // 1) First click: select a piece
        if (selectedSquare == null) {
            if (clickedPiece == null) return;

            // must select current player's color
            if ((clickedPiece.getColor() == utils.Color.WHITE) != whiteTurn) {
                if (parentGUI != null) {
                    parentGUI.flashMessage("It's " + (whiteTurn ? "White" : "Black") + "'s turn.");
                }
                return;
            }

            clearHighlights();
            selectedSquare = clicked;

            if (parentGUI != null) {
                parentGUI.flashMessage("Showing possible moves for " +
                        pieceToKey(clickedPiece).replace("_", " "));
            }

            showPossibleMoves(clicked);
            clicked.setHighlighted(true);
            return;
        }

        // 2) Clicking the same square again → deselect
        if (clicked == selectedSquare) {
            clearHighlights();
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        // 3) Second click → attempt move
        Position from = new Position(selectedSquare.getRow(), selectedSquare.getCol());
        Position to   = clickedPos;

        Piece moving = backendBoard.getPiece(from);
        Piece destBefore = backendBoard.getPiece(to); // for capture history

        if (moving == null) {
            clearHighlights();
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        // Validate with backend rules
        boolean valid = backendBoard.validateMove(from, to);
        if (valid && backendBoard.movePutsPlayerInCheck(from, to, moving.getColor())) {
            valid = false;
        }

        if (!valid) {
            if (parentGUI != null) parentGUI.flashMessage("Invalid move");
            clearHighlights();
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        // Optional: still record GUI move for undo/save (GUI-level)
        moveHistory.push(new MoveRecord(selectedSquare, clicked));

        // Perform move in backend
        try {
            backendBoard.movePiece(from, to); // may throw if something is wrong
        } catch (IllegalArgumentException ex) {
            if (parentGUI != null) parentGUI.flashMessage("Invalid move: " + ex.getMessage());
            clearHighlights();
            selectedSquare.setHighlighted(false);
            selectedSquare = null;
            return;
        }

        // Sync GUI with backend board state
        syncBoardFromBackend();

        // Update history panel
        if (historyPanel != null) {
            String movingKey = pieceToKey(moving);
            String fromStr = "(" + from.getRow() + "," + from.getCol() + ")";
            String toStr   = "(" + to.getRow() + "," + to.getCol() + ")";
            historyPanel.addMove(movingKey + ": " + fromStr + " → " + toStr);
            if (destBefore != null) {
                String currentPlayer = whiteTurn ? "White" : "Black";
                historyPanel.addCapturedPiece(currentPlayer, pieceToKey(destBefore));
            }
        }

        // === ONLINE MULTIPLAYER STEP (send move after applying it locally) ===
        if (parentGUI != null &&
                parentGUI.isOnlineMode() &&
                parentGUI.getOnlineManager().isConnected()) {

            // Basic compact encoding: "r1,c1-r2,c2"
            String notation =
                    from.getRow() + "," + from.getCol() + "-" +
                            to.getRow() + "," + to.getCol();

            parentGUI.getOnlineManager().sendMove(notation);
        }
        // =====================================================================

        clearHighlights();
        selectedSquare.setHighlighted(false);
        selectedSquare = null;

        // 4) Check / Checkmate detection on opponent
        utils.Color opponentColor = whiteTurn ? utils.Color.BLACK : utils.Color.WHITE;

        if (backendBoard.isCheck(opponentColor)) {
            if (parentGUI != null) parentGUI.updateStatus("Check!");

            if (backendBoard.isCheckmate(opponentColor)) {
                gameOver = true;
                String winner = whiteTurn ? "White" : "Black";
                if (parentGUI != null) parentGUI.showEndgameMessage(winner);
                return;
            }
        }

        // 5) Switch turn + timers
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

        Position from = new Position(fromSquare.getRow(), fromSquare.getCol());
        Piece piece = backendBoard.getPiece(from);
        if (piece == null) return;

        java.util.List<Position> rawMoves = piece.possibleMoves(backendBoard);
        java.util.List<Position> legalMoves = new ArrayList<>();

        // Filter out moves that would leave this player in check
        utils.Color color = piece.getColor();
        for (Position to : rawMoves) {
            if (!backendBoard.movePutsPlayerInCheck(from, to, color)) {
                legalMoves.add(to);
            }
        }

        for (Position to : legalMoves) {
            squares[to.getRow()][to.getCol()].setHighlighted(true);
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
        backendBoard = new Board();   // reset backend model
        gameOver = false;
        whiteTurn = true;
        selectedSquare = null;
        moveHistory.clear();
        clearHighlights();

        syncBoardFromBackend();       // redraw GUI from backend

        if (parentGUI != null) {
            parentGUI.updateStatus("White's Turn");
            parentGUI.resetTimers();
        }
        if (historyPanel != null) {
            historyPanel.reset();
        }
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
    /** Converts a backend Piece into a GUI pieceKey like "WHITE_KING". */
    private String pieceToKey(Piece piece) {
        if (piece == null) return null;
        String colorPrefix = (piece.getColor() == utils.Color.WHITE) ? "WHITE_" : "BLACK_";
        String type = piece.getClass().getSimpleName().toUpperCase(); // KING, QUEEN, etc.
        return colorPrefix + type;
    }
    /** Copies the backend Board state into the GUI squares. */
    private void syncBoardFromBackend() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Piece p = backendBoard.getPiece(new Position(row, col));
                String key = pieceToKey(p);

                if (key == null) {
                    squares[row][col].clearPiece();
                } else {
                    squares[row][col].setPiece(key);
                }

                squares[row][col].setHighlighted(false);
            }
        }
        repaint();
    }


}
