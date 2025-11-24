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

/**
 * Represents the main chessboard panel responsible for rendering the 8×8 grid,
 * handling user interactions, piece movement, and communication with the GUI
 * and game history components.
 */
public class BoardPanel extends JPanel {

    /** Backend chess engine board (single source of truth). */
    private Board backendBoard;
    /** Flag to stop moves once game is over (checkmate). */
    private boolean gameOver = false;

    private static final int BOARD_SIZE = 8;
    private final SquarePanel[][] squares = new SquarePanel[BOARD_SIZE][BOARD_SIZE];

    /** Currently selected square (first click), or null if none. */
    private SquarePanel selectedSquare = null;

    /** True if it's White's turn, false if Black's. */
    private boolean whiteTurn = true;

    /** GUI-level move history (used only for save/load display). */
    private final Stack<MoveRecord> moveHistory = new Stack<>();

    /** Backend move history for proper undo. */
    private final Stack<BackendMove> backendHistory = new Stack<>();

    /** Reference to parent GUI for status/timers. */
    private ChessGUI parentGUI;

    /** Optional panel that displays move history and captures. */
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
     * Record of a backend move used for true undo of engine state.
     * Stores cloned copies of the pieces so their internal state (like pawn firstMove)
     * can be fully restored.
     */
    private static class BackendMove {
        final Position from;
        final Position to;
        final Piece movedPiece;     // cloned copy before move
        final Piece capturedPiece;  // cloned copy of captured piece (or null)

        BackendMove(Position from, Position to, Piece movedPiece, Piece capturedPiece) {
            this.from = from;
            this.to = to;
            this.movedPiece = movedPiece;
            this.capturedPiece = capturedPiece;
        }
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
     * Initializes the layout, board squares, and starting pieces
     * by syncing from the backend board.
     *
     * @param parent the parent {@link ChessGUI} instance
     */
    public BoardPanel(ChessGUI parent) {
        this.parentGUI = parent;
        this.backendBoard = new Board(); // backend initializes standard position

        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard();
        syncBoardFromBackend();
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
            // must click on a piece
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

        // 3) Second click → attempt move from selectedSquare to clicked
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

        backendHistory.clear();   // <-- allow undo only for the *current* move

        // --- Record backend move for true undo ---
        // Clone moving piece BEFORE mutation
        Piece movingCopy = backendBoard.clonePiece(moving, moving.getPosition());
        // Clone captured piece (if any)
        Piece capturedCopy = (destBefore == null)
                ? null
                : backendBoard.clonePiece(destBefore, destBefore.getPosition());
        backendHistory.push(new BackendMove(from, to, movingCopy, capturedCopy));

        // Record GUI-level move for save/load (but GUI visuals are always driven by backend)
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

        // Update history panel (sidebar)
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

    /** Removes all move highlights from the board. */
    private void clearHighlights() {
        for (SquarePanel[] rowArr : squares)
            for (SquarePanel sq : rowArr)
                sq.setHighlighted(false);
    }

    /**
     * Highlights all legal moves for a given piece based on backend rules.
     *
     * @param fromSquare the square containing the selected piece
     */
    public void showPossibleMoves(SquarePanel fromSquare) {
        clearHighlights();

        Position from = new Position(fromSquare.getRow(), fromSquare.getCol());
        Piece piece = backendBoard.getPiece(from);
        if (piece == null) return;

        List<Position> rawMoves = piece.possibleMoves(backendBoard);
        List<Position> legalMoves = new ArrayList<>();

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
     * Resets the board to its initial state, clearing all pieces,
     * move history, highlights, and timers.
     */
    public void resetBoard() {
        backendBoard = new Board();   // reset backend model
        gameOver = false;
        whiteTurn = true;
        selectedSquare = null;
        moveHistory.clear();
        backendHistory.clear();
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
     * Saves the current game state (board and turn and moveHistory) to a file.
     * Note: this currently saves based on GUI piece keys, not backend state.
     */
    public void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            GameState state = new GameState();

            // Save board as piece-key strings
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    state.board[row][col] = squares[row][col].getPieceKey();
                }
            }

            state.whiteTurn = this.whiteTurn;

            // Save GUI move history (for possible future replay)
            for (MoveRecord m : moveHistory) {
                state.moveHistory.add(new GameState.MoveData(
                        m.fromRow, m.fromCol, m.toRow, m.toCol, m.capturedPiece));
            }

            out.writeObject(state);
            JOptionPane.showMessageDialog(this, "Game saved successfully!",
                    "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving game: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads a previously saved game from file and restores the board, moves,
     * and turn state.
     *
     * NOTE: This version restores GUI squares and moveHistory,
     * but does NOT fully reconstruct backendBoard from the save.
     * A full backend reconstruction requires more work in Board.java.
     */
    public void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            GameState state = (GameState) in.readObject();

            // Restore GUI from saved board
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    squares[row][col].setPiece(state.board[row][col]);
                }
            }

            // TODO (better): Rebuild backendBoard from state.board piece keys
            // and call syncBoardFromBackend().

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

            JOptionPane.showMessageDialog(this, "Game loaded successfully!",
                    "Load", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading game: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Undoes the most recent move, restoring the backend board state
     * and re-syncing the GUI.
     */
    public void undoLastMove() {
        // 1) We can only undo if backend has moves
        if (backendHistory.isEmpty()) {
            if (parentGUI != null) parentGUI.flashMessage("No moves left to undo.");
            return;
        }

        // 2) Pop backend move first (REAL undo)
        BackendMove lastBackend = backendHistory.pop();
        backendBoard.undoMove(
                lastBackend.from,
                lastBackend.to,
                lastBackend.movedPiece,
                lastBackend.capturedPiece
        );

        // 3) Resync GUI to backend
        syncBoardFromBackend();
        clearHighlights();
        selectedSquare = null;

        // 4) Keep the history panel in sync visually
        if (historyPanel != null) {
            historyPanel.removeLastMove();
        }

        // 5) Flip turn and update status/timer
        whiteTurn = !whiteTurn;
        if (parentGUI != null) {
            parentGUI.updateStatus(whiteTurn ? "White's Turn" : "Black's Turn");
            parentGUI.switchTurnTimer(whiteTurn);
        }
    }

    /** Represents a record of a single chess move, used for save/load. */
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
