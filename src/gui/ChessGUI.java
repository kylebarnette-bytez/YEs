package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main Chess GUI window.
 * Displays the chessboard, player timers, move history,
 * and provides user controls for saving, loading, and resetting the game.
 */
public class ChessGUI extends JFrame {

    private final BoardPanel boardPanel;
    private final GameHistoryPanel historyPanel;

    private final JLabel statusLabel;
    private JLabel whiteTimerLabel;
    private JLabel blackTimerLabel;

    private javax.swing.Timer swingTimer;

    // OLD TIMER SYSTEM — Each player has a total clock (5 minutes default)
    private int whiteSeconds = 300;
    private int blackSeconds = 300;

    // Tracks whose turn it is (controlled ONLY by the board / moves)
    private boolean whiteTurn = true;

    public ChessGUI() {

        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 800);

        // -------------------------
        // CENTER: Board
        // -------------------------
        boardPanel = new BoardPanel(this);
        add(boardPanel, BorderLayout.CENTER);

        // -------------------------
        // EAST: Undo, Exit, History
        // -------------------------
        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton undoBtn = new JButton("Undo Move");
        JButton exitBtn = new JButton("Exit");

        undoBtn.addActionListener(e -> boardPanel.undoLastMove());
        exitBtn.addActionListener(e -> System.exit(0));

        controlPanel.add(undoBtn);
        controlPanel.add(exitBtn);

        historyPanel = new GameHistoryPanel();
        boardPanel.setHistoryPanel(historyPanel);

        sidePanel.add(controlPanel, BorderLayout.NORTH);
        sidePanel.add(historyPanel, BorderLayout.CENTER);

        add(sidePanel, BorderLayout.EAST);

        // -------------------------
        // SOUTH: Status label
        // -------------------------
        statusLabel = new JLabel("White's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // -------------------------
        // NORTH: Timer bar
        // -------------------------
        JPanel timerPanel = new JPanel(new GridLayout(1, 2));

        whiteTimerLabel  = new JLabel("White: 05:00", SwingConstants.CENTER);
        blackTimerLabel  = new JLabel("Black: 05:00", SwingConstants.CENTER);

        whiteTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        blackTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        timerPanel.add(whiteTimerLabel);
        timerPanel.add(blackTimerLabel);

        add(timerPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);

        // -------------------------
        // MENU BAR
        // -------------------------
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        JMenuItem loadGameItem = new JMenuItem("Load Game");
        JMenuItem resetTimersItem = new JMenuItem("Reset Timers");

        resetTimersItem.addActionListener(e -> {
            whiteSeconds = 300;
            blackSeconds = 300;
            updateTimerLabels();
        });

        newGameItem.addActionListener(e -> {
            boardPanel.resetBoard();
            resetTimers();
        });

        saveGameItem.addActionListener(e -> boardPanel.saveGame());
        loadGameItem.addActionListener(e -> boardPanel.loadGame());

        gameMenu.add(resetTimersItem);
        gameMenu.add(newGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        setLocationRelativeTo(null);
        startTurnTimer();
        setVisible(true);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    /** Updates button/status bar messages. */
    public void updateStatus(String msg) {
        statusLabel.setText(msg);
    }

    // ======================================================
    //   OLD TIMER SYSTEM (White & Black countdown clocks)
    // ======================================================
    private void startTurnTimer() {

        if (swingTimer != null && swingTimer.isRunning())
            swingTimer.stop();

        swingTimer = new javax.swing.Timer(1000, e -> {

            if (whiteTurn) {
                whiteSeconds--;
                if (whiteSeconds <= 0) {
                    endGameOnTimeout("Black");
                    return;
                }
            } else {
                blackSeconds--;
                if (blackSeconds <= 0) {
                    endGameOnTimeout("White");
                    return;
                }
            }

            updateTimerLabels();
        });

        swingTimer.start();
    }

    /** Updates both timer labels (White & Black). */
    private void updateTimerLabels() {
        whiteTimerLabel.setText("White: " + formatTime(whiteSeconds));
        blackTimerLabel.setText("Black: " + formatTime(blackSeconds));
    }

    /** Reset both clocks (5:00) and restart timer for White. */
    public void resetTimers() {
        if (swingTimer != null) swingTimer.stop();

        whiteSeconds = 300;
        blackSeconds = 300;
        whiteTurn = true;

        updateTimerLabels();
        startTurnTimer();
    }

    /** Format seconds into MM:SS. */
    private String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    /** This is the old behavior — timeout ends the game immediately. */
    private void endGameOnTimeout(String winner) {
        if (swingTimer != null) swingTimer.stop();

        JOptionPane.showMessageDialog(
                this,
                "Time's up! " + winner + " wins!",
                "Timeout",
                JOptionPane.INFORMATION_MESSAGE
        );

        System.exit(0);
    }

    /**
     * Called by the board when a valid move occurs.
     * ONLY THIS should change turns — NOT the timer.
     */
    public void switchTurnTimer(boolean whiteTurnNow) {
        this.whiteTurn = whiteTurnNow;
        startTurnTimer();
    }

    public void showEndgameMessage(String winner) {
        if (swingTimer != null) swingTimer.stop();
        JOptionPane.showMessageDialog(
                this,
                winner + " wins! Game over.",
                "Checkmate",
                JOptionPane.INFORMATION_MESSAGE
        );
        System.exit(0);
    }

    /** Flash message then revert back. */
    public void flashMessage(String text) {
        statusLabel.setText(text);

        new javax.swing.Timer(2000, e ->
                statusLabel.setText(whiteTurn ? "White's Turn" : "Black's Turn")
        ) {{
            setRepeats(false);
            start();
        }};
    }
}
