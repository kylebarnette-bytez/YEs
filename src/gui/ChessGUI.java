package gui;

import javax.swing.*;
import java.awt.*;
import gui.*;

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
    private int whiteSeconds = 300;
    private int blackSeconds = 300;
    private boolean whiteTurn = true;

    /**
     * Constructs the main Chess GUI window,
     * initializing the board, side panels, and menus.
     */
    public ChessGUI() {
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 800);

        boardPanel = new BoardPanel(this);
        add(boardPanel, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        controlPanel.setMaximumSize(new Dimension(200, 120));

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

        statusLabel = new JLabel("White's Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JPanel timerPanel = new JPanel(new GridLayout(1, 2));
        whiteTimerLabel = new JLabel("White: 05:00", SwingConstants.CENTER);
        blackTimerLabel = new JLabel("Black: 05:00", SwingConstants.CENTER);
        whiteTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        blackTimerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerPanel.add(whiteTimerLabel);
        timerPanel.add(blackTimerLabel);
        add(timerPanel, BorderLayout.NORTH);
        add(statusLabel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem saveGameItem = new JMenuItem("Save Game");
        JMenuItem loadGameItem = new JMenuItem("Load Game");
        JMenuItem resetTimers = new JMenuItem("Reset Timers");

        resetTimers.addActionListener(e -> {
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

        gameMenu.add(resetTimers);
        gameMenu.add(newGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.add(loadGameItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        setLocationRelativeTo(null);
        startTurnTimer();
        setVisible(true);
    }

    /** Displays an endgame message when a king is captured. */
    public void showEndgameMessage(String winner) {
        JOptionPane.showMessageDialog(this,
                winner + " wins! Game over.",
                "Checkmate",
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /** Updates the status label to show the current game state. */
    public void updateStatus(String text) {
        statusLabel.setText(text);
    }

    /** Starts the countdown timer for the current player's turn. */
    private void startTurnTimer() {
        if (swingTimer != null && swingTimer.isRunning()) swingTimer.stop();

        swingTimer = new javax.swing.Timer(1000, e -> {
            if (whiteTurn) {
                whiteSeconds--;
                if (whiteSeconds <= 0) endGameOnTimeout("Black");
            } else {
                blackSeconds--;
                if (blackSeconds <= 0) endGameOnTimeout("White");
            }
            updateTimerLabels();
        });
        swingTimer.start();
    }

    /** Updates the visual display of both player timers. */
    private void updateTimerLabels() {
        whiteTimerLabel.setText("White: " + formatTime(whiteSeconds));
        blackTimerLabel.setText("Black: " + formatTime(blackSeconds));
    }

    /** Resets both timers and restarts the countdown for White. */
    public void resetTimers() {
        if (swingTimer != null) swingTimer.stop();
        whiteSeconds = 300;
        blackSeconds = 300;
        whiteTurn = true;
        updateTimerLabels();
        startTurnTimer();
    }

    /** Formats a time value in seconds into MM:SS format. */
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /** Ends the game if a player's timer runs out. */
    private void endGameOnTimeout(String winner) {
        swingTimer.stop();
        JOptionPane.showMessageDialog(this,
                "Time's up! " + winner + " wins!",
                "Timeout",
                JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    /**
     * Displays a temporary message in the status bar
     * that automatically clears after a short delay.
     */
    public void flashMessage(String text) {
        statusLabel.setText(text);
        new javax.swing.Timer(2000, e -> statusLabel.setText(whiteTurnText())) {{
            setRepeats(false);
            start();
        }};
    }

    /** Returns the current player's turn text. */
    private String whiteTurnText() {
        return boardPanel.isWhiteTurn() ? "White's Turn" : "Black's Turn";
    }

    /** Switches the active turn timer when control changes to the other player. */
    public void switchTurnTimer(boolean whiteTurnNow) {
        this.whiteTurn = whiteTurnNow;
        startTurnTimer();
    }
}
